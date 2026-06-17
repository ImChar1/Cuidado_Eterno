package com.cuidadoeterno.backend.modules.inventario.controller;

import com.cuidadoeterno.backend.modules.inventario.dto.ProductoCatalogoResponseDTO;
import com.cuidadoeterno.backend.modules.inventario.dto.PuestoVentaResponseDTO;
import com.cuidadoeterno.backend.modules.inventario.service.InventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para evitar problemas de CORS al probar con el emulador de Android
public class InventarioController {

    private final InventarioService inventarioService;

    /**
     * Endpoint 1: Usado en la pantalla del mapa/ubicación.
     * Dado un cementerio, devuelve qué locales físicos están disponibles ahí.
     */
    @GetMapping("/cementerio/{idCementerio}/puestos")
    public ResponseEntity<List<PuestoVentaResponseDTO>> listarPuestosPorCementerio(@PathVariable Integer idCementerio) {
        
        List<PuestoVentaResponseDTO> respuesta = inventarioService.obtenerPuestosPorCementerio(idCementerio)
                .stream()
                .map(puesto -> new PuestoVentaResponseDTO(
                        puesto.getIdPuesto(),
                        puesto.getNombreLocal(),
                        puesto.getUbicacionRef(),
                        puesto.getTelefono()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint 2: Usado al abrir la ventana de compras (Upselling).
     * Dado un local (puesto), devuelve su catálogo de productos con stock y precios finales.
     */
    @GetMapping("/puestos/{idPuesto}/catalogo")
    public ResponseEntity<List<ProductoCatalogoResponseDTO>> listarCatalogoPorPuesto(@PathVariable Integer idPuesto) {
        
        List<ProductoCatalogoResponseDTO> respuesta = inventarioService.obtenerCatalogoDisponiblePorPuesto(idPuesto)
                .stream()
                .map(catalogo -> new ProductoCatalogoResponseDTO(
                        catalogo.getProducto().getIdProducto(),
                        catalogo.getProducto().getNombre(),
                        catalogo.getProducto().getDescripcion(),
                        catalogo.getProducto().getCategoria(),
                        catalogo.getProducto().getUrlImagen(),
                        catalogo.getPrecioVenta(), // El precio que definió este local
                        catalogo.getHayStock()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }
}