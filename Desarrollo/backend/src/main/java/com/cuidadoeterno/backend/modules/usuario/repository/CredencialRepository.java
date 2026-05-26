package com.cuidadoeterno.backend.modules.usuario.repository;

import com.cuidadoeterno.backend.modules.usuario.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la tabla CREDENCIAL.
 *
 * Es el repositorio más importante del módulo usuario porque
 * UserDetailsServiceImpl lo usa para cargar credenciales durante el login.
 *
 * JpaRepository<Credencial, Integer> provee automáticamente:
 * save(), findById(), findAll(), delete(), existsById(), etc.
 */
@Repository
public interface CredencialRepository extends JpaRepository<Credencial, Integer> {

    /**
     * Busca una credencial por nombre de usuario.
     * Usado por UserDetailsServiceImpl en cada autenticación.
     *
     * Spring Data traduce esto a:
     * SELECT * FROM CREDENCIAL WHERE nombre_usuario = ?
     *
     * @param nombreUsuario valor del campo nombre_usuario
     * @return Optional vacío si no existe, para manejar el caso sin excepciones
     */
    Optional<Credencial> findByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si ya existe un nombre de usuario antes de registrar.
     * Usado en AuthServiceImpl para evitar duplicados.
     *
     * SELECT COUNT(*) > 0 FROM CREDENCIAL WHERE nombre_usuario = ?
     */
    boolean existsByNombreUsuario(String nombreUsuario);
}