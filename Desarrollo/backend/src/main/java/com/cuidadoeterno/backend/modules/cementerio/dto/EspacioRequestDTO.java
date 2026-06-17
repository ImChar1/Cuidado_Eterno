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
    private BigDecimal coordenadaLatitud;
    private BigDecimal coordenadaLongitud;
    private String materialPrincipal;
    private String estadoFisico;
}