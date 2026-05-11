package com.cuidadoeterno.api.services;

import com.cuidadoeterno.models.Servicio;
import com.cuidadoeterno.repositories.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    // Obtener todos los servicios para mostrarlos en la App
    public List<Servicio> obtenerTodos() {
        return servicioRepository.findAll();
    }

    // Crear un nuevo servicio (Para el perfil de Administrador)
    public Servicio guardarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
}