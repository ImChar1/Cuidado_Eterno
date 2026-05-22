package com.cuidadoeterno.backend.modules.usuario.repository;

import com.cuidadoeterno.backend.modules.usuario.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla PERSONA.
 *
 * Opera sobre la entidad padre de la jerarquía.
 * Cuando se guarda un Cliente o Cuidador a través de sus propios
 * repositorios, JPA inserta en PERSONA y en la tabla del subtipo
 * automáticamente gracias a la herencia JOINED.
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    /**
     * Verifica si ya existe un RUT antes de registrar.
     * El RUT es único en Chile, no puede repetirse entre personas.
     *
     * SELECT COUNT(*) > 0 FROM PERSONA WHERE rut = ?
     */
    boolean existsByRut(String rut);

    /**
     * Verifica si ya existe un email antes de registrar.
     *
     * SELECT COUNT(*) > 0 FROM PERSONA WHERE email = ?
     */
    boolean existsByEmail(String email);

     Optional<Persona> findByEmail(String email);
     
    /**
     * Busca una persona por email.
     * Útil para recuperación de contraseña o búsqueda por contacto.
     */
    Optional<Persona> findByCredencial_IdCredencial(Integer idCredencial);
}