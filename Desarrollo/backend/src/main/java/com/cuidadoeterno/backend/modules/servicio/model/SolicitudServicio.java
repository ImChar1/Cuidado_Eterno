package com.cuidadoeterno.backend.modules.servicio.model;

import com.cuidadoeterno.backend.modules.finanzas.model.PagoSolicitud;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SOLICITUD_SERVICIO")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud", nullable = false, updatable = false)
    private Integer idSolicitud;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_solicitud", nullable = false)
    private TipoSolicitud tipoSolicitud;

    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    // Monto total que debe pagar el cliente por esta solicitud
    @Column(name = "total_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCompra = BigDecimal.ZERO;

    // Estado del flujo: pendiente | pagada | en_proceso | completada | cancelada
    @Column(name = "estado_solicitud", nullable = false, length = 20)
    private String estadoSolicitud = "pendiente";

    // Instrucciones adicionales del cliente
    @Column(name = "instrucciones", length = 255)
    private String instrucciones;

    // Relación con el pago asociado (se asigna al iniciar el pago)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transaccion")
    private PagoSolicitud pagoSolicitud; // ¡Cambio clave aquí!

    // Conveniencia para el servicio: expone el id sin cargar la entidad completa
    // Le cambiamos el nombre para que no pelee con el Getter de Lombok
    public Integer getTransaccionId() {
        return pagoSolicitud != null ? pagoSolicitud.getIdTransaccion() : null;
    }
}