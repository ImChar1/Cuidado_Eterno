package com.cuidadoeterno.backend.modules.cementerio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EspacioResponseDTO {
    private Integer idEspacio;
    private String sectorPabellon;
    private String numeroSepultura;
    private String pisoNivel;
    private String pasillo;
    private String tipoEspacio;
    private String nombreCementerio;
}
