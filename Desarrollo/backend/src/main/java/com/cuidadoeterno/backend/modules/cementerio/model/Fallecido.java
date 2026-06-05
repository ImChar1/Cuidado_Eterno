package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "FALLECIDO")
@Getter
@Setter
@NoArgsConstructor
public class Fallecido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_fallecido", nullable = false, updatable = false)
    private Integer idFallecido;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 150)
    private String apellidos;

    @Column(name = "fecha_nacimiento", nullable = false, length = 150)
    private LocalDate fechaDeNacimiento;

    @Column(name = "fecha_defuncion")
    private LocalDate fechaDefuncion;

    @Column(name = "epitafo", length = 255)
    private String epitafo;

    // Relación: Un fallecido descansa en un Espacio específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;
}