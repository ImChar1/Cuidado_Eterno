package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.dto.EspacioRequestDTO;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import java.util.List;

public interface EspacioService {
    List<Espacio> obtenerEspaciosPorCementerio(Integer idCementerio);
    Espacio registrarNuevoEspacio(EspacioRequestDTO request);
}