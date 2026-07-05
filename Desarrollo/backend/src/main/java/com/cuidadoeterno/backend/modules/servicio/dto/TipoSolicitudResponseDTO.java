package com.cuidadoeterno.backend.modules.servicio.dto;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TipoSolicitudResponseDTO {
    
    private Integer idTipoSolicitud;
    private String nombreServicio;
    private String descripcion;
    private BigDecimal precioBase;
    private Short duracionEstimadaMin;
    private Boolean requiereInsumos;

}
