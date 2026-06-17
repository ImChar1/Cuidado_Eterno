package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class OrdenResponseDTO {
    private Integer idOrden;
    private Integer idSolicitud;          // ← para rastrear la solicitud origen
    private String nombreServicio;         // tipo de servicio: "Limpieza básica"
    private String nombreCuidador;         // nombre del cuidador asignado
    private String ubicacionEspacio;       // ubicacion_interna del espacio
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaProgramada;
    private String estadoOrden;
    private BigDecimal montoTotal;
    private Boolean tieneEvidencia;        // ← para saber si ya subieron fotos
    private Boolean tieneCalificacion;     // ← para saber si ya calificaron
}