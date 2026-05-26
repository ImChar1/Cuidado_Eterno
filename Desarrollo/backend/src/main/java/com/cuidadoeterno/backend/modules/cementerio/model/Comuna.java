package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMUNA")
@Getter
@Setter
@NoArgsConstructor
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comuna", nullable = false, updatable = false)
    private Integer idComuna;

    @Column(name = "nombre_comuna", nullable = false, length = 50)
    private String nombreComuna;

    // Relación: Muchas Comunas pertenecen a una Provincia
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_provincia", nullable = false)
    private Provincia provincia;
}