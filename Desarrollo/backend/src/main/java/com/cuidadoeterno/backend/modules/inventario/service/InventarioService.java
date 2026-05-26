package com.cuidadoeterno.backend.modules.inventario.service;

import com.cuidadoeterno.backend.modules.inventario.model.CatalogoProducto;
import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta;

import java.util.List;

public interface InventarioService {
    
    /**
     * Obtiene todos los locales físicos activos asociados a un cementerio.
     */
    List<PuestoVenta> obtenerPuestosPorCementerio(Integer idCementerio);

    /**
     * Obtiene la lista de productos que SÍ tienen stock en un puesto específico.
     */
    List<CatalogoProducto> obtenerCatalogoDisponiblePorPuesto(Integer idPuesto);
}