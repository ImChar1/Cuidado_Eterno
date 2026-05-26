package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CEMENTERIO")
@Getter
@Setter
@NoArgsConstructor
public class Cementerio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cementerio", nullable = false, updatable = false)
    private Integer idCementerio;

    @Column(name = "nombre_cementerio", nullable = false, length = 100)
    private String nombreCementerio;

    @Column(name = "direccion", nullable = false, length = 200)
    private String direccion;

    @Column(name = "telefono_contacto", length = 20)
    private String telefonoContacto;

    // Relación: Muchos Cementerios están en una Comuna
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comuna", nullable = false)
    private Comuna comuna;

    // Relación: Muchos Cementerios pueden compartir un Horario base
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;
}