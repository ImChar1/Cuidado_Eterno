package com.cuidadoeterno.backend.modules.servicio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPO_SOLICITUD")
@Getter
@Setter
@NoArgsConstructor
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_solicitud", nullable = false, updatable = false)
    private Integer idTipoSolicitud;

    @Column(name = "nombre_servicio", nullable = false, length = 100)
    private String nombreServicio;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "duracion_estimada_min", nullable = false)
    private Short duracionEstimadaMin;

    @Column(name = "requiere_insumos", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean requiereInsumos = false;

    @Column(name = "estado_sv", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean estadoSv = true;
}