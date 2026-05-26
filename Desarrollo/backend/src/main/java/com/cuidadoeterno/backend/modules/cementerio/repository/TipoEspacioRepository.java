package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.TipoEspacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla TIPO_ESPACIO.
 */
@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {

    /**
     * Busca un tipo de espacio por su nombre exacto (ej. "Mausoleo").
     * Útil para validaciones internas.
     */
    Optional<TipoEspacio> findByNombreTipo(String nombreTipo);
}