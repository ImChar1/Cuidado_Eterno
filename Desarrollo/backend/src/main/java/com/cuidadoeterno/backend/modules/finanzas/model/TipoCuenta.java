package com.cuidadoeterno.backend.modules.finanzas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tabla: tipo_cuenta
 * Catálogo paramétrico que define los tipos de cuentas bancarias soportadas.
 * (Ej: Cuenta Corriente, Cuenta Vista, Cuenta de Ahorro, Cuenta RUT).
 */
@Entity
@Table(name = "tipo_cuenta")
@Getter
@Setter
@NoArgsConstructor
public class TipoCuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_cuenta")
    private Integer idTipoCuenta;

    /**
     * Nombre del tipo de cuenta bancaria.
     */
    @Column(name = "nombre_tipo", nullable = false, length = 50)
    private String nombre;

}