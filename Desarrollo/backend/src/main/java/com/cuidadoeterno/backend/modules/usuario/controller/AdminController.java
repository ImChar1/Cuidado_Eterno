package com.cuidadoeterno.backend.modules.usuario.controller;
 
import com.cuidadoeterno.backend.modules.usuario.dto.PerfilDTO;
import com.cuidadoeterno.backend.modules.usuario.service.AuthService;
import com.cuidadoeterno.backend.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Administración", description = "Gestión de cuidadores — solo ADMINISTRADOR")
public class AdminController {
 
    private final AuthService authService;
 
    public AdminController(AuthService authService) {
        this.authService = authService;
    }
 
    /**
     * PUT /api/v1/admin/cuidadores/{idPersona}/verificar?estado=verificado
     *
     * Aprueba o rechaza el registro de un cuidador.
     * El cuidador solo puede aceptar solicitudes cuando estado = 'verificado'.
     *
     * @param estado  'verificado' o 'rechazado'
     */
    @Operation(
        summary = "Verificar o rechazar un cuidador",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/cuidadores/{idPersona}/verificar")
    public ResponseEntity<ApiResponse<Void>> verificarCuidador(
            @PathVariable Integer idPersona,
            @RequestParam String estado) {
 
        authService.cambiarEstadoVerificacion(idPersona, estado);
        return ResponseEntity.ok(
            ApiResponse.ok("Estado actualizado a: " + estado, null)
        );
    }
 
    /**
     * GET /api/v1/admin/cuidadores/pendientes
     *
     * Lista todos los cuidadores que esperan verificación.
     * El administrador los revisa y usa el endpoint de verificar para aprobarlos.
     */
    @Operation(
        summary = "Listar cuidadores pendientes de verificación",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/cuidadores/pendientes")
    public ResponseEntity<ApiResponse<List<PerfilDTO>>> listarPendientes() {
        List<PerfilDTO> pendientes = authService.listarCuidadoresPorEstado("pendiente");
        return ResponseEntity.ok(
            ApiResponse.ok("Cuidadores pendientes obtenidos", pendientes)
        );
    }
 
    /**
     * GET /api/v1/admin/cuidadores?estado=verificado
     *
     * Lista cuidadores por cualquier estado: 'pendiente', 'verificado', 'rechazado'.
     * Más flexible que el endpoint de pendientes.
     */
    @Operation(
        summary = "Listar cuidadores por estado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/cuidadores")
    public ResponseEntity<ApiResponse<List<PerfilDTO>>> listarPorEstado(
            @RequestParam(defaultValue = "pendiente") String estado) {
 
        List<PerfilDTO> cuidadores = authService.listarCuidadoresPorEstado(estado);
        return ResponseEntity.ok(
            ApiResponse.ok("Cuidadores con estado '" + estado + "' obtenidos", cuidadores)
        );
    }
}