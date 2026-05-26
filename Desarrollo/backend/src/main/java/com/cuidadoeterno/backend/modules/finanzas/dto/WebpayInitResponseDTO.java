package com.cuidadoeterno.backend.modules.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WebpayInitResponseDTO {
    private String token;
    private String url;
}