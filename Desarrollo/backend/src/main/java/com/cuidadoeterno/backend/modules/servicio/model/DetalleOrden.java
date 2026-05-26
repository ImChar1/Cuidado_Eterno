package com.cuidadoeterno.backend.modules.servicio.model;

import com.cuidadoeterno.backend.modules.cementerio.model.Espacio; // Relación cruzada
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DETALLE_ORDEN")
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

    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "estado_orden", nullable = false, length = 20)
    private String estadoOrden = "pendiente"; // pendiente, pagada, en_proceso, completada, cancelada

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // Relación obligatoria según CONSTRAINT _ESPACIO_FK del script
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;
    
    // Nota: Aquí se mapearán posteriormente las relaciones con id_usuario (cliente) 
    // e id_trabajador (cuidador) según la lógica de tu módulo de usuarios.
}