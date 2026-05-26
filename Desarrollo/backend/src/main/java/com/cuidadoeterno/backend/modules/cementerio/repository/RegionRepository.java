package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la tabla REGION.
 */
@Repository
public interface RegionRepository extends JpaRepository<Region, Integer> {
    // JpaRepository ya incluye findAll() que usaremos para listar todas las regiones.
}