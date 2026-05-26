package com.cuidadoeterno.backend.modules.usuario.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO de entrada para el endpoint POST /auth/registro/cliente
 *
 * Agrupa todos los datos necesarios para crear:
 * - Una fila en PERSONA
 * - Una fila en CREDENCIAL
 * - Una fila en CLIENTE
 *
 * Lo que Android envía:
 * {
 *   "rut": "12345678-9",
 *   "nombre": "Juan",
 *   "apPaterno": "Pérez",
 *   "apMaterno": "González",
 *   "email": "juan@email.com",
 *   "telefono": "+56912345678",
 *   "fechaNacimiento": "1990-05-15",
 *   "genero": "M",
 *   "nombreUsuario": "juan.perez",
 *   "clave": "miPassword123",
 *   "prefNotificacion": "push"
 * }
 */
public record RegistroClienteDTO(

    // ── Datos de PERSONA ────────────────────────────────────────────────────────

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(
        regexp = "^\\d{7,8}-[\\dkK]$",
        message = "Formato de RUT inválido. Ejemplo: 12345678-9"
    )
    String rut,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar 50 caracteres")
    String nombre,

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(max = 50, message = "El apellido paterno no puede superar 50 caracteres")
    String apPaterno,

    @Size(max = 50, message = "El apellido materno no puede superar 50 caracteres")
    String apMaterno,  // nullable

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 100, message = "El email no puede superar 100 caracteres")
    String email,

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
        regexp = "^\\+?[0-9]{8,15}$",
        message = "Formato de teléfono inválido"
    )
    String telefono,

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    LocalDate fechaNacimiento,

    @NotBlank(message = "El género es obligatorio")
    @Pattern(
        regexp = "^[MFO]$",
        message = "El género debe ser M, F u O"
    )
    String genero,

    // ── Datos de CREDENCIAL ─────────────────────────────────────────────────────

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    String nombreUsuario,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    String clave,

    // ── Datos de CLIENTE ────────────────────────────────────────────────────────

    @Pattern(
        regexp = "^(email|push|sms)$",
        message = "La preferencia de notificación debe ser: email, push o sms"
    )
    String prefNotificacion  // nullable, default "email" en el service

) {}