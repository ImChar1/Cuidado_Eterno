package com.cuidadoeterno.api.repositories;

import com.cuidadoeterno.models.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    // Solo con extender JpaRepository, Spring Boot ya sabe hacer INSERT, SELECT, UPDATE y DELETE.
}