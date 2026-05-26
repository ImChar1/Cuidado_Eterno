package com.cuidadoeterno.backend.modules.inventario.service;

import com.cuidadoeterno.backend.modules.inventario.model.CatalogoProducto;
import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta;
import com.cuidadoeterno.backend.modules.inventario.repository.CatalogoProductoRepository;
import com.cuidadoeterno.backend.modules.inventario.repository.PuestoVentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final PuestoVentaRepository puestoVentaRepository;
    private final CatalogoProductoRepository catalogoProductoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PuestoVenta> obtenerPuestosPorCementerio(Integer idCementerio) {
        // Llama al repositorio para buscar tiendas activas en el cementerio solicitado
        return puestoVentaRepository.findByCementerioIdCementerioAndEstadoPuestoTrue(idCementerio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CatalogoProducto> obtenerCatalogoDisponiblePorPuesto(Integer idPuesto) {
        // Llama al repositorio para buscar solo los productos con stock en ese local
        return catalogoProductoRepository.findByPuestoVentaIdPuestoAndHayStockTrue(idPuesto);
    }
}