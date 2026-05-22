package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.modules.cementerio.model.Horario;
import com.cuidadoeterno.backend.modules.cementerio.repository.HorarioRepository;
import com.cuidadoeterno.backend.modules.usuario.dto.LoginRequestDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.LoginResponseDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroClienteDTO;
import com.cuidadoeterno.backend.modules.usuario.dto.RegistroCuidadorDTO;
import com.cuidadoeterno.backend.modules.usuario.model.*;
import com.cuidadoeterno.backend.modules.usuario.repository.*;
import com.cuidadoeterno.backend.shared.exception.BusinessException;
import com.cuidadoeterno.backend.shared.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Implementación del servicio de autenticación.
 *
 * Decisiones de diseño:
 * - @Transactional en registro: si falla cualquier INSERT, se hace rollback
 *   de todos (PERSONA, CREDENCIAL, CLIENTE/CUIDADOR quedan consistentes)
 * - El login NO es transaccional porque solo lee datos
 * - Las validaciones de duplicados van ANTES de cualquier persistencia
 *   para evitar depender de excepciones de constraint de la BD
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final PersonaRepository personaRepository;
    private final CredencialRepository credencialRepository;
    private final ClienteRepository clienteRepository;
    private final CuidadorRepository cuidadorRepository;
    private final HorarioRepository horarioRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            RolRepository rolRepository,
            PersonaRepository personaRepository,
            CredencialRepository credencialRepository,
            ClienteRepository clienteRepository,
            CuidadorRepository cuidadorRepository,
            HorarioRepository horarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository;
        this.personaRepository = personaRepository;
        this.credencialRepository = credencialRepository;
        this.clienteRepository = clienteRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.horarioRepository = horarioRepository;
    }

    // ── LOGIN ───────────────────────────────────────────────────────────────────

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        // 1. Delegar autenticación a Spring Security
        //    Internamente llama a UserDetailsServiceImpl.loadUserByUsername()
        //    y verifica la contraseña con Pbkdf2PasswordEncoder.
        //    Si falla lanza BadCredentialsException → GlobalExceptionHandler → 401
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.nombreUsuario(),
                dto.clave()
            )
        );

        // 2. Cargar credencial para obtener el rol y los datos de la persona
        Credencial credencial = credencialRepository
            .findByNombreUsuario(dto.nombreUsuario())
            .orElseThrow(() -> new BusinessException(
                "Error al cargar credencial", HttpStatus.INTERNAL_SERVER_ERROR
            ));

        String nombreRol = credencial.getRol().getNombreRol();

        // 3. Cargar persona para obtener nombre y email
        //    Buscamos por el id_persona asociado a la credencial
        Persona persona = personaRepository
            .findByEmail(credencial.getNombreUsuario())
            .orElseGet(() -> buscarPersonaPorCredencial(credencial));

        // 4. Generar token JWT con HMAC-SHA256
        String token = jwtUtil.generarToken(dto.nombreUsuario(), nombreRol);

        return new LoginResponseDTO(
            token,
            nombreRol,
            persona.getIdPersona(),
            persona.getNombre(),
            persona.getApPaterno(),
            persona.getEmail()
        );
    }

    // ── REGISTRO CLIENTE ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void registrarCliente(RegistroClienteDTO dto) {

        // 1. Validar duplicados antes de persistir
        validarDuplicados(dto.rut(), dto.email(), dto.nombreUsuario());

        // 2. Buscar rol CLIENTE (debe existir como dato semilla en la BD)
        Rol rol = rolRepository.findByNombreRol("CLIENTE")
            .orElseThrow(() -> new BusinessException(
                "Rol CLIENTE no configurado en el sistema",
                HttpStatus.INTERNAL_SERVER_ERROR
            ));

        // 3. Crear y guardar Credencial con contraseña hasheada (PBKDF2)
        Credencial credencial = new Credencial();
        credencial.setNombreUsuario(dto.nombreUsuario());
        credencial.setClaveHash(passwordEncoder.encode(dto.clave()));
        credencial.setRol(rol);
        credencial.setEstadoCuenta("activa");
        credencial.setIntentosFallidos(0);

        // 4. Crear Cliente (que extiende Persona)
        //    JPA inserta en PERSONA primero, luego en CLIENTE
        Cliente cliente = new Cliente();

        // Datos de PERSONA
        cliente.setRut(dto.rut());
        cliente.setNombre(dto.nombre());
        cliente.setApPaterno(dto.apPaterno());
        cliente.setApMaterno(dto.apMaterno());
        cliente.setEmail(dto.email());
        cliente.setTelefono(dto.telefono());
        cliente.setFechaNacimiento(dto.fechaNacimiento());
        cliente.setGenero(dto.genero());
        cliente.setCredencial(credencial);

        // Datos de CLIENTE
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setPrefNotificacion(
            dto.prefNotificacion() != null ? dto.prefNotificacion() : "email"
        );
        cliente.setEstadoCliente("activo");

        // 5. Guardar — CASCADE en Persona.credencial persiste la Credencial también
        clienteRepository.save(cliente);
    }

    // ── REGISTRO CUIDADOR ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public void registrarCuidador(RegistroCuidadorDTO dto) {

        // 1. Validar duplicados
        validarDuplicados(dto.rut(), dto.email(), dto.nombreUsuario());

        // 2. Verificar que el horario exista
        Horario horario = horarioRepository.findById(dto.idHorario())
            .orElseThrow(() -> new BusinessException(
                "El horario con id " + dto.idHorario() + " no existe",
                HttpStatus.NOT_FOUND
            ));

        // 3. Buscar rol CUIDADOR
        Rol rol = rolRepository.findByNombreRol("CUIDADOR")
            .orElseThrow(() -> new BusinessException(
                "Rol CUIDADOR no configurado en el sistema",
                HttpStatus.INTERNAL_SERVER_ERROR
            ));

        // 4. Crear Credencial
        Credencial credencial = new Credencial();
        credencial.setNombreUsuario(dto.nombreUsuario());
        credencial.setClaveHash(passwordEncoder.encode(dto.clave()));
        credencial.setRol(rol);
        credencial.setEstadoCuenta("activa");
        credencial.setIntentosFallidos(0);

        // 5. Crear Cuidador
        Cuidador cuidador = new Cuidador();

        // Datos de PERSONA
        cuidador.setRut(dto.rut());
        cuidador.setNombre(dto.nombre());
        cuidador.setApPaterno(dto.apPaterno());
        cuidador.setApMaterno(dto.apMaterno());
        cuidador.setEmail(dto.email());
        cuidador.setTelefono(dto.telefono());
        cuidador.setFechaNacimiento(dto.fechaNacimiento());
        cuidador.setGenero(dto.genero());
        cuidador.setCredencial(credencial);

        // Datos de CUIDADOR
        cuidador.setHorario(horario);
        cuidador.setFechaIngreso(LocalDate.now());
        cuidador.setEstadoVerificacion("pendiente");
        cuidador.setEstadoDisponibilidad("disponible");

        // 6. Guardar
        cuidadorRepository.save(cuidador);
    }

    // ── Métodos privados ────────────────────────────────────────────────────────

    /**
     * Valida que RUT, email y nombre de usuario no estén ya registrados.
     * Se ejecuta al inicio de cada registro para fallar rápido
     * antes de cualquier operación de escritura en la BD.
     *
     * @throws BusinessException con status 409 CONFLICT si hay duplicado
     */
    private void validarDuplicados(String rut, String email, String nombreUsuario) {
        if (personaRepository.existsByRut(rut)) {
            throw new BusinessException(
                "El RUT " + rut + " ya está registrado",
                HttpStatus.CONFLICT
            );
        }
        if (personaRepository.existsByEmail(email)) {
            throw new BusinessException(
                "El email " + email + " ya está registrado",
                HttpStatus.CONFLICT
            );
        }
        if (credencialRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new BusinessException(
                "El nombre de usuario '" + nombreUsuario + "' ya está en uso",
                HttpStatus.CONFLICT
            );
        }
    }

    /**
     * Busca la Persona asociada a una Credencial navegando la relación inversa.
     * Se usa en el login cuando el nombre_usuario no coincide con el email.
     */
    private Persona buscarPersonaPorCredencial(Credencial credencial) {
        return personaRepository
            .findByCredencial_IdCredencial(credencial.getIdCredencial())
            .orElseThrow(() -> new BusinessException(
                "No se encontró persona asociada a las credenciales",
                HttpStatus.INTERNAL_SERVER_ERROR
            ));
    }
}