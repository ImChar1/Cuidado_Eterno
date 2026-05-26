package com.cuidadoeterno.backend.config;

import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransbankConfig {

    /**
     * Bean de configuración de Transbank.
     * Al ser un @Bean, Spring Boot lo mantiene en memoria y nos permite
     * inyectarlo fácilmente en nuestro servicio (WebpayServiceImpl) más adelante.
     */
    @Bean
    public WebpayOptions webpayOptions() {
        // Configuramos explícitamente el ambiente de INTEGRACIÓN (Pruebas)
        // usando el Código de Comercio y la API Key públicas oficiales de Transbank.
        return new WebpayOptions(
                IntegrationCommerceCodes.WEBPAY_PLUS,
                IntegrationApiKeys.WEBPAY,
                IntegrationType.TEST
        );
    }
}