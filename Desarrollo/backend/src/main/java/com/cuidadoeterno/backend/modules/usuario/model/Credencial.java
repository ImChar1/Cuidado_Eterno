package com.cuidadoeterno.backend.modules.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Tabla: CREDENCIAL
 * Almacena los datos de acceso al sistema.
 * clave_hash usa PBKDF2WithHmacSHA256 gestionado por Spring Security.
 *
 * Relaciones:
 * - ManyToOne → ROL  (una credencial tiene un rol)
 * - OneToOne  ← PERSONA (una credencial pertenece a una persona)
 */
@Entity
@Table(name = "CREDENCIAL")
@Getter
@Setter
@NoArgsConstructor
public class Credencial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credencial", nullable = false, updatable = false)
    private Integer idCredencial;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    /**
     * Hash generado por Pbkdf2PasswordEncoder.
     * Nunca se almacena la contraseña en texto plano.
     * Longitud 255 para acomodar el formato: {pbkdf2}iterations:salt:hash
     */
    @Column(name = "clave_hash", nullable = false, length = 255)
    private String claveHash;

    /**
     * Valores esperados: 'activa', 'bloqueada', 'inactiva'
     */
    @Column(name = "estado_cuenta", nullable = false, length = 20)
    private String estadoCuenta = "activa";

    @Column(name = "ultimo_inicio")
    private LocalDateTime ultimoInicio;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos = 0;

    // ── Relaciones ──────────────────────────────────────────────────────────────

    /**
     * FK → ROL.id_rol
     * EAGER porque siempre necesitamos el rol cuando cargamos las credenciales
     * (UserDetailsServiceImpl lo usa para construir las authorities).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;
}