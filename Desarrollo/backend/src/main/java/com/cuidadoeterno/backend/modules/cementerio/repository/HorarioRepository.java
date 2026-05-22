package com.cuidadoeterno.backend.modules.cementerio.repository;

import com.cuidadoeterno.backend.modules.cementerio.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la tabla HORARIO.
 * Usado por AuthServiceImpl al registrar un Cuidador
 * para verificar que el horario asignado existe.
 */
@Repository
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    /**
     * Devuelve todos los horarios disponibles.
     * Útil para que el administrador vea qué horarios
     * puede asignar al registrar un cuidador.
     */
    List<Horario> findByEstadoDisponibilidadTrue();

    /**
     * Busca horarios por día de la semana.
     * Ejemplo: findByDiaSemana("lunes")
     */
    List<Horario> findByDiaSemana(String diaSemana);
}