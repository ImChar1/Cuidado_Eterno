package com.cuidadoeterno.backend.modules.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class ProductoCatalogoResponseDTO {
    private Integer idProducto;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String urlImagen;
    
    // Datos específicos de la tienda (PuestoVenta)
    private BigDecimal precioVenta;
    private Boolean hayStock;
}