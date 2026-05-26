package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import java.math.BigDecimal;
import java.util.List;

public interface EspacioService {
    List<Espacio> obtenerEspaciosPorCementerio(Integer idCementerio);
    Espacio registrarNuevoEspacio(Integer idCementerio, Integer idTipoEspacio, String sector, String numero, BigDecimal latitud, BigDecimal longitud);
}