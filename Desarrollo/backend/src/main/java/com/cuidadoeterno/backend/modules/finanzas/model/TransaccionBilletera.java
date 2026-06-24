package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cuidadoeterno.backend.modules.finanzas.model.enums.TipoMovimiento;

@Entity
@Table(name = "transaccion_billetera")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionBilletera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tx_billetera")
    private Integer idTxBilletera;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_billetera", nullable = false)
    private BilleteraVirtual billetera;

    // Solo guardamos el ID para evitar dependencias circulares complejas con el módulo de servicio
    @Column(name = "id_orden")
    private Integer idOrden; 

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private TipoMovimiento tipoMovimiento; // "ABONO" o "RETIRO"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private String descripcion;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;
}
