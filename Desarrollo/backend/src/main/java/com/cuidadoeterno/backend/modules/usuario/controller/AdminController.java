package com.cuidadoeterno.backend.modules.usuario.controller;

import com.cuidadoeterno.backend.modules.usuario.dto.CuidadorAdminResponse;
import com.cuidadoeterno.backend.modules.usuario.dto.PerfilDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroCuidadorDTO;
import com.cuidadoeterno.backend.modules.usuario.service.AdminUsuarioService;
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
@RequestMapping("/admin") // Recomendable estandarizar la versión base de la API aquí
@PreAuthorize("hasRole('ADMINISTRADOR')")
@Tag(name = "Administración", description = "Gestión de cuidadores — solo ADMINISTRADOR")
public class AdminController {

    private final AdminUsuarioService adminService;
    private final AuthService authService; // Solo se mantiene para la creación forzada de cuentas

    public AdminController(AdminUsuarioService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @PutMapping("/cuidadores/{idPersona}/verificar")
    public ResponseEntity<ApiResponse<Void>> verificarCuidador(
            @PathVariable Integer idPersona,
            @RequestParam String estado) {
        adminService.cambiarEstadoVerificacion(idPersona, estado);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado a: " + estado, null));
    }

    @GetMapping("/cuidadores")
    public ResponseEntity<ApiResponse<List<CuidadorAdminResponse>>> listarPorEstado(
            @RequestParam(defaultValue = "pendiente") String estado) {
        List<CuidadorAdminResponse> cuidadores = adminService.listarCuidadoresPorEstado(estado);
        return ResponseEntity.ok(ApiResponse.ok("Cuidadores obtenidos", cuidadores));
    }

    @DeleteMapping("/cuidadores/{idPersona}")
    @Operation(summary = "Eliminar un cuidador del sistema", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> eliminarCuidador(@PathVariable Integer idPersona) {
        adminService.eliminarCuidador(idPersona);
        return ResponseEntity.ok(ApiResponse.ok("Cuidador eliminado del sistema", null));
    }

    @PutMapping("/cuidadores/{idPersona}")
    @Operation(summary = "Modificar datos básicos de un cuidador", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> modificarCuidador(
            @PathVariable Integer idPersona,
            @RequestBody PerfilDTO dto) {
        adminService.modificarCuidador(idPersona, dto);
        return ResponseEntity.ok(ApiResponse.ok("Datos del cuidador actualizados", null));
    }

    @PostMapping("/cuidadores/soporte")
    @Operation(summary = "Crear cuenta de cuidador desde soporte técnico", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> crearCuidadorSoporte(@RequestBody RegistroCuidadorDTO dto) {
        authService.registrarCuidador(dto); // Reutiliza el registro que ya valida duplicados [source: 13]
        return ResponseEntity.ok(ApiResponse.ok("Cuenta de cuidador creada exitosamente por soporte", null));
    }
}