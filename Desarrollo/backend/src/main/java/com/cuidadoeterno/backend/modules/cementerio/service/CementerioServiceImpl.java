package com.cuidadoeterno.backend.modules.cementerio.service;

import com.cuidadoeterno.backend.modules.cementerio.model.*;
import com.cuidadoeterno.backend.modules.cementerio.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CementerioServiceImpl implements CementerioService {

    // Lombok (@RequiredArgsConstructor) inyecta automáticamente estos repositorios
    private final RegionRepository regionRepository;
    private final ProvinciaRepository provinciaRepository;
    private final ComunaRepository comunaRepository;
    private final TipoEspacioRepository tipoEspacioRepository;
    private final CementerioRepository cementerioRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Region> obtenerTodasLasRegiones() {
        return regionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Provincia> obtenerProvinciasPorRegion(Integer idRegion) {
        return provinciaRepository.findByRegionIdRegion(idRegion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comuna> obtenerComunasPorProvincia(Integer idProvincia) {
        return comunaRepository.findByProvinciaIdProvincia(idProvincia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TipoEspacio> obtenerTiposDeEspacio() {
        return tipoEspacioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cementerio> obtenerCementeriosPorComuna(Integer idComuna) {
        return cementerioRepository.findByComunaIdComuna(idComuna);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cementerio> buscarCementeriosPorNombre(String nombre) {
        return cementerioRepository.findByNombreCementerioContainingIgnoreCase(nombre);
    }
}