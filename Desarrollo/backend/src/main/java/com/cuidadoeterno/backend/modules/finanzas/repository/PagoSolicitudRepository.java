package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.PagoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoSolicitudRepository extends JpaRepository<PagoSolicitud, Integer> {
}