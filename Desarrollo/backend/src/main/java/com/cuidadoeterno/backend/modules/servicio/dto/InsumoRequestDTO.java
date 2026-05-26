package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InsumoRequestDTO {
    private Integer idPuesto; // De qué florería lo sacará
    private BigDecimal montoTotal; // Cuánto costó este insumo en específico
}