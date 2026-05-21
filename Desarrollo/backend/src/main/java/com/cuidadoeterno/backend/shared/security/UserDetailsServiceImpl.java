package com.cuidadoeterno.backend.shared.security;

import com.cuidadoeterno.backend.modules.usuario.repository.CredencialRepository;
import com.cuidadoeterno.backend.modules.usuario.model.Credencial;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación de UserDetailsService que Spring Security usa para
 * cargar el usuario desde la base de datos durante la autenticación.
 *
 * El "username" en este sistema es el campo nombre_usuario de CREDENCIAL.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CredencialRepository credencialRepository;

    public UserDetailsServiceImpl(CredencialRepository credencialRepository) {
        this.credencialRepository = credencialRepository;
    }

    /**
     * Carga las credenciales del usuario desde la BD.
     * Spring Security llama a este método automáticamente durante el login.
     *
     * @param nombreUsuario valor del campo nombre_usuario en la tabla CREDENCIAL
     * @return UserDetails con username, hash de contraseña y rol como authority
     * @throws UsernameNotFoundException si el usuario no existe en la BD
     */
    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Credencial credencial = credencialRepository
            .findByNombreUsuario(nombreUsuario)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado: " + nombreUsuario
            ));

        // El rol se guarda con prefijo "ROLE_" por convención de Spring Security
        // Ejemplo: si nombre_rol = "CLIENTE", la authority será "ROLE_CLIENTE"
        String rol = "ROLE_" + credencial.getRol().getNombreRol().toUpperCase();

        return new User(
            credencial.getNombreUsuario(),
            credencial.getClaveHash(),
            List.of(new SimpleGrantedAuthority(rol))
        );
    }
}