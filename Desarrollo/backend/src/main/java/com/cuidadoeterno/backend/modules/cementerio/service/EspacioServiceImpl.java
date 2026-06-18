package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.dto.EspacioRequestDTO;
import com.cuidadoeterno.backend.modules.cementerio.model.Cementerio;
import com.cuidadoeterno.backend.modules.cementerio.model.Espacio;
import com.cuidadoeterno.backend.modules.cementerio.model.TipoEspacio;
import com.cuidadoeterno.backend.modules.cementerio.repository.CementerioRepository;
import com.cuidadoeterno.backend.modules.cementerio.repository.EspacioRepository;
import com.cuidadoeterno.backend.modules.cementerio.repository.TipoEspacioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Espacio registrarNuevoEspacio(EspacioRequestDTO request) {

        Optional<Espacio> espacioExistente = espacioRepository
            .findByCementerioIdCementerioAndSectorPabellonAndNumeroSepultura(
                request.getIdCementerio(),
                request.getSectorPabellon(),
                request.getNumeroSepultura()
            );

        if (espacioExistente.isPresent()) {
            return espacioExistente.get();
        }

        Cementerio cementerio = cementerioRepository.findById(request.getIdCementerio())
            .orElseThrow(() -> new IllegalArgumentException("El cementerio no existe"));

        TipoEspacio tipoEspacio = tipoEspacioRepository.findById(request.getIdTipoEspacio())
            .orElseThrow(() -> new IllegalArgumentException("El tipo de espacio no existe"));

        Espacio nuevoEspacio = new Espacio();
        nuevoEspacio.setCementerio(cementerio);
        nuevoEspacio.setTipoEspacio(tipoEspacio);
        nuevoEspacio.setSectorPabellon(request.getSectorPabellon());
        nuevoEspacio.setNumeroSepultura(request.getNumeroSepultura());
        nuevoEspacio.setCoordenadaLatitud(request.getCoordenadaLatitud());
        nuevoEspacio.setCoordenadaLongitud(request.getCoordenadaLongitud());
        nuevoEspacio.setMaterialPrincipal(request.getMaterialPrincipal());
        nuevoEspacio.setEstadoFisico(request.getEstadoFisico());

        return espacioRepository.save(nuevoEspacio);
}
}