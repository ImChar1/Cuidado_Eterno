package com.cuidadoeterno.backend.modules.servicio.model;

import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta; // Relación cruzada con Inventario
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "retiro_insumo")
@Getter
@Setter
@NoArgsConstructor
public class RetiroInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_retiro", nullable = false, updatable = false)
    private Integer idRetiro;

    @Column(name = "fecha_retiro", nullable = false)
    private LocalDateTime fechaRetiro;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "url_boleta_foto", length = 500)
    private String urlBoletaFoto;

    @Column(name = "estado_retiro", nullable = false, length = 20)
    private String estadoRetiro = "pendiente"; // pendiente, retirado, rechazado

    // Relación: Muchos retiros de insumos se asocian a una sola Orden de trabajo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private DetalleOrden detalleOrden;

    // Relación: El retiro físico se realiza en un Puesto de Venta específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_puesto", nullable = false)
    private PuestoVenta puestoVenta;
}