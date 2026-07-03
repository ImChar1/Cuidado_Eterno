package com.cuidadoeterno.backend.modules.usuario.service;
 
import com.cuidadoeterno.backend.modules.usuario.dto.*;
import com.cuidadoeterno.backend.modules.usuario.model.*;
import com.cuidadoeterno.backend.modules.usuario.repository.*;
import com.cuidadoeterno.backend.shared.exception.BusinessException;
import com.cuidadoeterno.backend.shared.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
 
import java.time.LocalDate;
 
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
    private final S3Service s3Service;
 
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            RolRepository rolRepository,
            PersonaRepository personaRepository,
            CredencialRepository credencialRepository,
            ClienteRepository clienteRepository,
            CuidadorRepository cuidadorRepository,
            S3Service s3Service) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil               = jwtUtil;
        this.passwordEncoder       = passwordEncoder;
        this.rolRepository         = rolRepository;
        this.personaRepository     = personaRepository;
        this.credencialRepository  = credencialRepository;
        this.clienteRepository     = clienteRepository;
        this.cuidadorRepository    = cuidadorRepository;
        this.s3Service = s3Service;
    }
 
    // ── LOGIN ───────────────────────────────────────────────────────────────────
 
    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(dto.nombreUsuario(), dto.clave())
        );
 
        Credencial credencial = credencialRepository
            .findByNombreUsuario(dto.nombreUsuario())
            .orElseThrow(() -> new BusinessException(
                "Error al cargar credencial", HttpStatus.INTERNAL_SERVER_ERROR
            ));
 
        String nombreRol = credencial.getRol().getNombreRol();
 
        Persona persona = personaRepository
            .findByCredencial_IdCredencial(credencial.getIdCredencial())
            .orElseThrow(() -> new BusinessException(
                "No se encontró persona asociada", HttpStatus.INTERNAL_SERVER_ERROR
            ));
 
        String token = jwtUtil.generarToken(dto.nombreUsuario(), nombreRol);
 
        return new LoginResponseDTO(
            token, nombreRol,
            persona.getIdPersona(), persona.getNombre(),
            persona.getApPaterno(), persona.getEmail()
        );
    }
 
    // ── REGISTRO CLIENTE ────────────────────────────────────────────────────────
 
    @Override
    @Transactional
    public void registrarCliente(RegistroClienteDTO dto) {
        validarDuplicados(dto.rut(), dto.email(), dto.nombreUsuario());
 
        Rol rol = rolRepository.findByNombreRol("CLIENTE")
            .orElseThrow(() -> new BusinessException(
                "Rol CLIENTE no configurado", HttpStatus.INTERNAL_SERVER_ERROR
            ));
 
        Credencial credencial = new Credencial();
        credencial.setNombreUsuario(dto.nombreUsuario());
        credencial.setClaveHash(passwordEncoder.encode(dto.clave()));
        credencial.setRol(rol);
        credencial.setEstadoCuenta("activa");
        credencial.setIntentosFallidos(0);
 
        Cliente cliente = new Cliente();
        cliente.setRut(dto.rut());
        cliente.setNombre(dto.nombre());
        cliente.setApPaterno(dto.apPaterno());
        cliente.setApMaterno(dto.apMaterno());
        cliente.setEmail(dto.email());
        cliente.setTelefono(dto.telefono());
        cliente.setFechaNacimiento(dto.fechaNacimiento());
        cliente.setGenero(dto.genero());
        cliente.setCredencial(credencial);
        cliente.setFechaRegistro(LocalDate.now());
        cliente.setPrefNotificacion(
            dto.prefNotificacion() != null ? dto.prefNotificacion() : "email"
        );
        cliente.setEstadoCliente("activo");
 
        clienteRepository.save(cliente);
    }
 
    // ── REGISTRO CUIDADOR ───────────────────────────────────────────────────────
 
    @Override
    @Transactional
    public void registrarCuidador(RegistroCuidadorDTO dto, MultipartFile documento) { // <--- Modificado
        validarDuplicados(dto.rut(), dto.email(), dto.nombreUsuario());

        Rol rol = rolRepository.findByNombreRol("CUIDADOR")
            .orElseThrow(() -> new BusinessException(
                "Rol CUIDADOR no configurado", HttpStatus.INTERNAL_SERVER_ERROR
            ));

        // 1. Subir documento a S3 y obtener la URL pública
        String urlDocumentoS3 = s3Service.subirArchivo(documento, "documentos/cuidadores/");

        // 2. Crear credencial
        Credencial credencial = new Credencial();
        credencial.setNombreUsuario(dto.nombreUsuario());
        credencial.setClaveHash(passwordEncoder.encode(dto.clave()));
        credencial.setRol(rol);
        credencial.setEstadoCuenta("activa");
        credencial.setIntentosFallidos(0);

        // 3. Crear cuidador
        Cuidador cuidador = new Cuidador();
        cuidador.setRut(dto.rut());
        cuidador.setNombre(dto.nombre());
        cuidador.setApPaterno(dto.apPaterno());
        cuidador.setApMaterno(dto.apMaterno());
        cuidador.setEmail(dto.email());
        cuidador.setTelefono(dto.telefono());
        cuidador.setFechaNacimiento(dto.fechaNacimiento());
        cuidador.setGenero(dto.genero());
        cuidador.setCredencial(credencial);
        cuidador.setFechaIngreso(LocalDate.now());
        cuidador.setEstadoVerificacion("pendiente");
        cuidador.setEstadoDisponibilidad("disponible");
        
        // 4. Asignar datos de certificación y la URL de S3
        cuidador.setUrlCertificacion(urlDocumentoS3); // <--- Usamos la URL generada
        cuidador.setTipoDocumento(dto.tipoDocumento());
        cuidador.setNumeroRegistro(dto.numeroRegistro());

        cuidadorRepository.save(cuidador);
    }
 
    // ── PERFIL ──────────────────────────────────────────────────────────────────
 
    @Override
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfil(String nombreUsuario) {
        Credencial credencial = credencialRepository
            .findByNombreUsuario(nombreUsuario)
            .orElseThrow(() -> new BusinessException(
                "Usuario no encontrado", HttpStatus.NOT_FOUND
            ));
 
        Persona persona = personaRepository
            .findByCredencial_IdCredencial(credencial.getIdCredencial())
            .orElseThrow(() -> new BusinessException(
                "Perfil no encontrado", HttpStatus.NOT_FOUND
            ));
 
        return construirPerfil(persona, credencial);
    }
 
    @Override
    @Transactional(readOnly = true)
    public PerfilDTO obtenerPerfilPorId(Integer idPersona) {
        Persona persona = personaRepository.findById(idPersona)
            .orElseThrow(() -> new BusinessException(
                "Usuario con id " + idPersona + " no encontrado",
                HttpStatus.NOT_FOUND
            ));
 
        return construirPerfil(persona, persona.getCredencial());
    }
 
    // ── Métodos privados ────────────────────────────────────────────────────────
 
    private PerfilDTO construirPerfil(Persona persona, Credencial credencial) {
        String rol = credencial.getRol().getNombreRol();
 
        PerfilDTO.Builder builder = PerfilDTO.builder()
            .idPersona(persona.getIdPersona())
            .nombre(persona.getNombre())
            .apPaterno(persona.getApPaterno())
            .apMaterno(persona.getApMaterno())
            .email(persona.getEmail())
            .telefono(persona.getTelefono())
            .fechaNacimiento(persona.getFechaNacimiento())
            .genero(persona.getGenero())
            .rol(rol)
            .nombreUsuario(credencial.getNombreUsuario())
            .estadoCuenta(credencial.getEstadoCuenta());
 
        if (persona instanceof Cliente c) {
            builder
                .fechaRegistro(c.getFechaRegistro())
                .prefNotificacion(c.getPrefNotificacion())
                .estadoCliente(c.getEstadoCliente());
 
        } else if (persona instanceof Cuidador c) {
            builder
                .calificacionPromedio(c.getCalificacionPromedio())
                .estadoVerificacion(c.getEstadoVerificacion())
                .estadoDisponibilidad(c.getEstadoDisponibilidad())
                .fechaIngresoCuidador(c.getFechaIngreso());
 
        } else if (persona instanceof Administrador a) {
            builder
                .nivelAcceso(a.getNivelAcceso())
                .cargo(a.getCargo())
                .fechaIngresoAdmin(a.getFechaIngreso());
        }
 
        return builder.build();
    }
 
    private void validarDuplicados(String rut, String email, String nombreUsuario) {
        if (personaRepository.existsByRut(rut)) {
            throw new BusinessException(
                "El RUT " + rut + " ya está registrado", HttpStatus.CONFLICT
            );
        }
        if (personaRepository.existsByEmail(email)) {
            throw new BusinessException(
                "El email " + email + " ya está registrado", HttpStatus.CONFLICT
            );
        }
        if (credencialRepository.existsByNombreUsuario(nombreUsuario)) {
            throw new BusinessException(
                "El nombre de usuario '" + nombreUsuario + "' ya está en uso",
                HttpStatus.CONFLICT
            );
        }
    }
}