package com.cuidadoeterno.backend.modules.usuario.service;

import com.cuidadoeterno.backend.modules.usuario.dto.CuidadorAdminResponse;
import com.cuidadoeterno.backend.modules.usuario.dto.PerfilDTO;
import java.util.List;

public interface AdminUsuarioService {
    List<CuidadorAdminResponse> listarCuidadoresPorEstado(String estado);
    void cambiarEstadoVerificacion(Integer idPersona, String nuevoEstado);
    void eliminarCuidador(Integer idPersona);
    void modificarCuidador(Integer idPersona, PerfilDTO dto);
}