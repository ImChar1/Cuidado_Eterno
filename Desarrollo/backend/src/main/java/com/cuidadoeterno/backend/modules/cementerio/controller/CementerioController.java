package com.cuidadoeterno.backend.modules.cementerio.controller;

import com.cuidadoeterno.backend.modules.cementerio.dto.*;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.service.CementerioService;
import com.cuidadoeterno.backend.modules.cementerio.service.EspacioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cementerios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite peticiones desde emuladores o dispositivos físicos
public class CementerioController {

    private final CementerioService cementerioService;
    private final EspacioService espacioService;

    // ============================================================
    // ENDPOINTS DE CATÁLOGOS (Para los desplegables de la App)
    // ============================================================

    @GetMapping("/regiones")
    public ResponseEntity<List<CatalogDTO>> listarRegiones() {
        List<CatalogDTO> dtos = cementerioService.obtenerTodasLasRegiones().stream()
                .map(r -> new CatalogDTO(r.getIdRegion(), r.getNombreRegion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/provincias/region/{idRegion}")
    public ResponseEntity<List<CatalogDTO>> listarProvincias(@PathVariable Integer idRegion) {
        List<CatalogDTO> dtos = cementerioService.obtenerProvinciasPorRegion(idRegion).stream()
                .map(p -> new CatalogDTO(p.getIdProvincia(), p.getNombreProvincia()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/comunas/provincia/{idProvincia}")
    public ResponseEntity<List<CatalogDTO>> listarComunas(@PathVariable Integer idProvincia) {
        List<CatalogDTO> dtos = cementerioService.obtenerComunasPorProvincia(idProvincia).stream()
                .map(c -> new CatalogDTO(c.getIdComuna(), c.getNombreComuna()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipos-espacio")
    public ResponseEntity<List<CatalogDTO>> listarTiposEspacio() {
        List<CatalogDTO> dtos = cementerioService.obtenerTiposDeEspacio().stream()
                .map(t -> new CatalogDTO(t.getIdTipoEspacio(), t.getNombreTipo()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ============================================================
    // ENDPOINTS DE BÚSQUEDA Y CEMENTERIOS
    // ============================================================

    @GetMapping("/buscar")
    public ResponseEntity<List<CementerioResponseDTO>> buscarPorNombre(@RequestParam String nombre) {
        List<CementerioResponseDTO> dtos = cementerioService.buscarCementeriosPorNombre(nombre).stream()
                .map(c -> new CementerioResponseDTO(
                        c.getIdCementerio(),
                        c.getNombreCementerio(),
                        c.getDireccion(),
                        c.getComuna().getNombreComuna()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/comuna/{idComuna}")
    public ResponseEntity<List<CementerioResponseDTO>> listarPorComuna(@PathVariable Integer idComuna) {
        List<CementerioResponseDTO> dtos = cementerioService.obtenerCementeriosPorComuna(idComuna).stream()
                .map(c -> new CementerioResponseDTO(
                        c.getIdCementerio(),
                        c.getNombreCementerio(),
                        c.getDireccion(),
                        c.getComuna().getNombreComuna()
                )).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ============================================================
    // ENDPOINTS DE ESPACIOS (Ubicaciones / Tumbas)
    // ============================================================

    @PostMapping("/espacios")
    public ResponseEntity<?> registrarEspacio(@RequestBody EspacioRequestDTO request) {
        try {
            Espacio nuevoEspacio = espacioService.registrarNuevoEspacio(
                    request.getIdCementerio(),
                    request.getIdTipoEspacio(),
                    request.getSectorPabellon(),
                    request.getNumeroSepultura(),
                    request.getCoordenadaLatitud(),
                    request.getCoordenadaLongitud()
            );
            
            // Retornamos el ID asignado para que Android sepa con qué espacio trabajar
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEspacio.getIdEspacio());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}