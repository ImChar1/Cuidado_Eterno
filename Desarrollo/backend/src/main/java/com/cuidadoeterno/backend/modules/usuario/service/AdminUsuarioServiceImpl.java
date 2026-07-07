package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.modules.usuario.dto.CuidadorAdminResponse; // <-- NUEVO IMPORT
import com.cuidadoeterno.backend.modules.usuario.dto.PerfilDTO;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.modules.usuario.repository.CuidadorRepository;
import com.cuidadoeterno.backend.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUsuarioServiceImpl implements AdminUsuarioService {

    private final CuidadorRepository cuidadorRepository;

    public AdminUsuarioServiceImpl(CuidadorRepository cuidadorRepository) {
        this.cuidadorRepository = cuidadorRepository;
    }

    // ACTUALIZADO: Ahora retorna List<CuidadorAdminResponse>
    @Override
    @Transactional(readOnly = true)
    public List<CuidadorAdminResponse> listarCuidadoresPorEstado(String estado) {
        return cuidadorRepository.findByEstadoVerificacion(estado)
                .stream()
                .map(c -> CuidadorAdminResponse.builder()
                        .idPersona(c.getIdPersona())
                        .rut(c.getRut()) // Asegúrate de que el getter en Persona se llame así
                        .nombre(c.getNombre())
                        .apPaterno(c.getApPaterno())
                        .email(c.getEmail())
                        .telefono(c.getTelefono())
                        .estadoVerificacion(c.getEstadoVerificacion())
                        .fechaRegistro(c.getFechaIngreso() != null ? c.getFechaIngreso().toString() : null) // <-- CORREGIDO AQUÍ
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cambiarEstadoVerificacion(Integer idPersona, String nuevoEstado) {
        Cuidador cuidador = cuidadorRepository.findById(idPersona)
                .orElseThrow(() -> new BusinessException("Cuidador no encontrado", HttpStatus.NOT_FOUND));
        cuidador.setEstadoVerificacion(nuevoEstado.toLowerCase());
        cuidadorRepository.save(cuidador);
    }

    @Override
    @Transactional
    public void eliminarCuidador(Integer idPersona) {
        if (!cuidadorRepository.existsById(idPersona)) {
            throw new BusinessException("El cuidador no existe", HttpStatus.NOT_FOUND);
        }
        cuidadorRepository.deleteById(idPersona);
    }

    @Override
    @Transactional
    public void modificarCuidador(Integer idPersona, PerfilDTO dto) {
        Cuidador cuidador = cuidadorRepository.findById(idPersona)
                .orElseThrow(() -> new BusinessException("Cuidador no encontrado", HttpStatus.NOT_FOUND));
        
        if (dto.getNombre() != null) cuidador.setNombre(dto.getNombre());
        if (dto.getEmail() != null) cuidador.setEmail(dto.getEmail());
        if (dto.getTelefono() != null) cuidador.setTelefono(dto.getTelefono());
        
        cuidadorRepository.save(cuidador);
    }
}