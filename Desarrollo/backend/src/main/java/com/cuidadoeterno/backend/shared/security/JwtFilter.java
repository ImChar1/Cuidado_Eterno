package com.cuidadoeterno.backend.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta UNA sola vez por request (OncePerRequestFilter).
 *
 * Flujo:
 * 1. Lee el header "Authorization: Bearer <token>"
 * 2. Extrae el nombre de usuario del token
 * 3. Carga el UserDetails desde la base de datos
 * 4. Si el token es válido, autentica al usuario en el SecurityContext
 * 5. La cadena de filtros continúa normalmente
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", dejamos pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token quitando "Bearer " (7 caracteres)
        final String token = authHeader.substring(7);
        final String nombreUsuario;

        try {
            nombreUsuario = jwtUtil.extraerNombreUsuario(token);
            // Solo procesamos si hay usuario en el token y aún no está autenticado en el contexto
            if (nombreUsuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(nombreUsuario);

                if (jwtUtil.esValido(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                        // credentials: null porque ya validamos con JWT
                            userDetails.getAuthorities()
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Registramos la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token malformado: dejamos pasar, Spring Security rechazará el acceso
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}