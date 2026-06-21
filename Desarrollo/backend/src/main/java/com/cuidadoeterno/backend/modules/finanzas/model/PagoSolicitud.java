package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_solicitud")
@Getter
@Setter
@NoArgsConstructor
public class PagoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaccion", nullable = false, updatable = false)
    private Integer idTransaccion;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago = "iniciado";

    // Datos replicados para consultas rápidas sin tener que cruzar con TRANSACCION_PAGO
    @Column(name = "token_transbank", length = 70)
    private String tokenTransbank;

    @Column(name = "codigo_autorizacion", length = 10)
    private String codigoAutorizacion;

    @Column(name = "response_code")
    private Short responseCode;

    @Column(name = "tipo_pago_transbank", length = 5)
    private String tipoPagoTransbank;

    @Column(name = "cuotas")
    private Integer cuotas;

    @Column(name = "ultimos_4_digitos", columnDefinition = "CHAR(4)", length = 4)
    private String ultimos4Digitos;

    @Column(name = "fecha_transaccion")
    private LocalDateTime fechaTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    private TipoPago tipoPago;
}