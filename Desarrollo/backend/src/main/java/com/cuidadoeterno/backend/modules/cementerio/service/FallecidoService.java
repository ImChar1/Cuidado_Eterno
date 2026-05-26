package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.dto.FallecidoDTO;
import com.cuidadoeterno.backend.modules.cementerio.model.Fallecido;

import java.util.List;

public interface FallecidoService {
    Fallecido registrarFallecido(FallecidoDTO request);
    List<Fallecido> buscarFallecidosPorEspacio(Integer idEspacio);
}