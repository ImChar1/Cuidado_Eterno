package com.cuidadoeterno.backend.modules.servicio.service;

import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.Calificacion;
import com.cuidadoeterno.backend.modules.servicio.model.DetalleOrden;
import com.cuidadoeterno.backend.modules.servicio.model.RegistroEvidencia;

import java.util.List;

public interface OrdenService {
    DetalleOrden crearNuevaOrden(OrdenRequestDTO request);
    List<OrdenResponseDTO> obtenerHistorialCliente(Integer idCliente);
    RegistroEvidencia subirEvidencia(EvidenciaRequestDTO request);
    Calificacion calificarServicio(CalificacionRequestDTO request);
}