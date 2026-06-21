package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipo_pago")
@Getter
@Setter
@NoArgsConstructor
public class TipoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_pago", nullable = false, updatable = false)
    private Integer idTipoPago;

    @Column(name = "nombre_metodo", nullable = false, length = 50)
    private String nombreMetodo;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}