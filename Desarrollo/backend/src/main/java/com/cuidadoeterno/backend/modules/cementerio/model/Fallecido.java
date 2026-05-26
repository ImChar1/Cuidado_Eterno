package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(name = "fecha_defuncion")
    private LocalDate fechaDefuncion;

    // Relación: Un fallecido descansa en un Espacio específico
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_espacio", nullable = false)
    private Espacio espacio;
}