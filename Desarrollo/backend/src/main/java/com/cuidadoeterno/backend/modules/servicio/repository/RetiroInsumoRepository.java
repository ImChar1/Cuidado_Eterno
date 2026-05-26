package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.RetiroInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RetiroInsumoRepository extends JpaRepository<RetiroInsumo, Integer> {

    // Lista todos los retiros de productos vinculados a un servicio
    List<RetiroInsumo> findByDetalleOrdenIdOrden(Integer idOrden);
}