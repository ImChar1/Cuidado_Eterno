package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrdenRequestDTO {
    private Integer idCliente;
    private Integer idTipoSolicitud; // Ej: 1 para Jardinería, 2 para Limpieza
    private Integer idEspacio; // La tumba exacta
    private LocalDateTime fechaProgramada;
    private BigDecimal montoTotalServicio; 
    private String observaciones;
    
    // Lista de productos comprados (puede venir vacía si solo pidió limpieza sin flores)
    private List<InsumoRequestDTO> insumos;
}