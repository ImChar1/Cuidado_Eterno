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
import com.cuidadoeterno.backend.modules.servicio.repository.*;
import com.cuidadoeterno.backend.modules.usuario.model.Cuidador;
import com.cuidadoeterno.backend.modules.usuario.repository.CuidadorRepository;
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
    private final CuidadorRepository cuidadorRepository;
    private final FallecidoRepository fallecidoRepository;
    private final EspacioService espacioService;
    private final PuestoVentaRepository puestoVentaRepository;
 
    // ── CLIENTE: crea la solicitud ──────────────────────────────────────────────
 
    /**
     * Lo llama el CLIENTE al confirmar su solicitud desde la app.
     * Crea el espacio si no existe, registra el fallecido,
     * crea la SolicitudServicio y el DetalleOrden en estado "pendiente".
     * NO asigna cuidador — el cuidador la acepta después con aceptarSolicitud().
     */
    @Override
    @Transactional
    public DetalleOrden crearNuevaOrden(OrdenRequestDTO request) {
 
        // 1. Validar tipo de solicitud
        TipoSolicitud tipo = tipoSolicitudRepository
            .findById(request.getIdTipoSolicitud())
            .orElseThrow(() -> new BusinessException(
                "Tipo de solicitud no válido", HttpStatus.BAD_REQUEST
            ));
 
        // 2. Registrar o reutilizar el espacio del difunto
        // Si el sector + número + cementerio ya existen, retorna el existente
        // Si no existe, lo crea con los datos ingresados por el cliente
        EspacioRequestDTO espacioRequest = new EspacioRequestDTO();
        espacioRequest.setIdCementerio(request.getIdCementerio());
        espacioRequest.setIdTipoEspacio(request.getIdTipoEspacio());
        espacioRequest.setSectorPabellon(request.getSectorPabellon());
        espacioRequest.setNumeroSepultura(request.getNumeroSepultura());
        espacioRequest.setPisoNivel(request.getPisoNivel());
        espacioRequest.setPasillo(request.getPasillo());
        espacioRequest.setMaterialPrincipal(request.getMaterialPrincipal());
 
        Espacio espacio = espacioService.registrarNuevoEspacio(espacioRequest);
 
        // 3. Registrar el fallecido asociado al espacio
        // Siempre se registra aunque el espacio ya existiera,
        // porque puede ser un difunto distinto en el mismo espacio
        Fallecido fallecido = new Fallecido();
        fallecido.setEspacio(espacio);
        fallecido.setNombres(request.getNombres());
        fallecido.setApellidos(request.getApellidos());
        fallecido.setFechaNacimiento(request.getFechaNacimiento());
        fallecido.setFechaDefuncion(request.getFechaDefuncion());
        fallecido.setEpitafio(request.getEpitafio());
        fallecidoRepository.save(fallecido);
 
        // 4. Crear la SolicitudServicio — cabecera del pedido del cliente
        SolicitudServicio solicitud = new SolicitudServicio();
        solicitud.setIdCliente(request.getIdCliente());
        solicitud.setTipoSolicitud(tipo);
        solicitud.setTotalCompra(request.getMontoTotalServicio());
        solicitud.setEstadoSolicitud("pendiente");
        solicitudRepository.save(solicitud);
 
        // 5. Crear el DetalleOrden — la orden de trabajo sin cuidador asignado
        // El cuidador se asigna cuando acepta la solicitud (ver aceptarSolicitud)
        DetalleOrden orden = new DetalleOrden();
        orden.setSolicitudServicio(solicitud);
        orden.setEspacio(espacio);
        orden.setFechaProgramada(request.getFechaProgramada());
        orden.setMontoTotal(request.getMontoTotalServicio());
        orden.setObservaciones(request.getObservaciones());
        orden.setEstadoOrden("pendiente");
        // cuidador queda null hasta que alguien la acepte
        orden = detalleOrdenRepository.save(orden);
 
        // 6. Procesar insumos si el cliente agregó productos del catálogo
        if (request.getInsumos() != null && !request.getInsumos().isEmpty()) {
            for (InsumoRequestDTO insumoDto : request.getInsumos()) {
                PuestoVenta puesto = puestoVentaRepository
                    .findById(insumoDto.getIdPuesto())
                    .orElseThrow(() -> new BusinessException(
                        "Puesto de venta no encontrado", HttpStatus.BAD_REQUEST
                    ));
 
                RetiroInsumo retiro = new RetiroInsumo();
                retiro.setDetalleOrden(orden);
                retiro.setPuestoVenta(puesto);
                retiro.setMontoTotal(insumoDto.getMontoTotal());
                // El retiro de insumos se agenda 1 hora antes del servicio
                retiro.setFechaRetiro(request.getFechaProgramada().minusHours(1));
                retiroInsumoRepository.save(retiro);
            }
        }
 
        return orden;
    }
 
    // ── CUIDADOR: acepta una solicitud disponible ───────────────────────────────
 
    /**
     * Lo llama el CUIDADOR cuando decide tomar una solicitud pendiente.
     * Valida que el cuidador esté verificado y disponible,
     * asigna el cuidador al DetalleOrden y lo marca como ocupado.
     */
    @Override
    @Transactional
    public DetalleOrden aceptarSolicitud(Integer idOrden, Integer idCuidador) {
 
        DetalleOrden orden = detalleOrdenRepository.findById(idOrden)
            .orElseThrow(() -> new BusinessException(
                "Orden no encontrada", HttpStatus.NOT_FOUND
            ));
 
        // Verificar que la orden sigue disponible para tomar
        if (!"pendiente".equals(orden.getEstadoOrden())) {
            throw new BusinessException(
                "Esta solicitud ya fue tomada por otro cuidador",
                HttpStatus.CONFLICT
            );
        }
 
        // Verificar que el cuidador está verificado y disponible
        Cuidador cuidador = cuidadorRepository.findById(idCuidador)
            .orElseThrow(() -> new BusinessException(
                "Cuidador no encontrado", HttpStatus.NOT_FOUND
            ));
 
        if (!"verificado".equals(cuidador.getEstadoVerificacion())) {
            throw new BusinessException(
                "Tu cuenta aún no ha sido verificada por el administrador",
                HttpStatus.FORBIDDEN
            );
        }
 
        if (!"disponible".equals(cuidador.getEstadoDisponibilidad())) {
            throw new BusinessException(
                "No puedes aceptar solicitudes mientras tienes una orden en proceso",
                HttpStatus.CONFLICT
            );
        }
 
        // Asignar el cuidador y cambiar estados
        orden.setCuidador(cuidador);
        orden.setEstadoOrden("en_proceso");
        detalleOrdenRepository.save(orden);
 
        // Marcar al cuidador como ocupado
        cuidador.setEstadoDisponibilidad("ocupado");
        cuidadorRepository.save(cuidador);
 
        // Actualizar el estado de la solicitud
        SolicitudServicio solicitud = orden.getSolicitudServicio();
        solicitud.setEstadoSolicitud("en_proceso");
        solicitudRepository.save(solicitud);
 
        return orden;
    }
 
    // ── CLIENTE: historial de solicitudes ───────────────────────────────────────
 
    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerHistorialCliente(Integer idCliente) {
        List<SolicitudServicio> solicitudes = solicitudRepository
            .findByIdClienteOrderByFechaSolicitudDesc(idCliente);
 
        return solicitudes.stream().map(solicitud -> {
            Optional<DetalleOrden> ordenOpt = detalleOrdenRepository
                .findBySolicitudServicioIdSolicitud(solicitud.getIdSolicitud());
 
            if (ordenOpt.isEmpty()) {
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
 
            String nombreCuidador = orden.getCuidador() != null
                ? orden.getCuidador().getNombre() + " " + orden.getCuidador().getApPaterno()
                : "Sin asignar";
 
            boolean tieneEvidencia = evidenciaRepository
                .existsByDetalleOrdenIdOrden(orden.getIdOrden());
 
            boolean tieneCalificacion = calificacionRepository
                .findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent();
 
            return new OrdenResponseDTO(
                orden.getIdOrden(),
                solicitud.getIdSolicitud(),
                solicitud.getTipoSolicitud().getNombreServicio(),
                nombreCuidador,
                orden.getEspacio().getSectorPabellon()
                    + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(),
                orden.getFechaProgramada(),
                orden.getEstadoOrden(),
                orden.getMontoTotal(),
                tieneEvidencia,
                tieneCalificacion
            );
        }).collect(Collectors.toList());
    }
 
    // ── CUIDADOR: solicitudes disponibles por cementerio ───────────────────────
 
    /**
     * Lo llama el CUIDADOR para ver qué solicitudes puede tomar.
     * Filtra por cementerio y estado "pendiente" (sin cuidador asignado).
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerSolicitudesDisponibles(Integer idCementerio) {
        List<DetalleOrden> ordenesPendientes = detalleOrdenRepository
            .findByEstadoOrdenAndEspacioCementerioIdCementerio("pendiente", idCementerio);
 
        return ordenesPendientes.stream().map(orden -> {
            SolicitudServicio solicitud = orden.getSolicitudServicio();
            return new OrdenResponseDTO(
                orden.getIdOrden(),
                solicitud.getIdSolicitud(),
                solicitud.getTipoSolicitud().getNombreServicio(),
                "Sin asignar",
                orden.getEspacio().getSectorPabellon()
                    + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(),
                orden.getFechaProgramada(),
                orden.getEstadoOrden(),
                orden.getMontoTotal(),
                false,
                false
            );
        }).collect(Collectors.toList());
    }
 
    // ── CUIDADOR: subir evidencia ───────────────────────────────────────────────
 
    @Override
    @Transactional
    public RegistroEvidencia subirEvidencia(EvidenciaRequestDTO request) {
        DetalleOrden orden = detalleOrdenRepository.findById(request.getIdOrden())
            .orElseThrow(() -> new BusinessException(
                "Orden no encontrada", HttpStatus.NOT_FOUND
            ));
 
        RegistroEvidencia evidencia = new RegistroEvidencia();
        evidencia.setDetalleOrden(orden);
        evidencia.setUrlFoto(request.getUrlFoto());
        evidencia.setTipoMomento(request.getTipoMomento());
        evidencia.setDescripcionEstado(request.getDescripcionEstado());
 
        return evidenciaRepository.save(evidencia);
    }

    /**
     * Lo llama el CUIDADOR para ver todas sus órdenes.
     * Incluye órdenes en_proceso y completadas.
     * El cuidador ve: tipo de servicio, ubicación del espacio,
     * fecha programada, estado y si tiene calificación recibida.
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrdenResponseDTO> obtenerHistorialCuidador(Integer idCuidador) {
        List<DetalleOrden> ordenes = detalleOrdenRepository
            .findByCuidadorIdPersonaOrderByFechaCreacionDesc(idCuidador);
    
        return ordenes.stream().map(orden -> {
            SolicitudServicio solicitud = orden.getSolicitudServicio();
    
            boolean tieneEvidencia = evidenciaRepository
                .existsByDetalleOrdenIdOrden(orden.getIdOrden());
    
            boolean tieneCalificacion = calificacionRepository
                .findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent();
    
            return new OrdenResponseDTO(
                orden.getIdOrden(),
                solicitud.getIdSolicitud(),
                solicitud.getTipoSolicitud().getNombreServicio(),
                // El cuidador ve el nombre del cliente en vez del cuidador
                "Cliente #" + solicitud.getIdCliente(),
                orden.getEspacio().getSectorPabellon()
                    + " - N°" + orden.getEspacio().getNumeroSepultura(),
                orden.getFechaCreacion(),
                orden.getFechaProgramada(),
                orden.getEstadoOrden(),
                orden.getMontoTotal(),
                tieneEvidencia,
                tieneCalificacion
            );
        }).collect(Collectors.toList());
    }
 
    // ── CLIENTE: calificar el servicio ──────────────────────────────────────────
 
    @Override
    @Transactional
    public Calificacion calificarServicio(CalificacionRequestDTO request) {
        DetalleOrden orden = detalleOrdenRepository.findById(request.getIdOrden())
            .orElseThrow(() -> new BusinessException(
                "Orden no encontrada", HttpStatus.NOT_FOUND
            ));
 
        if (calificacionRepository.findByDetalleOrdenIdOrden(orden.getIdOrden()).isPresent()) {
            throw new BusinessException(
                "Este servicio ya fue calificado", HttpStatus.CONFLICT
            );
        }
 
        Calificacion calificacion = new Calificacion();
        calificacion.setDetalleOrden(orden);
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());
 
        // Cerrar el ciclo: la orden queda completada
        orden.setEstadoOrden("completada");
        detalleOrdenRepository.save(orden);
 
        // Liberar al cuidador para que pueda tomar nuevas solicitudes
        Cuidador cuidador = orden.getCuidador();
        if (cuidador != null) {
            cuidador.setEstadoDisponibilidad("disponible");
            cuidadorRepository.save(cuidador);
        }
 
        return calificacionRepository.save(calificacion);
    }
}