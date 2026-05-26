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
    private String tipoServicio; // "Jardinería"
    private LocalDateTime fechaProgramada;
    private String estadoOrden; // "pendiente", "completada"
    private BigDecimal montoTotal;
}