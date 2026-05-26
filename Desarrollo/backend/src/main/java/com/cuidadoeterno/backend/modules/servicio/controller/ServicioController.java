package com.cuidadoeterno.backend.modules.servicio.controller;

import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.Calificacion;
import com.cuidadoeterno.backend.modules.servicio.model.DetalleOrden;
import com.cuidadoeterno.backend.modules.servicio.model.RegistroEvidencia;
import com.cuidadoeterno.backend.modules.servicio.service.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Clave para evitar problemas de CORS probando en emuladores Android o Flutter
public class ServicioController {

    private final OrdenService ordenService;

    /**
     * Endpoint 1: Crear una nueva solicitud de servicio con o sin insumos (flores/velas).
     * POST /api/servicios/orden
     */
    @PostMapping("/orden")
    public ResponseEntity<DetalleOrden> crearOrden(@RequestBody OrdenRequestDTO request) {
        DetalleOrden nuevaOrden = ordenService.crearNuevaOrden(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    /**
     * Endpoint 2: Obtener el historial de órdenes/solicitudes de un cliente específico.
     * GET /api/servicios/cliente/{idCliente}/historial
     */
    @GetMapping("/cliente/{idCliente}/historial")
    public ResponseEntity<List<OrdenResponseDTO>> listarHistorialCliente(@PathVariable Integer idCliente) {
        List<OrdenResponseDTO> historial = ordenService.obtenerHistorialCliente(idCliente);
        return ResponseEntity.ok(historial);
    }

    /**
     * Endpoint 3: Subir una evidencia (foto de "antes", "durante" o "después") por parte del cuidador.
     * POST /api/servicios/evidencia
     */
    @PostMapping("/evidencia")
    public ResponseEntity<RegistroEvidencia> subirEvidencia(@RequestBody EvidenciaRequestDTO request) {
        RegistroEvidencia evidencia = ordenService.subirEvidencia(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(evidencia);
    }

    /**
     * Endpoint 4: Calificar un servicio finalizado (con estrellas de 1 al 5 y comentario).
     * POST /api/servicios/calificar
     */
    @PostMapping("/calificar")
    public ResponseEntity<Calificacion> calificarServicio(@RequestBody CalificacionRequestDTO request) {
        Calificacion calificacion = ordenService.calificarServicio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(calificacion);
    }
}