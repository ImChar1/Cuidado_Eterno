package com.cuidadoeterno.backend.modules.cementerio.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class EspacioRequestDTO {
    private Integer idCementerio;
    private Integer idTipoEspacio;
    private String sectorPabellon;
    private String numeroSepultura;
    // Opcionales — los ingresa el admin con GPS, el cliente los omite
    private BigDecimal coordenadaLatitud;
    private BigDecimal coordenadaLongitud;
    private String materialPrincipal;
    private String estadoFisico;
    // Nuevos campos del wireframe
    private String pisoNivel;
    private String pasillo;
}
