package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    // Usamos Optional porque una orden puede o no tener una calificación todavía
    Optional<Calificacion> findByDetalleOrdenIdOrden(Integer idOrden);
}