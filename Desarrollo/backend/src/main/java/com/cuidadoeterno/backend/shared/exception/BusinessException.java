package com.cuidadoeterno.backend.shared.exception;

import org.springframework.http.HttpStatus;

/**
 * Excepción base para errores de lógica de negocio.
 *
 * Se lanza cuando una operación falla por una regla del dominio,
 * no por un error técnico. Ejemplos:
 * - Intentar registrar un RUT que ya existe
 * - Intentar hacer login con credenciales incorrectas
 * - Intentar asignar un cuidador que no está disponible
 *
 * GlobalExceptionHandler la captura y la convierte en
 * ApiResponse<Void> con el status HTTP correspondiente.
 */
public class BusinessException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Constructor con mensaje y status HTTP explícito.
     *
     * @param message descripción del error (llega al cliente Android)
     * @param status  código HTTP que se enviará en la respuesta
     */
    public BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor con status 400 BAD_REQUEST por defecto.
     * Cubre la mayoría de errores de validación de negocio.
     *
     * @param message descripción del error
     */
    public BusinessException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    public HttpStatus getStatus() {
        return status;
    }
}