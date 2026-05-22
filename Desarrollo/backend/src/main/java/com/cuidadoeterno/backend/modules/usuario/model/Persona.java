package com.cuidadoeterno.backend.modules.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tabla: PERSONA
 * Entidad padre de todos los actores del sistema.
 * CLIENTE, CUIDADOR y ADMINISTRADOR extienden esta tabla
 * usando herencia de tipo JOINED (tabla separada por subtipo).
 *
 * Relaciones:
 * - OneToOne  → CREDENCIAL (cada persona tiene una credencial única)
 * - OneToOne  ← CLIENTE / CUIDADOR / ADMINISTRADOR (subtipos)
 */
@Entity
@Table(name = "PERSONA")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona", nullable = false, updatable = false)
    private Integer idPersona;

    @Column(name = "rut", nullable = false, unique = true, length = 12)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @Column(name = "ap_paterno", nullable = false, length = 50)
    private String apPaterno;

    /**
     * Apellido materno es opcional (nullable = true por defecto).
     */
    @Column(name = "ap_materno", length = 50)
    private String apMaterno;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * 'M' = Masculino, 'F' = Femenino, 'O' = Otro
     */
    @Column(name = "genero", nullable = false, length = 1)
    private String genero;

    // ── Relaciones ──────────────────────────────────────────────────────────────

    /**
     * FK → CREDENCIAL.id_credencial
     * unique = true garantiza que una credencial no pueda asignarse a dos personas.
     * CascadeType.ALL: al guardar/eliminar una Persona se opera también su Credencial.
     */
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_credencial", nullable = false, unique = true)
    private Credencial credencial;
}