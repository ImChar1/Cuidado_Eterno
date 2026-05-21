package com.cuidadoeterno.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Mapea el bloque "jwt:" de application.yml a esta clase.
 * Usar @ConfigurationProperties es más seguro que @Value
 * porque falla rápido al arrancar si falta alguna propiedad.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /** Clave secreta para firmar el token. Mínimo 32 caracteres. */
    private String secret;

    /** Tiempo de vida del token en milisegundos. */
    private long expiration;

    // ── Getters y setters manuales (sin Lombok para que Spring los encuentre) ──

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
}