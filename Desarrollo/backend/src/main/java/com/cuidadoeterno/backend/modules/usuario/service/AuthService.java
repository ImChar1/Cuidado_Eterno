package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.modules.usuario.dto.LoginRequestDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.LoginResponseDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroClienteDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroCuidadorDTO;

/**
 * Contrato del servicio de autenticación.
 *
 * Separar interfaz de implementación permite:
 * - Mockear fácilmente en tests unitarios
 * - Cambiar la implementación sin tocar el controller
 * - Documentar el contrato de forma explícita
 */
public interface AuthService {

    /**
     * Autentica un usuario y devuelve un token JWT.
     *
     * @param dto credenciales enviadas desde Android
     * @return token JWT + datos básicos del usuario para el ViewModel
     * @throws org.springframework.security.authentication.BadCredentialsException
     *         si las credenciales son incorrectas
     */
    LoginResponseDTO login(LoginRequestDTO dto);

    /**
     * Registra un nuevo cliente en el sistema.
     * Crea filas en PERSONA + CREDENCIAL + CLIENTE de forma transaccional.
     *
     * @param dto datos del formulario de registro desde Android
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si el RUT, email o nombre de usuario ya existen
     */
    void registrarCliente(RegistroClienteDTO dto);

    /**
     * Registra un nuevo cuidador en el sistema.
     * Solo accesible por ADMINISTRADOR (@PreAuthorize en el controller).
     * Crea filas en PERSONA + CREDENCIAL + CUIDADOR de forma transaccional.
     *
     * @param dto datos del cuidador incluyendo id del horario asignado
     * @throws com.cuidadoeterno.backend.shared.exception.BusinessException
     *         si el RUT, email o nombre de usuario ya existen,
     *         o si el horario no existe
     */
    void registrarCuidador(RegistroCuidadorDTO dto);
}