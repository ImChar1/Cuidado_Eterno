package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.TipoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoPagoRepository extends JpaRepository<TipoPago, Integer> {
    Optional<TipoPago> findByNombreMetodo(String nombreMetodo);
}