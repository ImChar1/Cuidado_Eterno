package com.cuidadoeterno.backend.modules.servicio.model;

import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.inventario.model.Producto;
import com.cuidadoeterno.backend.modules.servicio.model.enums.SubEstadoOrden;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.modules.finanzas.model.PagoCuidador;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "detalle_orden")
@Getter
@Setter
@NoArgsConstructor
public class DetalleOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden", nullable = false, updatable = false)
    private Integer idOrden;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "estado_orden", nullable = false, length = 20)
    private String estadoOrden = "pendiente";

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "cantidad_productos")
    private Integer cantidadProductos;

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_estado_orden", nullable = false)
    private SubEstadoOrden subEstadoOrden = SubEstadoOrden.SIN_ASIGNAR;

    // ── Relaciones ──────────────────────────────────────────────────────────────

    // Espacio (tumba) donde se realiza el servicio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private SolicitudServicio solicitudServicio;

    // Cuidador asignado a esta orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona_cuidador")
    private Cuidador cuidador;

    // Producto asociado a la orden (nullable: el cliente puede no pedir insumos)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    // Pago al cuidador una vez completado el servicio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago_cuidador")
    private PagoCuidador pagoCuidador;
}