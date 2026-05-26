package com.cuidadoeterno.backend.modules.usuario.repository;

import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la tabla CUIDADOR.
 *
 * Además de las operaciones básicas, expone consultas
 * relevantes para asignación de servicios y disponibilidad.
 */
@Repository
public interface CuidadorRepository extends JpaRepository<Cuidador, Integer> {

    /**
     * Busca un cuidador por el nombre_usuario de su credencial.
     * Mismo patrón que ClienteRepository, navega la jerarquía JPA.
     */
    @Query("SELECT c FROM Cuidador c WHERE c.credencial.nombreUsuario = :nombreUsuario")
    Optional<Cuidador> findByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

    /**
     * Devuelve todos los cuidadores disponibles y verificados.
     * Usado al asignar un cuidador a una nueva orden.
     *
     * SELECT * FROM CUIDADOR
     * WHERE estado_disponibilidad = 'disponible'
     * AND estado_verificacion = 'verificado'
     */
    List<Cuidador> findByEstadoDisponibilidadAndEstadoVerificacion(
        String estadoDisponibilidad,
        String estadoVerificacion
    );

    /**
     * Busca cuidadores por estado de verificación.
     * Útil para el panel del administrador (ver pendientes de verificar).
     */
    List<Cuidador> findByEstadoVerificacion(String estadoVerificacion);
}