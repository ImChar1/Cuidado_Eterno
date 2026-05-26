package com.cuidadoeterno.backend.modules.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para el endpoint POST /auth/login
 *
 * Lo que Android envía en el body:
 * {
 *   "nombreUsuario": "juan.perez",
 *   "clave": "miPassword123"
 * }
 *
 * @NotBlank valida que el campo no sea null, vacío ni solo espacios.
 * Spring lanza MethodArgumentNotValidException si falla,
 * que GlobalExceptionHandler convierte en ApiResponse de error.
 */
public record LoginRequestDTO(

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 50, message = "El nombre de usuario no puede superar 50 caracteres")
    String nombreUsuario,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    String clave

) {}