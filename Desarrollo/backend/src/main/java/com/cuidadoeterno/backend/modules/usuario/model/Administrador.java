package com.cuidadoeterno.backend.modules.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tabla: ADMINISTRADOR
 * Subtipo de PERSONA. Gestiona el sistema y los cementerios asignados.
 */
@Entity
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "id_persona")
@Getter
@Setter
@NoArgsConstructor
public class Administrador extends Persona {

    /**
     * Nivel de acceso al sistema: 'total', 'parcial'
     */
    @Column(name = "nivel_acceso", nullable = false, length = 30)
    private String nivelAcceso;

    @Column(name = "cargo", nullable = false, length = 50)
    private String cargo;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;
}