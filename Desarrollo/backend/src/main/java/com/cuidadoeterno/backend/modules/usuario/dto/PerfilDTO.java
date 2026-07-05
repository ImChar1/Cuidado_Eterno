package com.cuidadoeterno.backend.modules.usuario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de salida para GET /auth/perfil y GET /auth/perfil/{id}
 *
 * Cubre los 3 roles del sistema en un solo objeto.
 * Los campos específicos de cada rol son null cuando no aplican,
 * y @JsonInclude(NON_NULL) evita que lleguen al cliente Android.
 *
 * Ejemplo respuesta CLIENTE:
 * {
 *   "idPersona": 1, "nombre": "Juan", "apPaterno": "Pérez",
 *   "email": "juan@email.com", "rol": "CLIENTE",
 *   "estadoCliente": "activo", "prefNotificacion": "push"
 *   // campos de CUIDADOR y ADMINISTRADOR ausentes
 * }
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerfilDTO {

    // ── Datos comunes (PERSONA) ─────────────────────────────────────────────────
    private final Integer idPersona;
    private final String nombre;
    private final String apPaterno;
    private final String apMaterno;
    private final String email;
    private final String telefono;
    private final LocalDate fechaNacimiento;
    private final String genero;

    // ── Datos de CREDENCIAL ─────────────────────────────────────────────────────
    private final String rol;
    private final String nombreUsuario;
    private final String estadoCuenta;

    // ── Exclusivo CLIENTE ───────────────────────────────────────────────────────
    private final LocalDate fechaRegistro;
    private final String prefNotificacion;
    private final String estadoCliente;

    // ── Exclusivo CUIDADOR ──────────────────────────────────────────────────────
    private final BigDecimal calificacionPromedio;
    private final String estadoVerificacion;
    private final String estadoDisponibilidad;
    private final LocalDate fechaIngresoCuidador;

    // ── Exclusivo ADMINISTRADOR ─────────────────────────────────────────────────
    private final String nivelAcceso;
    private final String cargo;
    private final LocalDate fechaIngresoAdmin;

}