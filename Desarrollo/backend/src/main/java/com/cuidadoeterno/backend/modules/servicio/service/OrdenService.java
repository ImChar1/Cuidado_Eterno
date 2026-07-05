package com.cuidadoeterno.backend.modules.servicio.service;

import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.Calificacion;
import com.cuidadoeterno.backend.modules.servicio.model.DetalleOrden;
import com.cuidadoeterno.backend.modules.servicio.model.RegistroEvidencia;
import com.cuidadoeterno.backend.modules.servicio.model.enums.SubEstadoOrden;

import java.util.List;

public interface OrdenService {
    DetalleOrden crearNuevaOrden(OrdenRequestDTO request);
    List<OrdenResponseDTO> obtenerHistorialCliente(Integer idCliente);
    RegistroEvidencia subirEvidencia(EvidenciaRequestDTO request);
    Calificacion calificarServicio(CalificacionRequestDTO request);
    DetalleOrden aceptarSolicitud(Integer idOrden, Integer idCuidador);
    List<OrdenResponseDTO> obtenerSolicitudesDisponibles(Integer idCementerio);
    List<OrdenResponseDTO> obtenerHistorialCuidador(Integer idCuidador);
    DetalleOrden finalizarServicioCuidador(Integer idOrden);
    DetalleOrden actualizarSubEstado(Integer idOrden, Integer idCuidador, SubEstadoOrden nuevoSubEstado);
    List<TipoSolicitudResponseDTO> obtenerTodosLosTipos();
}