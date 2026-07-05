package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Cementerio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la tabla CEMENTERIO.
 */
@Repository
public interface CementerioRepository extends JpaRepository<Cementerio, Integer> {

    /**
     * Busca cementerios cuyo nombre contenga el texto ingresado (Buscador).
     * Ignora mayúsculas y minúsculas (IgnoreCase).
     */
    List<Cementerio> findByNombreCementerioContainingIgnoreCase(String nombre);

    @Query("SELECT c FROM Cementerio c JOIN FETCH c.comuna")
    List<Cementerio> findAllConComuna();
    /**
     * Lista todos los cementerios ubicados en una comuna específica.
     */
    List<Cementerio> findByComunaIdComuna(Integer idComuna);
}