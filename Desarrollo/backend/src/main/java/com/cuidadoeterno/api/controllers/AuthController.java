package com.cuidadoeterno.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // Clase interna temporal para recibir los datos del JSON
    public static class LoginRequest {
        public String email;
        public String password;
    }

    // ENDPOINT: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Aquí luego validaremos contra la Base de Datos y encriptaremos con SHA-256 (RF-03)
        // Por ahora simulamos un login exitoso
        if ("usuario@correo.com".equals(request.email) && "123456".equals(request.password)) {
            response.put("mensaje", "Login exitoso");
            response.put("token", "simulacion_de_token_jwt_aqui");
            response.put("rol", "CLIENTE"); // Esto le dirá a React/Android a qué pantalla ir
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Credenciales inválidas");
            return ResponseEntity.status(401).body(response);
        }
    }
}