package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.model.*;
import java.util.List;

public interface CementerioService {
    List<Region> obtenerTodasLasRegiones();
    List<Provincia> obtenerProvinciasPorRegion(Integer idRegion);
    List<Comuna> obtenerComunasPorProvincia(Integer idProvincia);
    List<TipoEspacio> obtenerTiposDeEspacio();
    List<Cementerio> obtenerCementeriosPorComuna(Integer idComuna);
    List<Cementerio> buscarCementeriosPorNombre(String nombre);
}