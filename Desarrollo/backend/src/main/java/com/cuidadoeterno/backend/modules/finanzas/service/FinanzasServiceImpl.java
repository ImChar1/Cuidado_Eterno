package com.cuidadoeterno.backend.modules.finanzas.service;

import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import com.cuidadoeterno.backend.modules.finanzas.dto.*;
import com.cuidadoeterno.backend.modules.finanzas.model.PagoSolicitud;
import com.cuidadoeterno.backend.modules.finanzas.model.TipoPago;
import com.cuidadoeterno.backend.modules.finanzas.model.TransaccionPago;
import com.cuidadoeterno.backend.modules.finanzas.repository.PagoSolicitudRepository;
import com.cuidadoeterno.backend.modules.finanzas.repository.TipoPagoRepository;
import com.cuidadoeterno.backend.modules.finanzas.repository.TransaccionPagoRepository;
import com.cuidadoeterno.backend.modules.servicio.model.SolicitudServicio;
import com.cuidadoeterno.backend.modules.servicio.repository.SolicitudServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanzasServiceImpl implements FinanzasService {

    private final PagoSolicitudRepository pagoSolicitudRepository;
    private final TransaccionPagoRepository transaccionPagoRepository;
    private final TipoPagoRepository tipoPagoRepository;
    private final SolicitudServicioRepository solicitudServicioRepository;

    private final WebpayOptions webpayOptions;

    @Override
    @Transactional
    public WebpayInitResponseDTO iniciarPagoWebpay(WebpayInitRequestDTO request) {
        SolicitudServicio solicitud = solicitudServicioRepository.findById(request.getIdSolicitud())
                .orElseThrow(() -> new IllegalArgumentException("Solicitud de servicio no encontrada"));

        TipoPago tipoWebpay = tipoPagoRepository.findByNombreMetodo("Webpay")
                .orElseThrow(() -> new IllegalStateException("Método de pago Webpay no configurado en BD"));

        // 1. Crear y persistir PAGO_SOLICITUD para obtener su ID generado
        PagoSolicitud pagoSolicitud = new PagoSolicitud();
        pagoSolicitud.setTipoPago(tipoWebpay);
        pagoSolicitud.setMontoTotal(solicitud.getTotalCompra());
        pagoSolicitud.setEstadoPago("iniciado");
        pagoSolicitud = pagoSolicitudRepository.save(pagoSolicitud);

        String ordenCompra = "ORD-" + pagoSolicitud.getIdTransaccion() + "-" + System.currentTimeMillis();
        String sessionId   = UUID.randomUUID().toString();
        double amount      = solicitud.getTotalCompra().doubleValue();

        try {
            WebpayPlus.Transaction tx = new WebpayPlus.Transaction(webpayOptions);
            var tbkResponse = tx.create(ordenCompra, sessionId, amount, request.getReturnUrl());

            // 2. Registrar la transacción Webpay completa en TRANSACCION_PAGO
            TransaccionPago transaccionPago = new TransaccionPago();
            transaccionPago.setPagoSolicitud(pagoSolicitud);
            transaccionPago.setTokenWs(tbkResponse.getToken());
            transaccionPago.setOrdenCompra(ordenCompra);
            transaccionPago.setSessionId(sessionId);
            transaccionPago.setMonto(solicitud.getTotalCompra());
            transaccionPago.setUrlRetorno(request.getReturnUrl());
            transaccionPago.setUrlWebpay(tbkResponse.getUrl());
            transaccionPago.setEstadoTransaccion("iniciada");
            transaccionPagoRepository.save(transaccionPago);

            // 3. Vincular SOLICITUD_SERVICIO → PAGO_SOLICITUD usando la relación correcta
            solicitud.getTransaccionId();
            solicitudServicioRepository.save(solicitud);

            return new WebpayInitResponseDTO(tbkResponse.getToken(), tbkResponse.getUrl());

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con Transbank: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public WebpayCommitResponseDTO confirmarPagoWebpay(String tokenWs) {
        TransaccionPago transaccion = transaccionPagoRepository.findByTokenWs(tokenWs)
                .orElseThrow(() -> new IllegalArgumentException("Token de transacción inválido o no encontrado"));

        PagoSolicitud pagoSolicitud = transaccion.getPagoSolicitud();

        try {
            WebpayPlus.Transaction tx = new WebpayPlus.Transaction(webpayOptions);
            var tbkResponse = tx.commit(tokenWs);

            boolean isAprobado   = "AUTHORIZED".equals(tbkResponse.getStatus());
            short responseCodeVal = (short) (isAprobado ? 0 : 1);

            // Extraer últimos 4 dígitos de forma segura
            String ultimos4 = (tbkResponse.getCardDetail() != null)
                    ? tbkResponse.getCardDetail().getCardNumber()
                    : "";

            // 4. Actualizar TRANSACCION_PAGO con la respuesta del commit
            transaccion.setResponseCode(responseCodeVal);
            transaccion.setVci(tbkResponse.getVci());
            transaccion.setCodigoAutorizacion(tbkResponse.getAuthorizationCode());
            transaccion.setTipoPago(tbkResponse.getPaymentTypeCode());
            transaccion.setNumeroCuotas((int) tbkResponse.getInstallmentsNumber());
            transaccion.setUltimos4Digitos(ultimos4);
            transaccion.setEstadoTransaccion(isAprobado ? "autorizada" : "rechazada");
            transaccion.setIntentosConfirmacion(transaccion.getIntentosConfirmacion() + 1);
            transaccionPagoRepository.save(transaccion);

            // 5. Reflejar resultado en PAGO_SOLICITUD
            pagoSolicitud.setEstadoPago(isAprobado ? "completado" : "rechazado");
            pagoSolicitud.setCodigoAutorizacion(tbkResponse.getAuthorizationCode());
            pagoSolicitud.setUltimos4Digitos(ultimos4);
            pagoSolicitud.setResponseCode(responseCodeVal);
            pagoSolicitud.setFechaTransaccion(LocalDateTime.now());
            pagoSolicitudRepository.save(pagoSolicitud);

            // 6. Actualizar estado de SOLICITUD_SERVICIO si el pago fue aprobado
            //    Buscamos por la entidad PagoSolicitud (campo id_transaccion en BD)
            if (isAprobado) {
                solicitudServicioRepository
                        .findByPagoSolicitudIdTransaccion(pagoSolicitud.getIdTransaccion())
                        .ifPresent(solicitud -> {
                            solicitud.setEstadoSolicitud("pagada");
                            solicitudServicioRepository.save(solicitud);
                        });
            }

            return WebpayCommitResponseDTO.builder()
                    .status(tbkResponse.getStatus())
                    .buyOrder(tbkResponse.getBuyOrder())
                    .authorizationCode(tbkResponse.getAuthorizationCode())
                    .amount(BigDecimal.valueOf(tbkResponse.getAmount()))
                    .cardNumber(ultimos4)
                    .transactionDate(String.valueOf(tbkResponse.getTransactionDate()))
                    .build();

        } catch (Exception e) {
            transaccion.setEstadoTransaccion("abortada_o_error");
            pagoSolicitud.setEstadoPago("fallido");
            transaccionPagoRepository.save(transaccion);
            pagoSolicitudRepository.save(pagoSolicitud);
            throw new RuntimeException("Error al confirmar el pago con Transbank: " + e.getMessage(), e);
        }
    }
}