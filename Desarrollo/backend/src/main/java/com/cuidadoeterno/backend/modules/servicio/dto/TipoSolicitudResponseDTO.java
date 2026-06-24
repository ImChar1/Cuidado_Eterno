package com.cuidadoeterno.backend.modules.servicio.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoSolicitudResponseDTO {
    
    private Long idTipoSolicitud;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal precioBase;
    private Integer duracionEstimadaMin;
    private Boolean requiereInsumos;

}
