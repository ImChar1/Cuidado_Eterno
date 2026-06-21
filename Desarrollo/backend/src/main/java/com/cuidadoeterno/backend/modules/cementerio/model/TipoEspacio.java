package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_espacio")
@Getter
@Setter
@NoArgsConstructor
public class TipoEspacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_espacio", nullable = false, updatable = false)
    private Integer idTipoEspacio;

    @Column(name = "nombre_tipo", nullable = false, length = 50)
    private String nombreTipo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "nivel_complejidad", nullable = false)
    private Integer nivelComplejidad;
}