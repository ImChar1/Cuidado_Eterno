package com.cuidadoeterno.backend.modules.inventario.repository;

import com.cuidadoeterno.backend.modules.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Trae todos los productos que están activos en el sistema global
    List<Producto> findByEstadoActivoTrue();

    // Permite filtrar si en el futuro quieres una pestaña solo de "Flores" o "Herramientas"
    List<Producto> findByCategoriaAndEstadoActivoTrue(String categoria);
}