package com.cuidadoeterno.backend.modules.finanzas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebpayInitRequestDTO {
    // El ID de la Solicitud de Servicio (SOLICITUD_SERVICIO) que el cliente va a pagar
    private Integer idSolicitud; 
    
    // Hacia dónde debe redirigir Transbank al cliente una vez que ponga su tarjeta
    private String returnUrl; 
}