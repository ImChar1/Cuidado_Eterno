package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Fallecido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FallecidoRepository extends JpaRepository<Fallecido, Integer> {

    // Busca quiénes están sepultados en una tumba/espacio específico
    List<Fallecido> findByEspacioIdEspacio(Integer idEspacio);

    // Buscador por nombre (útil si el cliente no recuerda exactamente el número de la tumba)
    List<Fallecido> findByNombreCompletoContainingIgnoreCase(String nombre);
}