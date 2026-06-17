package com.cuidadoeterno.backend.modules.servicio.repository;

import com.cuidadoeterno.backend.modules.servicio.model.RegistroEvidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroEvidenciaRepository extends JpaRepository<RegistroEvidencia, Integer> {

    // Trae todas las fotos (antes, durante, después) asociadas a un servicio específico
    List<RegistroEvidencia> findByDetalleOrdenIdOrden(Integer idOrden);

    boolean existsByDetalleOrdenIdOrden(Integer idOrden);
}