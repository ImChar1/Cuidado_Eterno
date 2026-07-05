package com.cuidadoeterno.backend.modules.servicio.controller;

import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.Calificacion;
import com.cuidadoeterno.backend.modules.servicio.model.DetalleOrden;
import com.cuidadoeterno.backend.modules.servicio.model.RegistroEvidencia;
import com.cuidadoeterno.backend.modules.servicio.service.OrdenService;
import com.cuidadoeterno.backend.shared.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.cuidadoeterno.backend.modules.servicio.model.enums.SubEstadoOrden;

import java.util.List;
 
@RestController
@RequestMapping("/ordenes")
@RequiredArgsConstructor
@Tag(name = "Órdenes", description = "Gestión de solicitudes de servicio y órdenes de trabajo")
public class OrdenController {
 
    private final OrdenService ordenService;
 
    // ── CLIENTE ─────────────────────────────────────────────────────────────────
    @GetMapping("tipos-solicitud")
    public ResponseEntity<ApiResponse<List<TipoSolicitudResponseDTO>>> obtenerTiposSolicitud() {
        List<TipoSolicitudResponseDTO> tipos = ordenService.obtenerTodosLosTipos();
        return ResponseEntity.ok(ApiResponse.ok("Tipos de solicitud obtenidos", tipos));
    }
    /**
     * POST /api/v1/ordenes
     *
     * El cliente crea una nueva solicitud de servicio.
     * Registra o reutiliza el espacio del difunto,
     * registra los datos del fallecido y crea la orden en estado "pendiente".
     * El cuidador la acepta después.
     *
     * Acceso: ROLE_CLIENTE
     */
    @Operation(
        summary = "Crear solicitud de servicio",
        description = "El cliente levanta una nueva solicitud. Queda pendiente hasta que un cuidador la acepte.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Integer>> crearOrden(
            @RequestBody OrdenRequestDTO request) {
 
        DetalleOrden orden = ordenService.crearNuevaOrden(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Solicitud creada exitosamente", orden.getIdOrden()));
    }
 
    /**
     * GET /api/v1/ordenes/historial/cliente/{idCliente}
     *
     * Devuelve el historial de solicitudes del cliente.
     * Incluye estado, cuidador asignado, monto y si tiene evidencia o calificación.
     *
     * Acceso: ROLE_CLIENTE
     */
    @Operation(
        summary = "Historial de solicitudes del cliente",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/historial/cliente/{idCliente}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<List<OrdenResponseDTO>>> historialCliente(
            @PathVariable Integer idCliente) {
 
        List<OrdenResponseDTO> historial = ordenService.obtenerHistorialCliente(idCliente);
        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido", historial));
    }
 
    /**
     * POST /api/v1/ordenes/{idOrden}/calificar
     *
     * El cliente califica el servicio completado.
     * Cierra el ciclo: la orden pasa a "completada" y el cuidador queda libre.
     *
     * Acceso: ROLE_CLIENTE
     */
    @Operation(
        summary = "Calificar un servicio completado",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{idOrden}/calificar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ApiResponse<Void>> calificar(
            @PathVariable Integer idOrden,
            @RequestBody CalificacionRequestDTO request) {
 
        request.setIdOrden(idOrden);
        ordenService.calificarServicio(request);
        return ResponseEntity.ok(ApiResponse.ok("Servicio calificado exitosamente", null));
    }
 
    // ── CUIDADOR ────────────────────────────────────────────────────────────────
 
    /**
     * GET /api/v1/ordenes/disponibles?idCementerio=1
     *
     * El cuidador consulta las solicitudes disponibles filtradas por cementerio.
     * Solo muestra órdenes en estado "pendiente" (sin cuidador asignado).
     * El cuidador filtra por el cementerio donde trabaja.
     *
     * Acceso: ROLE_CUIDADOR
     */
    @Operation(
        summary = "Solicitudes disponibles para el cuidador",
        description = "Lista las órdenes pendientes del cementerio seleccionado.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/disponibles")
    @PreAuthorize("hasRole('CUIDADOR')")
    public ResponseEntity<ApiResponse<List<OrdenResponseDTO>>> solicitudesDisponibles(
            @RequestParam Integer idCementerio) {
 
        List<OrdenResponseDTO> disponibles = ordenService
            .obtenerSolicitudesDisponibles(idCementerio);
        return ResponseEntity.ok(ApiResponse.ok("Solicitudes disponibles obtenidas", disponibles));
    }
 
    /**
     * PUT /api/v1/ordenes/{idOrden}/aceptar?idCuidador=5
     *
     * El cuidador acepta una solicitud disponible.
     * Valida que el cuidador esté verificado y disponible,
     * lo asigna a la orden y la cambia a estado "en_proceso".
     *
     * Acceso: ROLE_CUIDADOR
     */
    @Operation(
        summary = "Aceptar una solicitud",
        description = "El cuidador toma una solicitud pendiente. Solo cuidadores verificados y disponibles pueden hacerlo.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{idOrden}/aceptar")
    @PreAuthorize("hasRole('CUIDADOR')")
    public ResponseEntity<ApiResponse<Void>> aceptarSolicitud(
            @PathVariable Integer idOrden,
            @RequestParam Integer idCuidador) {
 
        ordenService.aceptarSolicitud(idOrden, idCuidador);
        return ResponseEntity.ok(ApiResponse.ok("Solicitud aceptada exitosamente", null));
    }
 
    /**
     * GET /api/v1/ordenes/historial/cuidador/{idCuidador}
     *
     * El cuidador ve sus órdenes: en proceso, completadas e historial completo.
     * Incluye datos del espacio, cliente y calificación recibida.
     *
     * Acceso: ROLE_CUIDADOR
     */
    @Operation(
        summary = "Historial de órdenes del cuidador",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/historial/cuidador/{idCuidador}")
    @PreAuthorize("hasRole('CUIDADOR')")
    public ResponseEntity<ApiResponse<List<OrdenResponseDTO>>> historialCuidador(
            @PathVariable Integer idCuidador) {
 
        List<OrdenResponseDTO> historial = ordenService.obtenerHistorialCuidador(idCuidador);
        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido", historial));
    }
 
    /**
     * POST /api/v1/ordenes/{idOrden}/evidencia
     *
     * El cuidador sube fotos de evidencia antes y después del servicio.
     * tipo_momento: "antes" o "despues"
     *
     * Acceso: ROLE_CUIDADOR
     */
    @Operation(
        summary = "Subir evidencia fotográfica",
        description = "El cuidador sube fotos del estado del espacio antes y después del servicio.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{idOrden}/evidencia")
    @PreAuthorize("hasRole('CUIDADOR')")
    public ResponseEntity<ApiResponse<Void>> subirEvidencia(
            @PathVariable Integer idOrden,
            @RequestBody EvidenciaRequestDTO request) {
 
        request.setIdOrden(idOrden);
        ordenService.subirEvidencia(request);
        return ResponseEntity.ok(ApiResponse.ok("Evidencia registrada exitosamente", null));
    }

    /**
     * PUT /api/v1/ordenes/{idOrden}/finalizar
     *
     * El cuidador marca el servicio como terminado tras subir la evidencia.
     * Esto cambia el estado a "completada" y ABONA EL PAGO a su billetera virtual.
     *
     * Acceso: ROLE_CUIDADOR
     */
    @Operation(
        summary = "Finalizar servicio (Cuidador)",
        description = "Marca la orden como completada y deposita las ganancias en la billetera virtual del cuidador.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{idOrden}/finalizar")
    @PreAuthorize("hasRole('CUIDADOR')")
    public ResponseEntity<ApiResponse<Void>> finalizarServicioCuidador(
            @PathVariable Integer idOrden) {
 
        ordenService.finalizarServicioCuidador(idOrden);
        return ResponseEntity.ok(ApiResponse.ok("Servicio finalizado y pago abonado exitosamente", null));
    }

    /**
     * PATCH /api/v1/ordenes/{idOrden}/sub-estado
     * El cuidador informa que está "En camino", "Comprando insumos", etc.
     */
    @PatchMapping("/{idOrden}/sub-estado")
    public ResponseEntity<ApiResponse<OrdenResponseDTO>> actualizarSubEstadoEnVivo(
            @PathVariable Integer idOrden,
            @RequestParam Integer idCuidador,
            @RequestParam SubEstadoOrden nuevoSubEstado) {
            
        DetalleOrden orden = ordenService.actualizarSubEstado(idOrden, idCuidador, nuevoSubEstado);
        
        // 1. Reconstruimos el nombre del cuidador de forma segura
        String nombreCuidador = orden.getCuidador() != null
            ? orden.getCuidador().getNombre() + " " + orden.getCuidador().getApPaterno()
            : "Sin asignar";
            
        // 2. Usamos el constructor completo con los 12 parámetros requeridos
        OrdenResponseDTO responseDTO = new OrdenResponseDTO(
            orden.getIdOrden(),
            orden.getSolicitudServicio().getIdSolicitud(),
            orden.getSolicitudServicio().getTipoSolicitud().getNombreServicio(),
            nombreCuidador,
            orden.getEspacio().getSectorPabellon() + " - N°" + orden.getEspacio().getNumeroSepultura(),
            orden.getFechaCreacion(),
            orden.getFechaProgramada(),
            orden.getEstadoOrden(),
            orden.getMontoTotal(),
            false, // tieneEvidencia (por defecto en falso para la actualización rápida)
            false, // tieneCalificacion
            orden.getSubEstadoOrden()
        );
        
        // 3. CORRECCIÓN: Añadimos explícitamente el tipo <OrdenResponseDTO> para eliminar el fallo de inferencia
        return ResponseEntity.ok(
            ApiResponse.ok("Estado de transmisión actualizado con éxito", responseDTO)
        );
    }

}