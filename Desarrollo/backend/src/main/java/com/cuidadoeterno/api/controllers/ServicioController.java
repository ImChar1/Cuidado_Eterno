package com.cuidadoeterno.api.controllers;

import com.cuidadoeterno.models.Servicio;
import com.cuidadoeterno.services.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*") // Permite que React se conecte sin errores de bloqueo
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    // ENDPOINT: GET http://localhost:8080/api/servicios
    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioService.obtenerTodos();
    }

    // ENDPOINT: POST http://localhost:8080/api/servicios
    @PostMapping
    public Servicio crearServicio(@RequestBody Servicio servicio) {
        return servicioService.guardarServicio(servicio);
    }
}