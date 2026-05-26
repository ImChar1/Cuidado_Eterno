package com.cuidadoeterno.backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvidenciaRequestDTO {
    private Integer idOrden;
    private String urlFoto; // La URL de la foto subida (ej. a Firebase o AWS S3)
    private String tipoMomento; // "antes", "durante", "despues"
    private String descripcionEstado;
}