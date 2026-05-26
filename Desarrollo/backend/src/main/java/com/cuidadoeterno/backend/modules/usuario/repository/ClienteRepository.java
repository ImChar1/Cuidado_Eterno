package com.cuidadoeterno.backend.modules.usuario.repository;

import com.cuidadoeterno.backend.modules.usuario.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla CLIENTE.
 *
 * JpaRepository<Cliente, Integer> usa Integer porque el PK de Cliente
 * es id_persona heredado de Persona, que es Integer.
 *
 * Las consultas sobre Cliente hacen automáticamente JOIN con PERSONA
 * gracias a la herencia JOINED configurada en las entidades.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca un cliente por el nombre_usuario de su credencial.
     * Útil para cargar el perfil completo tras el login.
     *
     * La query navega: CLIENTE → PERSONA → CREDENCIAL → nombre_usuario
     * JPQL usa nombres de atributos Java, no nombres de columnas SQL.
     */
    @Query("SELECT c FROM Cliente c WHERE c.credencial.nombreUsuario = :nombreUsuario")
    Optional<Cliente> findByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

    /**
     * Busca un cliente por el email de la persona.
     * La query navega: CLIENTE → PERSONA → email
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Verifica si existe un cliente con ese estado.
     * Útil para filtros administrativos.
     */
    boolean existsByEstadoCliente(String estadoCliente);
}