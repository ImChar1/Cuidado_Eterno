package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.modules.usuario.dto.LoginRequestDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.LoginResponseDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.PerfilDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroClienteDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroCuidadorDTO;

public interface AuthService {

    /**
     * Autentica un usuario y devuelve un token JWT.
     *
     * @param dto credenciales enviadas desde Android
     * @return token JWT + datos básicos del usuario para el ViewModel
     */
    LoginResponseDTO login(LoginRequestDTO dto);

    /**
     * Registra un nuevo cliente.
     * Crea filas en PERSONA + CREDENCIAL + CLIENTE de forma transaccional.
     *
     * @param dto datos del formulario de registro
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si el RUT, email o nombre de usuario ya existen (409)
     */
    void registrarCliente(RegistroClienteDTO dto);

    /**
     * Registra un nuevo cuidador.
     * Solo accesible por ADMINISTRADOR (@PreAuthorize en el controller).
     * Crea filas en PERSONA + CREDENCIAL + CUIDADOR de forma transaccional.
     *
     * @param dto datos del cuidador incluyendo id del horario asignado
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si hay duplicados (409) o el horario no existe (404)
     */
    void registrarCuidador(RegistroCuidadorDTO dto);

    /**
     * Devuelve el perfil completo del usuario autenticado.
     * Los campos del PerfilDTO varían según el rol:
     * - CLIENTE:        datos personales + estado + preferencia notificación
     * - CUIDADOR:       datos personales + calificación + disponibilidad
     * - ADMINISTRADOR:  datos personales + cargo + nivel acceso
     *
     * @param nombreUsuario extraído del token JWT en el controller
     * @return PerfilDTO con los campos correspondientes al rol
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si el usuario no se encuentra (404)
     */
    PerfilDTO obtenerPerfil(String nombreUsuario);

    /**
     * Devuelve el perfil de cualquier usuario por su id_persona.
     * Solo accesible por ADMINISTRADOR.
     *
     * @param idPersona PK de la tabla PERSONA
     * @return PerfilDTO con los campos del rol del usuario consultado
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si el id no existe (404)
     */
    PerfilDTO obtenerPerfilPorId(Integer idPersona);
}