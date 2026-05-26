package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalificacionRequestDTO {
    private Integer idOrden;
    private Integer puntuacion; // 1 al 5
    private String comentario;
}