package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    // ── Datos del espacio (se crea o reutiliza)
    private Integer idCementerio;
    private Integer idTipoEspacio;
    private String sectorPabellon;
    private String numeroSepultura;
    private String pisoNivel;
    private String pasillo;
    private String materialPrincipal;  // el cliente lo ingresa

    // -- datos del difunto
    private String nombres;
    private String apellidos;
    private LocalDate fechaNacimiento;
    private LocalDate fechaDefuncion; 
    private String epitafio;     
    
    // Lista de productos comprados (puede venir vacía si solo pidió limpieza sin flores)
    private List<InsumoRequestDTO> insumos;
}