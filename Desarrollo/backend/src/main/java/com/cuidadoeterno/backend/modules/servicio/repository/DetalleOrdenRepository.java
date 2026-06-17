package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.DetalleOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Integer> {

    // Permite a la app del cuidador ver todos los servicios que están en estado "pendiente" o "en_proceso"
    List<DetalleOrden> findByEstadoOrden(String estadoOrden);

    Optional<DetalleOrden> findBySolicitudServicioIdSolicitud(Integer idSolicitud);

    // Permite ver el historial de mantenimientos que se le han hecho a una tumba en específico
    List<DetalleOrden> findByEspacioIdEspacio(Integer idEspacio);
}