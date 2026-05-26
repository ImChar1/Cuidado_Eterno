package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la tabla PROVINCIA.
 */
@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Integer> {

    /**
     * Devuelve todas las provincias pertenecientes a una región específica.
     * Útil para llenar selects dependientes en el frontend (Ej: Si elige RM, mostrar solo sus provincias).
     */
    List<Provincia> findByRegionIdRegion(Integer idRegion);
}