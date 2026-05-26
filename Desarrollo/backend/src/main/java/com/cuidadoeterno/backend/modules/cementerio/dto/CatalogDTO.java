package com.cuidadoeterno.backend.modules.cementerio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO genérico para alimentar Spinners/Selects en la App Móvil.
 * Sirve para Región, Provincia, Comuna y Tipo de Espacio.
 */
@Getter
@Setter
@AllArgsConstructor
public class CatalogDTO {
    private Integer id;
    private String nombre;
}