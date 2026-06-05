package com.cuidadoeterno.backend.config;

import com.cuidadoeterno.backend.shared.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de Spring Security.
 *
 * Decisiones de diseño:
 * - PBKDF2 con HMAC-SHA256 para hashing de contraseñas (requerimiento del proyecto)
 * - JWT stateless: no se usa sesión HTTP
 * - CORS configurado para Android (en dev permite cualquier origen)
 * - @EnableMethodSecurity activa @PreAuthorize en controllers y services
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    // ── Rutas públicas ──────────────────────────────────────────────────────────

    /**
     * Rutas que no requieren token JWT.
     * Todo lo demás exige autenticación.
     */
    private static final String[] RUTAS_PUBLICAS = {
        "/auth/**",           // login y registro
        "/swagger-ui/**",     // documentación (deshabilitar en prod con variable de entorno)
        "/api-docs/**",
        "/api-docs/**",
        "/swagger-ui.html",         
        "/swagger-ui/index.html",   
        "/api-docs/swagger-config",
        "/v3/api-docs/**" 
    };

    // ── Cadena de filtros ───────────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF deshabilitado porque usamos JWT stateless (no cookies de sesión)
            .csrf(AbstractHttpConfigurer::disable)

            // CORS configurado para Android
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Control de rutas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(RUTAS_PUBLICAS).permitAll()
                .anyRequest().authenticated()
            )

            // Sin sesión HTTP: cada request trae su propio token
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Proveedor de autenticación con PBKDF2
            .authenticationProvider(authenticationProvider())

            // El filtro JWT se ejecuta antes del filtro de usuario/contraseña
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── Encoder PBKDF2 con HMAC-SHA256 ─────────────────────────────────────────

    /**
     * PBKDF2 con HMAC-SHA256.
     *
     * Parámetros elegidos siguiendo la recomendación NIST SP 800-132:
     * - secret: cadena vacía (el salt aleatorio ya añade suficiente entropía)
     * - saltLength: 16 bytes (128 bits)
     * - iterations: 310.000 (mínimo recomendado NIST 2023 para PBKDF2-HMAC-SHA256)
     * - algorithm: PBKDF2WithHmacSHA256
     *
     * Spring almacena el hash como: {pbkdf2}iterations:salt:hash (todo en Base64)
     * lo que hace el campo clave_hash autosuficiente para la verificación.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(
            "",
            16,
            310_000,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256
        );
    }

    // ── AuthenticationProvider ──────────────────────────────────────────────────

    /**
     * Conecta UserDetailsService + PasswordEncoder.
     * Spring Security usa esto internamente cuando llamas a authenticationManager.authenticate().
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager expuesto como Bean para que AuthServiceImpl lo inyecte
     * y pueda llamar a authenticate() en el login.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ── CORS para Android ───────────────────────────────────────────────────────

    /**
     * En desarrollo permite cualquier origen porque el emulador Android
     * y dispositivos físicos tienen IPs distintas.
     *
     * En producción reemplazar allowedOrigins con las IPs/dominios reales
     * usando una variable de entorno: ${ALLOWED_ORIGINS}
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of("*")); // dev: cualquier origen
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization")); // Android puede leer este header
        config.setAllowCredentials(false); // false porque usamos JWT, no cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}