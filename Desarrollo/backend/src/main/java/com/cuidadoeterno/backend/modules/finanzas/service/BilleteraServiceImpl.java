package com.cuidadoeterno.backend.modules.finanzas.service;

import com.cuidadoeterno.backend.modules.finanzas.model.BilleteraVirtual;
import com.cuidadoeterno.backend.modules.finanzas.model.TransaccionBilletera;
import com.cuidadoeterno.backend.modules.finanzas.model.enums.TipoMovimiento;
import com.cuidadoeterno.backend.modules.finanzas.repository.BilleteraVirtualRepository;
import com.cuidadoeterno.backend.modules.finanzas.repository.TransaccionBilleteraRepository;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.modules.usuario.repository.CuidadorRepository;
import com.cuidadoeterno.backend.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BilleteraServiceImpl implements BilleteraService {

    private final BilleteraVirtualRepository billeteraRepository;
    private final TransaccionBilleteraRepository transaccionRepository;
    private final CuidadorRepository cuidadorRepository;

    public BilleteraServiceImpl(
            BilleteraVirtualRepository billeteraRepository,
            TransaccionBilleteraRepository transaccionRepository,
            CuidadorRepository cuidadorRepository) {
        this.billeteraRepository = billeteraRepository;
        this.transaccionRepository = transaccionRepository;
        this.cuidadorRepository = cuidadorRepository;
    }

    @Override
    @Transactional // CRÍTICO: Si falla guardar la transacción, no se suma el saldo
    public void abonarPagoPorServicio(Integer idCuidador, Integer idOrden, BigDecimal montoAbono) {
        
        // 1. Buscamos la billetera del cuidador
        BilleteraVirtual billetera = billeteraRepository.findByCuidador_IdPersona(idCuidador)
                .orElseGet(() -> crearBilleteraPorDefecto(idCuidador)); // Si no tiene, se la creamos en el momento

        // 2. Sumamos el dinero al saldo disponible
        BigDecimal saldoActual = billetera.getSaldoDisponible();
        billetera.setSaldoDisponible(saldoActual.add(montoAbono));
        billetera.setFechaActualizacion(LocalDateTime.now());
        billeteraRepository.save(billetera);

        // 3. Dejamos el registro histórico (El "voucher" o comprobante)
        TransaccionBilletera tx = TransaccionBilletera.builder()
                .billetera(billetera)
                .idOrden(idOrden)
                .tipoMovimiento(TipoMovimiento.ABONO)
                .monto(montoAbono)
                .descripcion("Pago por finalización de Orden #" + idOrden)
                .fechaTransaccion(LocalDateTime.now())
                .build();
        
        transaccionRepository.save(tx);
    }

    // Método auxiliar robusto: Si un cuidador antiguo no tenía billetera en la BD, se la creamos vacía
    private BilleteraVirtual crearBilleteraPorDefecto(Integer idCuidador) {
        Cuidador cuidador = cuidadorRepository.findById(idCuidador)
                .orElseThrow(() -> new BusinessException("Cuidador no encontrado", HttpStatus.NOT_FOUND));

        BilleteraVirtual nuevaBilletera = BilleteraVirtual.builder()
                .cuidador(cuidador)
                .saldoDisponible(BigDecimal.ZERO)
                .fechaActualizacion(LocalDateTime.now())
                .build();
        
        return billeteraRepository.save(nuevaBilletera);
    }
}