package com.cuidadoeterno.backend.modules.usuario.dto;

/**
 * DTO de salida para el endpoint POST /auth/login
 *
 * Lo que Android recibe dentro del campo "data" de ApiResponse:
 * {
 *   "success": true,
 *   "message": "Login exitoso",
 *   "data": {
 *     "token": "eyJhbGciOiJIUzI1NiJ9...",
 *     "tipo": "Bearer",
 *     "rol": "CLIENTE",
 *     "idPersona": 1,
 *     "nombre": "Juan",
 *     "apPaterno": "Pérez",
 *     "email": "juan.perez@email.com"
 *   }
 * }
 *
 * El ViewModel en Android usa idPersona y rol para decidir
 * qué pantalla mostrar después del login.
 *
 * Usamos record porque es inmutable: el token no se modifica una vez generado.
 */
public record LoginResponseDTO(

    /** JWT generado por JwtUtil */
    String token,

    /** Siempre "Bearer" — Android lo antepone al token en cada request */
    String tipo,

    /** Nombre del rol: "CLIENTE", "CUIDADOR" o "ADMINISTRADOR" */
    String rol,

    /** ID de la persona autenticada — Android lo guarda en SharedPreferences */
    Integer idPersona,

    /** Nombre de pila para mostrar en la UI */
    String nombre,

    /** Apellido paterno */
    String apPaterno,

    /** Email del usuario autenticado */
    String email

) {
    /**
     * Constructor con tipo "Bearer" por defecto.
     * Evita que AuthServiceImpl tenga que pasarlo explícitamente.
     */
    public LoginResponseDTO(String token, String rol, Integer idPersona,
                            String nombre, String apPaterno, String email) {
        this(token, "Bearer", rol, idPersona, nombre, apPaterno, email);
    }
}