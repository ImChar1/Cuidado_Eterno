package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.TransaccionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Integer> {
    Optional<TransaccionPago> findByTokenWs(String tokenWs);
    Optional<TransaccionPago> findByOrdenCompra(String ordenCompra);
}