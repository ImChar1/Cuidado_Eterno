package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.TipoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Integer> {
    // JpaRepository ya provee findAll() para llenar las tarjetas principales de tu app
}