package com.cuidadoeterno.backend.modules.inventario.repository;

import com.cuidadoeterno.backend.modules.inventario.model.CatalogoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogoProductoRepository extends JpaRepository<CatalogoProducto, Integer> {

    // Lista TODOS los productos de un local (sin importar si hay stock o no, útil para un administrador)
    List<CatalogoProducto> findByPuestoVentaIdPuesto(Integer idPuesto);

    // Lista solo los productos que SÍ tienen stock en un local específico (El que consumirá la App Android)
    List<CatalogoProducto> findByPuestoVentaIdPuestoAndHayStockTrue(Integer idPuesto);

    // Busca un producto específico dentro de un local específico (Para evitar duplicidad o actualizar precio)
    Optional<CatalogoProducto> findByPuestoVentaIdPuestoAndProductoIdProducto(Integer idPuesto, Integer idProducto);
}