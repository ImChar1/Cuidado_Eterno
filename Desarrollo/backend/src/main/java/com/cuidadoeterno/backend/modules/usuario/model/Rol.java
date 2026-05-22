package com.cuidadoeterno.backend.modules.usuario.model;
 
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
/**
 * Tabla: ROL
 * Catálogo de roles del sistema: CLIENTE, CUIDADOR, ADMINISTRADOR.
 * Es la raíz de la cadena ROL → CREDENCIAL → PERSONA.
 */
@Entity
@Table(name = "ROL")
@Getter
@Setter
@NoArgsConstructor
public class Rol {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol", nullable = false, updatable = false)
    private Integer idRol;
 
    @Column(name = "nombre_rol", nullable = false, length = 30)
    private String nombreRol;
}