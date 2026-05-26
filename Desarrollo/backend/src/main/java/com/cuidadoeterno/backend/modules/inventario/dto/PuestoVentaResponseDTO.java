package com.cuidadoeterno.backend.modules.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PuestoVentaResponseDTO {
    private Integer idPuesto;
    private String nombreLocal;
    private String ubicacionRef;
    private String telefono;
}