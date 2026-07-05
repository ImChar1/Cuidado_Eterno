package com.cuidadoeterno.backend.modules.servicio.service;
import com.cuidadoeterno.backend.modules.cementerio.dto.EspacioRequestDTO;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.model.Fallecido;
import com.cuidadoeterno.backend.modules.cementerio.repository.FallecidoRepository;
import com.cuidadoeterno.backend.modules.cementerio.service.EspacioService;
import com.cuidadoeterno.backend.modules.inventario.model.PuestoVenta;
import com.cuidadoeterno.backend.modules.inventario.repository.PuestoVentaRepository;
import com.cuidadoeterno.backend.modules.servicio.dto.*;
import com.cuidadoeterno.backend.modules.servicio.model.*;
import com.cuidadoeterno.backend.modules.servicio.model.enums.SubEstadoOrden;
import com.cuidadoeterno.backend.modules.servicio.repository.*;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.modules.usuario.repository.CuidadorRepository;
import com.cuidadoeterno.backend.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cuidadoeterno.backend.modules.finanzas.service.BilleteraService;

import java.math.BigDecimal;
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
    private final CuidadorRepository cuidadorRepository;
    private final FallecidoRepository fallecidoRepository;
    private final EspacioService espacioService;
    private final PuestoVentaRepository puestoVentaRepository;
    private final BilleteraService billeteraService;
    

    // ── CLIENTE: crea la solicitud ──────────────────────────────────────────────

    @Override
    @Transactional
    public DetalleOrden crearNuevaOrden(OrdenRequestDTO request) {

        TipoSolicitud tipo = tipoSolicitudRepository
            .findById(request.getIdTipoSolicitud())
            .orElseThrow(() -> new BusinessException("Tipo de solicitud no válido", HttpStatus.BAD_REQUEST));

        EspacioRequestDTO espacioRequest = new EspacioRequestDTO();
        espacioRequest.setIdCementerio(request.getIdCementerio());
        espacioRequest.setIdTipoEspacio(request.getIdTipoEspacio());
        espacioRequest.setSectorPabellon(request.getSectorPabellon());
        espacioRequest.setNumeroSepultura(request.getNumeroSepultura());
        espacioRequest.setPisoNivel(request.getPisoNivel());
        espacioRequest.setPasillo(request.getPasillo());
        espacioRequest.setMaterialPrincipal(request.getMaterialPrincipal());

        Espacio espacio = espacioService.registrarNuevoEspacio(espacioRequest);

        Fallecido fallecido = new Fallecido();
        fallecido.setEspacio(espacio);
        fallecido.setNombres(request.getNombres());
        fallecido.setApellidos(request.getApellidos());
        fallecido.setFechaNacimiento(request.getFechaNacimiento());
        fallecido.setFechaDefuncion(request.getFechaDefuncion());
        fallecido.setEpitafio(request.getEpitafio());
        fallecidoRepository.save(fallecido);

        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdCliente(request.getIdCliente());
        solicitud.setTipoSolicitud(tipo);
        solicitud.setTotalCompra(request.getMontoTotalServicio());
        solicitud.setEstadoSolicitud("pendiente");
        solicitudRepository.save(solicitud);

        DetalleOrden orden = new DetalleOrden();
        orden.setSolicitudServicio(solicitud);
        orden.setEspacio(espacio);
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setMontoTotal(request.getMontoTotalServicio());
        orden.setObservaciones(request.getObservaciones());
        orden.setEstadoOrden("pendiente");
        orden.setSubEstadoOrden(SubEstadoOrden.SIN_ASIGNAR); // <-- CORRECCIÓN: Inicialización
        orden = detalleOrdenRepository.save(orden);

        if (request.getInsumos() != null && !request.getInsumos().isEmpty()) {
            for (InsumoRequestDTO insumoDto : request.getInsumos()) {
                PuestoVenta puesto = puestoVentaRepository
                    .findById(insumoDto.getIdPuesto())
                    .orElseThrow(() -> new BusinessException("Puesto de venta no encontrado", HttpStatus.BAD_REQUEST));

                RetiroInsumo retiro = new RetiroInsumo();
                retiro.setDetalleOrden(orden);
                retiro.setPuestoVenta(puesto);
                retiro.setMontoTotal(insumoDto.getMontoTotal());
                retiro.setFechaRetiro(request.getFechaProgramada().minusHours(1));
                retiroInsumoRepository.save(retiro);
            }
        }

        return orden;
    }

    // ── CUIDADOR: acepta una solicitud disponible ───────────────────────────────

    @Override
    @Transactional
    public DetalleOrden aceptarSolicitud(Integer idOrden, Integer idCuidador) {

        DetalleOrden orden = detalleOrdenRepository.findById(idOrden)
            .orElseThrow(() -> new BusinessException("Orden no encontrada", HttpStatus.NOT_FOUND));

        if (!"pendiente".equals(orden.getEstadoOrden())) {
            throw new BusinessException("Esta solicitud ya fue tomada por otro cuidador", HttpStatus.CONFLICT);
        }

        Cuidador cuidador = cuidadorRepository.findById(idCuidador)
            .orElseThrow(() -> new BusinessException("Cuidador no encontrado", HttpStatus.NOT_FOUND));

        if (!"verificado".equals(cuidador.getEstadoVerificacion())) {
            throw new BusinessException("Tu cuenta aún no ha sido verificada por el administrador", HttpStatus.FORBIDDEN);
        }

        if (!"disponible".equals(cuidador.getEstadoDisponibilidad())) {
            throw new BusinessException("No puedes aceptar solicitudes mientras tienes una orden en proceso", HttpStatus.CONFLICT);
        }

        orden.setCuidador(cuidador);
        orden.setEstadoOrden("en_proceso");
        orden.setSubEstadoOrden(SubEstadoOrden.ASIGNADO); // <-- CORRECCIÓN: Cambia a ASIGNADO
        detalleOrdenRepository.save(orden);

        cuidador.setEstadoDisponibilidad("ocupado");
        cuidadorRepository.save(cuidador);

        SolicitudServicio solicitud = orden.getSolicitudServicio();
        solicitud.setEstadoSolicitud("en_proceso");
        solicitudRepository.save(solicitud);

        return orden;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoSolicitudResponseDTO> obtenerTodosLosTipos() {
        // Obtenemos todas las entidades de la BD y las transformamos a DTO
        return tipoSolicitudRepository.findAll().stream()
                .map(tipo -> new TipoSolicitudResponseDTO(
                        tipo.getIdTipoSolicitud(),
                        tipo.getNombreServicio(),
                        tipo.getDescripcion(),
                        tipo.getPrecioBase(),
                        tipo.getDuracionEstimadaMin(),
                        tipo.isRequiereInsumos()
                ))
                .collect(Collectors.toList());
    }

    // ── LECTURAS Y LISTADOS ─────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerHistorialCliente(Integer idCliente) {
        List<SolicitudServicio> solicitudes = solicitudRepository.findByIdClienteOrderByFechaSolicitudDesc(idCliente);

        return solicitudes.stream().map(solicitud -> {
            Optional<DetalleOrden> ordenOpt = detalleOrdenRepository.findBySolicitudServicioIdSolicitud(solicitud.getIdSolicitud());

            if (ordenOpt.isEmpty()) {
                return new OrdenResponseDTO(
                    null, solicitud.getIdSolicitud(), solicitud.getTipoSolicitud().getNombreServicio(),
                    "Sin asignar", null, solicitud.getFechaSolicitud(), null,
                    solicitud.getEstadoSolicitud(), solicitud.getTotalCompra(), false, false, SubEstadoOrden.SIN_ASIGNAR
                );
            }

            DetalleOrden orden = ordenOpt.get();
            String nombreCuidador = orden.getCuidador() != null
                ? orden.getCuidador().getNombre() + " " + orden.getCuidador().getApPaterno()
                : "Sin asignar";

            boolean tieneEvidencia = evidenciaRepository.existsByDetalleOrdenIdOrden(orden.getIdOrden());
            boolean tieneCalificacion = calificacionRepository.findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent();

            return new OrdenResponseDTO(
                orden.getIdOrden(), solicitud.getIdSolicitud(), solicitud.getTipoSolicitud().getNombreServicio(),
                nombreCuidador, orden.getEspacio().getSectorPabellon() + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(), orden.getFechaProgramada(), orden.getEstadoOrden(),
                orden.getMontoTotal(), tieneEvidencia, tieneCalificacion, orden.getSubEstadoOrden() // <-- INCLUIDO AQUÍ
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerSolicitudesDisponibles(Integer idCementerio) {
        List<DetalleOrden> ordenesPendientes = detalleOrdenRepository.findByEstadoOrdenAndEspacioCementerioIdCementerio("pendiente", idCementerio);

        return ordenesPendientes.stream().map(orden -> {
            SolicitudServicio solicitud = orden.getSolicitudServicio();
            return new OrdenResponseDTO(
                orden.getIdOrden(), solicitud.getIdSolicitud(), solicitud.getTipoSolicitud().getNombreServicio(),
                "Sin asignar", orden.getEspacio().getSectorPabellon() + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(), orden.getFechaProgramada(), orden.getEstadoOrden(),
                orden.getMontoTotal(), false, false, orden.getSubEstadoOrden() // <-- INCLUIDO AQUÍ
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerHistorialCuidador(Integer idCuidador) {
        List<DetalleOrden> ordenes = detalleOrdenRepository.findByCuidadorIdPersonaOrderByFechaCreacionDesc(idCuidador);

        return ordenes.stream().map(orden -> {
            SolicitudServicio solicitud = orden.getSolicitudServicio();
            boolean tieneEvidencia = evidenciaRepository.existsByDetalleOrdenIdOrden(orden.getIdOrden());
            boolean tieneCalificacion = calificacionRepository.findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent();

            return new OrdenResponseDTO(
                orden.getIdOrden(), solicitud.getIdSolicitud(), solicitud.getTipoSolicitud().getNombreServicio(),
                "Cliente #" + solicitud.getIdCliente(), orden.getEspacio().getSectorPabellon() + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(), orden.getFechaProgramada(), orden.getEstadoOrden(),
                orden.getMontoTotal(), tieneEvidencia, tieneCalificacion, orden.getSubEstadoOrden() // <-- INCLUIDO AQUÍ
            );
        }).collect(Collectors.toList());
    }

    // ── ACCIONES EN PROCESO ─────────────────────────────────────────────────────

    @Override
    @Transactional
    public RegistroEvidencia subirEvidencia(EvidenciaRequestDTO request) {
        DetalleOrden orden = detalleOrdenRepository.findById(request.getIdOrden())
            .orElseThrow(() -> new BusinessException("Orden no encontrada", HttpStatus.NOT_FOUND));

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
            .orElseThrow(() -> new BusinessException("Orden no encontrada", HttpStatus.NOT_FOUND));

        if (calificacionRepository.findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent()) {
            throw new BusinessException("Este servicio ya fue calificado", HttpStatus.CONFLICT);
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setDetalleOrden(orden);
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());

        orden.setEstadoOrden("completada");
        detalleOrdenRepository.save(orden);

        Cuidador cuidador = orden.getCuidador();
        if (cuidador != null) {
            cuidador.setEstadoDisponibilidad("disponible");
            cuidadorRepository.save(cuidador);
        }

        return calificacionRepository.save(calificacion);
    }

    @Override
    @Transactional
    public DetalleOrden finalizarServicioCuidador(Integer idOrden) {
        DetalleOrden orden = detalleOrdenRepository.findById(idOrden)
            .orElseThrow(() -> new BusinessException("Orden no encontrada", HttpStatus.NOT_FOUND));

        orden.setEstadoOrden("completada"); 
        detalleOrdenRepository.save(orden);

        BigDecimal ganancia = orden.getSolicitudServicio().getTotalCompra();
        billeteraService.abonarPagoPorServicio(orden.getCuidador().getIdPersona(), idOrden, ganancia);

        Cuidador cuidador = orden.getCuidador();
        if (cuidador != null) {
            cuidador.setEstadoDisponibilidad("disponible");
            cuidadorRepository.save(cuidador);
        }

        return orden;
    }

    // ── NUEVO: CUIDADOR ACTUALIZA SUB-ESTADO EN VIVO ───────────────────────────

    @Override
    @Transactional
    public DetalleOrden actualizarSubEstado(Integer idOrden, Integer idCuidador, SubEstadoOrden nuevoSubEstado) {
        DetalleOrden orden = detalleOrdenRepository.findById(idOrden)
            .orElseThrow(() -> new BusinessException("Orden no encontrada", HttpStatus.NOT_FOUND));

        // Por seguridad, verificamos que el cuidador que intenta cambiar el estado es el dueño de la orden
        if (orden.getCuidador() == null || !orden.getCuidador().getIdPersona().equals(idCuidador)) {
            throw new BusinessException("No tienes permiso para modificar esta orden o no está asignada", HttpStatus.FORBIDDEN);
        }

        orden.setSubEstadoOrden(nuevoSubEstado);
        return detalleOrdenRepository.save(orden);
    }
}