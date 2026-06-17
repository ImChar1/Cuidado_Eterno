package com.cuidadoeterno.backend.modules.usuario.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO de entrada para el endpoint POST /auth/registro/cuidador
 *
 * Similar a RegistroClienteDTO pero incluye el id del horario
 * asignado al cuidador. El horario lo asigna el administrador,
 * por lo que este endpoint debería estar protegido con @PreAuthorize.
 *
 * Crea filas en: PERSONA + CREDENCIAL + CUIDADOR
 *
 * Lo que Android (panel admin) envía:
 * {
 *   "rut": "98765432-1",
 *   "nombre": "María",
 *   "apPaterno": "López",
 *   "apMaterno": null,
 *   "email": "maria@cuidado.cl",
 *   "telefono": "+56987654321",
 *   "fechaNacimiento": "1985-03-20",
 *   "genero": "F",
 *   "nombreUsuario": "maria.lopez",
 *   "clave": "securePass99",
 *   "idHorario": 3
 * }
 */
public record RegistroCuidadorDTO(

    // ── Datos de PERSONA ────────────────────────────────────────────────────────

    @NotBlank(message = "El RUT es obligatorio")
    @Pattern(
        regexp = "^\\d{7,8}-[\\dkK]$",
        message = "Formato de RUT inválido. Ejemplo: 98765432-1"
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

    // ── Datos de CUIDADOR ───────────────────────────────────────────────────────

    // ── Disponibilidad del cuidador ─────────────────────────────────────────────
    // Ya no se asigna un horario fijo de cementerio.
    // El cuidador declara en qué días puede trabajar y en qué horario.
    // La validación contra el horario del cementerio ocurre al aceptar una solicitud.

    @NotBlank(message = "Los días de disponibilidad son obligatorios")
    String disponibilidadDias,          // "lunes,martes,miercoles"

    @NotNull(message = "La hora de inicio es obligatoria")
    LocalTime disponibilidadHoraInicio,

    @NotNull(message = "La hora de fin es obligatoria")
    LocalTime disponibilidadHoraFin,

    // ── Documentación de certificación ─────────────────────────────────────────

    @NotBlank(message = "La URL de certificación es obligatoria")
    @Size(max = 500)
    String urlCertificacion,

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(
        regexp = "^(cedula|certificado_municipal|registro_cementerio|otro)$",
        message = "Tipo de documento no válido"
    )
    String tipoDocumento,

    // Opcional: número de registro si el documento lo tiene
    @Size(max = 50)
    String numeroRegistro

) {}