package com.cuidadoeterno.backend.modules.finanzas.repository;

import com.cuidadoeterno.backend.modules.finanzas.model.TransaccionBilletera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransaccionBilleteraRepository extends JpaRepository<TransaccionBilletera, Integer> {
    
    // Para mostrarle al cuidador su historial de pagos, ordenado del más reciente al más antiguo
    List<TransaccionBilletera> findByBilletera_IdBilleteraOrderByFechaTransaccionDesc(Integer idBilletera);
}