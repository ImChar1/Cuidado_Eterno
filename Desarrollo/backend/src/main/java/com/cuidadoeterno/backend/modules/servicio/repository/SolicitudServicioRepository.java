package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.SolicitudServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Integer> {

    // Trae todas las solicitudes de un cliente ordenadas de la más reciente a la más antigua
    List<SolicitudServicio> findByIdClienteOrderByFechaSolicitudDesc(Integer idCliente);

    Optional<SolicitudServicio> findByIdTransaccion(Integer idTransaccion);
}