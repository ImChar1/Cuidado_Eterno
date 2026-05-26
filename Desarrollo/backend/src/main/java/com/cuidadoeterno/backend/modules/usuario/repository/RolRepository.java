package com.cuidadoeterno.backend.modules.usuario.repository;

import com.cuidadoeterno.backend.modules.usuario.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla ROL.
 *
 * Principalmente usado en AuthServiceImpl al registrar un usuario:
 * necesitamos buscar el Rol por nombre para asignárselo
 * a la Credencial antes de guardarla.
 *
 * Los roles se insertan manualmente en la BD (son datos semilla),
 * no se crean desde la API.
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol por su nombre.
     * Ejemplo de uso en registro:
     *   Rol rol = rolRepository.findByNombreRol("CLIENTE")
     *       .orElseThrow(() -> new BusinessException("Rol no encontrado"));
     *
     * SELECT * FROM ROL WHERE nombre_rol = ?
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}