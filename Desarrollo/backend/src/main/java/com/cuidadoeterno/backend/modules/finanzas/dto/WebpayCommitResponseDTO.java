package com.cuidadoeterno.backend.modules.finanzas.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class WebpayCommitResponseDTO {
    private String status; // "AUTHORIZED" o "REJECTED"
    private String buyOrder;
    private String authorizationCode;
    private BigDecimal amount;
    private String cardNumber; // Solo los últimos 4 dígitos por seguridad
    private String transactionDate;
}