package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.model.Cementerio;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.model.TipoEspacio;
import com.cuidadoeterno.backend.modules.cementerio.repository.CementerioRepository;
import com.cuidadoeterno.backend.modules.cementerio.repository.EspacioRepository;
import com.cuidadoeterno.backend.modules.cementerio.repository.TipoEspacioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EspacioServiceImpl implements EspacioService {

    private final EspacioRepository espacioRepository;
    private final CementerioRepository cementerioRepository;
    private final TipoEspacioRepository tipoEspacioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Espacio> obtenerEspaciosPorCementerio(Integer idCementerio) {
        return espacioRepository.findByCementerioIdCementerio(idCementerio);
    }

    @Override
    @Transactional
    public Espacio registrarNuevoEspacio(Integer idCementerio, Integer idTipoEspacio, 
                                         String sector, String numero, 
                                         BigDecimal latitud, BigDecimal longitud) {
                                             
        // 1. Validar que la tumba no exista ya (Evita duplicidad)
        Optional<Espacio> espacioExistente = espacioRepository
                .findByCementerioIdCementerioAndSectorPabellonAndNumeroSepultura(idCementerio, sector, numero);
                
        if (espacioExistente.isPresent()) {
            // Si ya existe, retornamos el existente para que el cliente lo use
            return espacioExistente.get();
        }

        // 2. Buscar las entidades padre
        Cementerio cementerio = cementerioRepository.findById(idCementerio)
                .orElseThrow(() -> new IllegalArgumentException("El cementerio no existe"));
                
        TipoEspacio tipoEspacio = tipoEspacioRepository.findById(idTipoEspacio)
                .orElseThrow(() -> new IllegalArgumentException("El tipo de espacio no existe"));

        // 3. Crear y guardar el nuevo espacio
        Espacio nuevoEspacio = new Espacio();
        nuevoEspacio.setCementerio(cementerio);
        nuevoEspacio.setTipoEspacio(tipoEspacio);
        nuevoEspacio.setSectorPabellon(sector);
        nuevoEspacio.setNumeroSepultura(numero);
        nuevoEspacio.setCoordenadaLatitud(latitud);
        nuevoEspacio.setCoordenadaLongitud(longitud);

        return espacioRepository.save(nuevoEspacio);
    }
}