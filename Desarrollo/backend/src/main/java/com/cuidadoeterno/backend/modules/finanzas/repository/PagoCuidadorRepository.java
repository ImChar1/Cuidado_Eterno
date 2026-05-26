package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.PagoCuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoCuidadorRepository extends JpaRepository<PagoCuidador, Integer> {
    List<PagoCuidador> findByIdCuenta(Integer idCuenta);
}