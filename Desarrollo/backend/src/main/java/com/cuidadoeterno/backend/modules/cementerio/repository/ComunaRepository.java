package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la tabla COMUNA.
 */
@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Integer> {

    /**
     * Devuelve todas las comunas de una provincia específica.
     */
    List<Comuna> findByProvinciaIdProvincia(Integer idProvincia);
}