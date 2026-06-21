package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Tabla: HORARIO
 * Tabla normalizada que cubre tanto el horario del cementerio
 * como el horario del cuidador (que depende del horario del cementerio).
 *
 * Relaciones inversas (no declaradas aquí para evitar carga innecesaria):
 * - CEMENTERIO tiene FK → HORARIO
 * - CUIDADOR   tiene FK → HORARIO
 */
@Entity
@Table(name = "horario")
@Getter
@Setter
@NoArgsConstructor
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario", nullable = false, updatable = false)
    private Integer idHorario;

    /**
     * 'lunes', 'martes', ..., 'domingo'
     */
    @Column(name = "dia_semana", nullable = false, length = 12)
    private String diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    /**
     * 1 = disponible, 0 = no disponible
     */
    @Column(name = "estado_disponibilidad", nullable = false)
    private Boolean estadoDisponibilidad = true;
}