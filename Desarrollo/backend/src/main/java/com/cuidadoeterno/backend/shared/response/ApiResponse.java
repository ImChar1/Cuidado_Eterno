package com.cuidadoeterno.backend.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Wrapper estándar para todas las respuestas de la API.
 *
 * El ViewModel en Android siempre recibirá este objeto,
 * lo que hace el manejo de errores predecible en el cliente.
 *
 * Ejemplo de respuesta exitosa:
 * {
 *   "success": true,
 *   "message": "Login exitoso",
 *   "data": { "token": "eyJ...", "rol": "CLIENTE", "nombre": "Juan" }
 * }
 *
 * Ejemplo de error:
 * {
 *   "success": false,
 *   "message": "Credenciales incorrectas",
 *   "data": null
 * }
 *
 * @param <T> tipo del campo data (LoginResponseDTO, List<ProductoDTO>, etc.)
 */
@JsonInclude(JsonInclude.Include.NON_NULL) // no serializa campos null
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // ── Factory methods ─────────────────────────────────────────────────────────

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // ── Getters ─────────────────────────────────────────────────────────────────

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}