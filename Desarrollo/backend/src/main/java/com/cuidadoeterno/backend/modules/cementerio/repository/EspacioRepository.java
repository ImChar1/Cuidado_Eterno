package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la tabla ESPACIO.
 * Representa la ubicación exacta de la sepultura dentro de un cementerio.
 */
@Repository
public interface EspacioRepository extends JpaRepository<Espacio, Integer> {

    /**
     * Busca todos los espacios registrados dentro de un cementerio específico.
     */
    List<Espacio> findByCementerioIdCementerio(Integer idCementerio);

    /**
     * Verifica si ya existe un espacio registrado en ese cementerio, sector y número de sepultura.
     * Útil para evitar duplicados en la base de datos al momento de guardar un nuevo espacio.
     */
    Optional<Espacio> findByCementerioIdCementerioAndSectorPabellonAndNumeroSepultura(
            Integer idCementerio, String sectorPabellon, String numeroSepultura);
}