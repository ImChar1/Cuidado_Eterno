package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_cuidador")
@Getter
@Setter
@NoArgsConstructor
public class PagoCuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_cuidador", nullable = false, updatable = false)
    private Integer idPagoCuidador;

    // Relacionamos con el identificador de la cuenta bancaria del trabajador 
    // (Esta tabla CUENTA_BANCO probablemente la mapeemos en el módulo de usuario)
    @Column(name = "id_cuenta", nullable = false)
    private Integer idCuenta;

    // Relación: Qué método usamos para pagarle (Ej: Transferencia Bancaria)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "estado_pago", nullable = false, length = 20)
    private String estadoPago = "pendiente"; // pendiente, procesando, transferido

    // ID que te entrega tu banco al hacerle la transferencia manual o automática al trabajador
    @Column(name = "id_transaccion_ext", length = 100)
    private String idTransaccionExt;

    // URL de la foto/pdf del comprobante de transferencia (para que el cuidador lo vea en su app)
    @Column(name = "comprobante_url", length = 500)
    private String comprobanteUrl;
}