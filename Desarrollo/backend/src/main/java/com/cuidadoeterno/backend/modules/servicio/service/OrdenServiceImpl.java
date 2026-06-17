package com.cuidadoeterno.backend.modules.servicio.service;

import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.repository.EspacioRepository;
import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta;
import com.cuidadoeterno.backend.modules.inventario.repository.PuestoVentaRepository;
import com.cuidadoeterno.backend.modules.usuario.repository.CuidadorRepository;
import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.*;
import com.cuidadoeterno.backend.modules.servicio.repository.*;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.shared.exception.BusinessException;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenServiceImpl implements OrdenService {

    private final SolicitudServicioRepository solicitudRepository;
    private final DetalleOrdenRepository detalleOrdenRepository;
    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final RetiroInsumoRepository retiroInsumoRepository;
    private final RegistroEvidenciaRepository evidenciaRepository;
    private final CalificacionRepository calificacionRepository;
    
    // Repositorios de otros módulos
    private final EspacioRepository espacioRepository;
    private final PuestoVentaRepository puestoVentaRepository;
    private final CuidadorRepository cuidadorRepository;

    @Override
    @Transactional
    public DetalleOrden crearNuevaOrden(OrdenRequestDTO request) {
        // 1. Validar Tipo de Solicitud y Espacio
        TipoSolicitud tipo = tipoSolicitudRepository.findById(request.getIdTipoSolicitud())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de solicitud no válido"));
        Espacio espacio = espacioRepository.findById(request.getIdEspacio())
                .orElseThrow(() -> new IllegalArgumentException("El espacio no existe"));
        Cuidador cuidador = cuidadorRepository.findById(request.getIdCuidador())
                .orElseThrow(() -> new IllegalArgumentException("Cuidador no encontrado"));

        if (!"verificado".equals(cuidador.getEstadoVerificacion())) {
            throw new BusinessException(
                "Tu cuenta está pendiente de verificación por el administrador",
                HttpStatus.FORBIDDEN
            );
        }

        // 2. Crear la Cabecera de la Solicitud
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdCliente(request.getIdCliente());
        solicitud.setTipoSolicitud(tipo);
        solicitudRepository.save(solicitud);

        // 3. Crear el Detalle de la Orden
        DetalleOrden orden = new DetalleOrden();
        orden.setEspacio(espacio);
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setMontoTotal(request.getMontoTotalServicio());
        orden.setObservaciones(request.getObservaciones());
        // En un caso real, aquí asociaríamos orden a solicitud si existe la relación en el modelo
        orden = detalleOrdenRepository.save(orden);

        // 4. Procesar compras de insumos (flores, velas) si es que hay
        if (request.getInsumos() != null && !request.getInsumos().isEmpty()) {
            for (InsumoRequestDTO insumoDto : request.getInsumos()) {
                PuestoVenta puesto = puestoVentaRepository.findById(insumoDto.getIdPuesto())
                        .orElseThrow(() -> new IllegalArgumentException("Puesto de venta no encontrado"));

                RetiroInsumo retiro = new RetiroInsumo();
                retiro.setDetalleOrden(orden);
                retiro.setPuestoVenta(puesto);
                retiro.setMontoTotal(insumoDto.getMontoTotal());
                retiro.setFechaRetiro(request.getFechaProgramada().minusHours(1)); // El retiro se agenda 1 hora antes
                retiroInsumoRepository.save(retiro);
            }
        }

        return orden;
    }

    @Override
@Transactional(readOnly = true)
public List<OrdenResponseDTO> obtenerHistorialCliente(Integer idCliente) {
    List<SolicitudServicio> solicitudes = solicitudRepository
        .findByIdClienteOrderByFechaSolicitudDesc(idCliente);

    return solicitudes.stream().map(solicitud -> {
        // Buscar el detalle de orden asociado a esta solicitud
        Optional<DetalleOrden> ordenOpt = detalleOrdenRepository
            .findBySolicitudServicioIdSolicitud(solicitud.getIdSolicitud());

        if (ordenOpt.isEmpty()) {
            // La solicitud existe pero aún no tiene cuidador asignado
            return new OrdenResponseDTO(
                null,
                solicitud.getIdSolicitud(),
                solicitud.getTipoSolicitud().getNombreServicio(),
                "Sin asignar",
                null,
                solicitud.getFechaSolicitud(),
                null,
                solicitud.getEstadoSolicitud(),
                solicitud.getTotalCompra(),
                false,
                false
            );
        }

        DetalleOrden orden = ordenOpt.get();
        String nombreCuidador = orden.getCuidador().getNombre()
            + " " + orden.getCuidador().getApPaterno();

        boolean tieneEvidencia = evidenciaRepository
            .existsByDetalleOrdenIdOrden(orden.getIdOrden());

        boolean tieneCalificacion = calificacionRepository
            .findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent();

        return new OrdenResponseDTO(
            orden.getIdOrden(),
            solicitud.getIdSolicitud(),
            solicitud.getTipoSolicitud().getNombreServicio(),
            nombreCuidador,
            orden.getEspacio().getSectorPabellon() + " - N°" + orden.getEspacio().getNumeroSepultura(),
            orden.getFechaCreacion(),
            orden.getFechaProgramada(),
            orden.getEstadoOrden(),
            orden.getMontoTotal(),
            tieneEvidencia,
            tieneCalificacion
        );
    }).collect(Collectors.toList());
}

    @Override
    @Transactional
    public RegistroEvidencia subirEvidencia(EvidenciaRequestDTO request) {
        DetalleOrden orden = detalleOrdenRepository.findById(request.getIdOrden())
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        RegistroEvidencia evidencia = new RegistroEvidencia();
        evidencia.setDetalleOrden(orden);
        evidencia.setUrlFoto(request.getUrlFoto());
        evidencia.setTipoMomento(request.getTipoMomento());
        evidencia.setDescripcionEstado(request.getDescripcionEstado());
        
        return evidenciaRepository.save(evidencia);
    }

    @Override
    @Transactional
    public Calificacion calificarServicio(CalificacionRequestDTO request) {
        DetalleOrden orden = detalleOrdenRepository.findById(request.getIdOrden())
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));

        if (calificacionRepository.findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent()) {
            throw new IllegalArgumentException("Este servicio ya fue calificado");
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setDetalleOrden(orden);
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());

        // Al calificar, marcamos la orden como completada (cierre del ciclo)
        orden.setEstadoOrden("completada");
        detalleOrdenRepository.save(orden);

        Cuidador cuidador = orden.getCuidador();
        cuidador.setEstadoDisponibilidad("disponible");
        cuidadorRepository.save(cuidador);

        return calificacionRepository.save(calificacion);
    }

    
}