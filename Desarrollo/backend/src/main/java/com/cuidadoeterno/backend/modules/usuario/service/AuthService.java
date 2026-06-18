package com.cuidadoeterno.backend.modules.usuario.service;

import java.util.List;

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
     * Público — el cuidador se auto-registra y queda en estado 'pendiente'.
     * El administrador lo verifica después con cambiarEstadoVerificacion().
     *
     * Ya no recibe idHorario. El cuidador declara su disponibilidad
     * (días y rango horario) y la validación contra el horario del
     * cementerio ocurre al momento de aceptar una solicitud.
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

    // ── Administración de cuidadores ────────────────────────────────────────────
 
    /**
     * Cambia el estado de verificación de un cuidador.
     * Solo accesible por ADMINISTRADOR.
     *
     * @param idPersona  PK del cuidador en tabla PERSONA
     * @param nuevoEstado  'verificado' o 'rechazado'
     */
    void cambiarEstadoVerificacion(Integer idPersona, String nuevoEstado);
 
    /**
     * Lista cuidadores filtrados por estado de verificación.
     * Usado por AdminController para ver pendientes, verificados o rechazados.
     *
     * @param estado  'pendiente', 'verificado' o 'rechazado'
     * @return lista de PerfilDTO con datos del cuidador
     */
    List<PerfilDTO> listarCuidadoresPorEstado(String estado);
}