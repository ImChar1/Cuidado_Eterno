package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.dto.FallecidoDTO;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.model.Fallecido;
import com.cuidadoeterno.backend.modules.cementerio.repository.EspacioRepository;
import com.cuidadoeterno.backend.modules.cementerio.repository.FallecidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FallecidoServiceImpl implements FallecidoService {

    private final FallecidoRepository fallecidoRepository;
    private final EspacioRepository espacioRepository;

    @Override
    @Transactional
    public Fallecido registrarFallecido(FallecidoDTO request) {
        // Validamos que la tumba donde lo van a registrar exista
        Espacio espacio = espacioRepository.findById(request.getIdEspacio())
                .orElseThrow(() -> new IllegalArgumentException("El espacio o tumba no existe."));

        Fallecido nuevoFallecido = new Fallecido();
        nuevoFallecido.setNombres(request.getNombres());
        nuevoFallecido.setApellidos(request.getApellidos());
        nuevoFallecido.setFechaDefuncion(request.getFechaDefuncion());
        nuevoFallecido.setFechaDeNacimiento(request.getFechaDeNacimiento());
        nuevoFallecido.setEspacio(espacio);

        return fallecidoRepository.save(nuevoFallecido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Fallecido> buscarFallecidosPorEspacio(Integer idEspacio) {
        return fallecidoRepository.findByEspacioIdEspacio(idEspacio);
    }
}