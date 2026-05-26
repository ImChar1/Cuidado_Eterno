package com.cuidadoeterno.backend.modules.inventario.repository;

import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuestoVentaRepository extends JpaRepository<PuestoVenta, Integer> {

    // Busca los locales físicos que pertenecen a un cementerio específico y que están abiertos/activos
    List<PuestoVenta> findByCementerioIdCementerioAndEstadoPuestoTrue(Integer idCementerio);
}