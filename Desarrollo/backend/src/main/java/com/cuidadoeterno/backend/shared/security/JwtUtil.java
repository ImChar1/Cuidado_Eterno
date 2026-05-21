package com.cuidadoeterno.backend.shared.security;

import com.cuidadoeterno.backend.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generar, firmar y validar tokens JWT.
 *
 * Algoritmo: HMAC-SHA256 (HS256)
 * El payload incluye: subject (nombre_usuario), rol, y fechas de emisión/expiración.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtUtil(JwtConfig jwtConfig) {
        // Keys.hmacShaKeyFor valida que el secret tenga al menos 32 bytes para HS256
        this.secretKey = Keys.hmacShaKeyFor(
            jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8)
        );
        this.expiration = jwtConfig.getExpiration();
    }

    /**
     * Genera un token JWT firmado con HMAC-SHA256.
     *
     * @param nombreUsuario username que irá como subject del token
     * @param rol           nombre del rol (ej: "CLIENTE", "CUIDADOR", "ADMINISTRADOR")
     * @return token JWT como String
     */
    public String generarToken(String nombreUsuario, String rol) {
        Date ahora = new Date();
        Date expira = new Date(ahora.getTime() + expiration);

        return Jwts.builder()
            .subject(nombreUsuario)
            .claim("rol", rol)
            .issuedAt(ahora)
            .expiration(expira)
            .signWith(secretKey)          // HS256 por defecto con SecretKey
            .compact();
    }

    /**
     * Extrae el subject (nombre_usuario) del token.
     *
     * @param token JWT como String
     * @return nombre_usuario guardado en el subject
     */
    public String extraerNombreUsuario(String token) {
        return extraerClaims(token).getSubject();
    }

    /**
     * Extrae el rol del token.
     *
     * @param token JWT como String
     * @return nombre del rol
     */
    public String extraerRol(String token) {
        return extraerClaims(token).get("rol", String.class);
    }

    /**
     * Valida que el token sea correcto y no esté expirado.
     *
     * @param token         JWT como String
     * @param nombreUsuario username esperado
     * @return true si el token es válido para ese usuario
     */
    public boolean esValido(String token, String nombreUsuario) {
        try {
            String subject = extraerNombreUsuario(token);
            return subject.equals(nombreUsuario) && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Métodos privados ────────────────────────────────────────────────────────

    private boolean estaExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    /**
     * Parsea y verifica la firma del token, devolviendo los Claims.
     * Lanza JwtException si la firma no coincide o el token está malformado.
     */
    private Claims extraerClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}