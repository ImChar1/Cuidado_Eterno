package com.cuidadoeterno.backend.modules.usuario.controller;

import com.cuidadoeterno.backend.modules.usuario.dto.LoginRequestDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.LoginResponseDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroClienteDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroCuidadorDTO;
import com.cuidadoeterno.backend.modules.usuario.service.AuthService;
import com.cuidadoeterno.backend.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación y gestión de usuarios.
 *
 * Base path: /api/v1/auth  (el prefijo /api/v1 viene de application.yml)
 *
 * Endpoints públicos (no requieren token):
 *   POST /auth/login
 *   POST /auth/registro/cliente
 *                                      IMPORTANTE FALTA CREAR UN ARCHIVO PERFIL DTO
 *                                         LUEGO MODIFICAR EL AUTHSERVICE Y EL OTRO SERVICE 
 *                                         
 * Endpoints protegidos (requieren token + rol):
 *   POST /auth/registro/cuidador  → solo ADMINISTRADOR
 *   GET  /auth/perfil             → cualquier usuario autenticado
 *   GET  /auth/perfil/{id}        → solo ADMINISTRADOR
 *   POST /auth/logout             → cualquier usuario autenticado
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de login, registro y gestión de sesión")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ── ENDPOINTS PÚBLICOS ──────────────────────────────────────────────────────

    /**
     * POST /api/v1/auth/login
     *
     * Autentica al usuario y devuelve un token JWT.
     * Android guarda el token en SharedPreferences y lo incluye
     * en cada request posterior como: Authorization: Bearer <token>
     *
     * Acceso: público (sin token)
     *
     * Body:
     * {
     *   "nombreUsuario": "juan.perez",
     *   "clave": "miPassword123"
     * }
     *
     * Respuesta 200:
     * {
     *   "success": true,
     *   "message": "Login exitoso",
     *   "data": {
     *     "token": "eyJ...",
     *     "tipo": "Bearer",
     *     "rol": "CLIENTE",
     *     "idPersona": 1,
     *     "nombre": "Juan",
     *     "apPaterno": "Pérez",
     *     "email": "juan@email.com"
     *   }
     * }
     */
    @Operation(
        summary = "Login de usuario",
        description = "Autentica con nombre de usuario y contraseña. Devuelve JWT para usar en requests posteriores."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales incorrectas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {

        LoginResponseDTO respuesta = authService.login(dto);
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", respuesta));
    }

    /**
     * POST /api/v1/auth/registro/cliente
     *
     * Registra un nuevo cliente en el sistema.
     * Crea filas en PERSONA + CREDENCIAL + CLIENTE de forma transaccional.
     *
     * Acceso: público (sin token) — cualquier persona puede auto-registrarse
     *
     * Body: RegistroClienteDTO (ver DTO para campos requeridos)
     *
     * Respuesta 201:
     * {
     *   "success": true,
     *   "message": "Cliente registrado exitosamente"
     * }
     */
    @Operation(
        summary = "Registro de cliente",
        description = "Crea una nueva cuenta de cliente. No requiere autenticación previa."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente creado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "RUT, email o usuario ya existe"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/registro/cliente")
    public ResponseEntity<ApiResponse<Void>> registrarCliente(
            @Valid @RequestBody RegistroClienteDTO dto) {

        authService.registrarCliente(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Cliente registrado exitosamente", null));
    }

    // ── ENDPOINTS PROTEGIDOS ────────────────────────────────────────────────────

    /**
     * POST /api/v1/auth/registro/cuidador
     *
     * Registra un nuevo cuidador. Solo el ADMINISTRADOR puede hacerlo
     * porque implica asignar un horario y validar identidad.
     *
     * Acceso: ROLE_ADMINISTRADOR
     *
     * Header requerido: Authorization: Bearer <token>
     *
     * Body: RegistroCuidadorDTO (ver DTO para campos requeridos)
     *
     * Respuesta 201:
     * {
     *   "success": true,
     *   "message": "Cuidador registrado exitosamente"
     * }
     */
    @Operation(
        summary = "Registro de cuidador",
        description = "Crea una cuenta de cuidador. Solo accesible por ADMINISTRADOR.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cuidador creado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Horario no encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "RUT, email o usuario ya existe")
    })
    @PostMapping("/registro/cuidador")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<Void>> registrarCuidador(
            @Valid @RequestBody RegistroCuidadorDTO dto) {

        authService.registrarCuidador(dto);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Cuidador registrado exitosamente", null));
    }

    /**
     * POST /api/v1/auth/logout
     *
     * Cierre de sesión.
     *
     * Con JWT stateless el servidor no mantiene sesión,
     * por lo que el logout real ocurre en Android eliminando
     * el token de SharedPreferences.
     *
     * Este endpoint existe para:
     * 1. Registrar el evento de logout en logs del servidor
     * 2. Permitir que Android tenga un endpoint estándar que llamar
     * 3. Futuro: implementar blacklist de tokens si se requiere
     *
     * Acceso: cualquier usuario autenticado
     *
     * Header requerido: Authorization: Bearer <token>
     */
    @Operation(
        summary = "Logout",
        description = "Cierra la sesión. Android debe eliminar el token localmente.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(
            ApiResponse.ok("Sesión cerrada exitosamente", null)
        );
    }

    /**
     * GET /api/v1/auth/perfil
     *
     * Devuelve los datos del usuario actualmente autenticado.
     * Android usa este endpoint para refrescar los datos del perfil
     * sin hacer un nuevo login.
     *
     * Acceso: cualquier usuario autenticado (CLIENTE, CUIDADOR, ADMINISTRADOR)
     *
     * Header requerido: Authorization: Bearer <token>
     *
     * Respuesta 200:
     * {
     *   "success": true,
     *   "message": "OK",
     *   "data": {
     *     "token": null,
     *     "tipo": "Bearer",
     *     "rol": "CLIENTE",
     *     "idPersona": 1,
     *     "nombre": "Juan",
     *     "apPaterno": "Pérez",
     *     "email": "juan@email.com"
     *   }
     * }
     *
     * Nota: este endpoint requiere que AuthService exponga un método
     * obtenerPerfil(String nombreUsuario). Se implementa en la siguiente
     * iteración del módulo cuando se defina el PerfilDTO completo.
     */
    @Operation(
        summary = "Perfil del usuario autenticado",
        description = "Retorna los datos del usuario que posee el token JWT actual.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> perfil(
            @RequestHeader("Authorization") String authHeader) {

        // Extraemos el nombre de usuario del header para identificar al llamante
        // En la siguiente iteración esto llamará a authService.obtenerPerfil()
        String token = authHeader.substring(7);
        return ResponseEntity.ok(
            ApiResponse.ok("Endpoint activo. Implementación de perfil en siguiente iteración.", token)
        );
    }

    /**
     * GET /api/v1/auth/perfil/{id}
     *
     * Devuelve los datos de cualquier usuario por su id_persona.
     * Solo el ADMINISTRADOR puede consultar perfiles de otros usuarios.
     *
     * Acceso: ROLE_ADMINISTRADOR
     *
     * Header requerido: Authorization: Bearer <token>
     *
     * Path variable: id → id_persona en tabla PERSONA
     */
    @Operation(
        summary = "Perfil de usuario por ID",
        description = "Consulta el perfil de cualquier usuario. Solo para ADMINISTRADOR.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Perfil encontrado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Sin permisos de administrador"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/perfil/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ApiResponse<String>> perfilPorId(@PathVariable Integer id) {
        // Implementación completa en siguiente iteración con PerfilDTO
        return ResponseEntity.ok(
            ApiResponse.ok("Endpoint activo para id: " + id + ". Implementación en siguiente iteración.", null)
        );
    }
}