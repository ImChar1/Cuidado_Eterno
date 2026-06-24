package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.BilleteraVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BilleteraVirtualRepository extends JpaRepository<BilleteraVirtual, Integer> {
    
    // Necesitaremos buscar la billetera usando el ID del cuidador
    Optional<BilleteraVirtual> findByCuidador_IdPersona(Integer idCuidador);
}
