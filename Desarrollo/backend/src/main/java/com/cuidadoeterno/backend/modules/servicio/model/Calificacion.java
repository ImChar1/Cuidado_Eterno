package com.cuidadoeterno.backend.modules.servicio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "calificacion")
@Getter
@Setter
@NoArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion", nullable = false, updatable = false)
    private Integer idCalificacion;

    // Se mapea a TINYINT en base de datos para valores de 1 a 5 estrellas
    @Column(name = "puntuacion", nullable = false)
    private Integer puntuacion;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "fecha_calificacion", nullable = false, updatable = false)
    private LocalDateTime fechaCalificacion = LocalDateTime.now();

    // Relación: Se califica una Orden/Servicio en específico
    @OneToOne(fetch = FetchType.LAZY) // Usualmente es 1 calificación por orden
    @JoinColumn(name = "id_orden", nullable = false, unique = true)
    private DetalleOrden detalleOrden;
}