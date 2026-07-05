package com.cuidadoeterno.backend.shared.exception;

import com.cuidadoeterno.backend.shared.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Intercepta todas las excepciones no manejadas en los controllers
 * y las convierte en ApiResponse con el status HTTP correcto.
 *
 * Android siempre recibirá la misma estructura, independientemente
 * del tipo de error. El ViewModel solo necesita revisar "success".
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> atraparErrores(Exception ex) {
        // Esto captura CUALQUIER error 500 y te lo devuelve como texto en el JSON
        ex.printStackTrace(); // Lo imprime en consola
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor: " + ex.getMessage()));
    }
    /**
     * Errores de validación de DTOs (@Valid en el controller).
     * Spring lanza esto cuando @NotBlank, @Pattern, @Size, etc. fallan.
     *
     * Respuesta al cliente:
     * {
     *   "success": false,
     *   "message": "rut: Formato de RUT inválido. | email: Formato de email inválido."
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidacion(
            MethodArgumentNotValidException ex) {

        String errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(" | "));

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errores));
    }

    /**
     * Errores de lógica de negocio lanzados explícitamente
     * desde los services con throw new BusinessException(...).
     *
     * El status HTTP viene dentro de la excepción (400, 404, 409, etc.)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        return ResponseEntity
            .status(ex.getStatus())
            .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Credenciales incorrectas durante el login.
     * Spring Security lanza esto cuando usuario o contraseña no coinciden.
     *
     * Siempre 401 y mensaje genérico para no revelar si el usuario existe.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
            BadCredentialsException ex) {

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("Credenciales incorrectas"));
    }

    /**
     * Cuenta deshabilitada (estado_cuenta = 'inactiva').
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Void>> handleDisabled(DisabledException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("La cuenta está deshabilitada"));
    }

    /**
     * Cuenta bloqueada (estado_cuenta = 'bloqueada').
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleLocked(LockedException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("La cuenta está bloqueada. Contacte al administrador"));
    }

    /**
     * Captura cualquier excepción no prevista.
     * Evita que stacktraces internos lleguen al cliente Android.
     *
     * En producción este mensaje es genérico intencionalmente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("Error interno del servidor. Intente más tarde"));
    }
}