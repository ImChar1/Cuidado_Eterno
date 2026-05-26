package com.cuidadoeterno.backend.modules.cementerio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CementerioResponseDTO {
    private Integer idCementerio;
    private String nombreCementerio;
    private String direccion;
    private String telefonoContacto;
    private String nombreComuna; // Aliviamos la carga de red mandando solo el texto
}