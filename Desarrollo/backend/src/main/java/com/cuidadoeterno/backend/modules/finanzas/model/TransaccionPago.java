package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACCION_PAGO")
@Getter
@Setter
@NoArgsConstructor
public class TransaccionPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion_pago", nullable = false, updatable = false)
    private Integer idTransaccionPago;

    // Relación con tu tabla de pagos
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaccion", nullable = false)
    private PagoSolicitud pagoSolicitud;

    // --- Datos de inicio ---
    @Column(name = "token_ws", nullable = false, length = 64, unique = true)
    private String tokenWs;

    @Column(name = "orden_compra", nullable = false, length = 26, unique = true)
    private String ordenCompra;

    @Column(name = "session_id", nullable = false, length = 61)
    private String sessionId;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "url_retorno", nullable = false, length = 500)
    private String urlRetorno;

    @Column(name = "url_webpay", length = 500)
    private String urlWebpay;

    @Column(name = "estado_transaccion", nullable = false, length = 20)
    private String estadoTransaccion = "iniciada";

    // --- Datos de confirmación (Commit) ---
    @Column(name = "vci", length = 6)
    private String vci;

    @Column(name = "response_code")
    private Short responseCode;

    @Column(name = "tipo_pago", length = 5)
    private String tipoPago;

    @Column(name = "numero_cuotas")
    private Integer numeroCuotas;

    @Column(name = "monto_cuota", precision = 10, scale = 2)
    private BigDecimal montoCuota;

    @Column(name = "codigo_autorizacion", length = 6)
    private String codigoAutorizacion;

    @Column(name = "ultimos_4_digitos", columnDefinition = "CHAR(4)", length = 4)
    private String ultimos4Digitos;

    @Column(name = "numero_tarjeta", length = 19)
    private String numeroTarjeta;

    @Column(name = "tipo_tarjeta", length = 10)
    private String tipoTarjeta;

    @Column(name = "fecha_transaccion_tbk")
    private LocalDateTime fechaTransaccionTbk;

    @Column(name = "fecha_contable")
    private LocalDate fechaContable;

    // --- Datos de Anulación ---
    @Column(name = "es_anulacion", nullable = false)
    private Boolean esAnulacion = false;

    @Column(name = "monto_anulacion", precision = 10, scale = 2)
    private BigDecimal montoAnulacion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "token_anulacion", length = 64)
    private String tokenAnulacion;

    @Column(name = "codigo_accion_anulacion", length = 6)
    private String codigoAccionAnulacion;

    // --- Info del Comercio y Auditoría ---
    @Column(name = "codigo_comercio", length = 12)
    private String codigoComercio;

    @Column(name = "codigo_tienda", length = 12)
    private String codigoTienda;

    @Column(name = "ambiente", nullable = false, length = 10)
    private String ambiente = "produccion";

    @Column(name = "ip_cliente", length = 45)
    private String ipCliente;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Mapeo JSON a String
    @Column(name = "payload_respuesta", columnDefinition = "json")
    private String payloadRespuesta;

    @Column(name = "intentos_confirmacion", nullable = false)
    private Integer intentosConfirmacion = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    // Actualiza la fecha automáticamente antes de guardar cambios
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}