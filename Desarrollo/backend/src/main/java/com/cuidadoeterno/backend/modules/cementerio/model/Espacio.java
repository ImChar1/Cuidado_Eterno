package com.cuidadoeterno.backend.modules.cementerio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "espacio")
@Getter
@Setter
@NoArgsConstructor
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_espacio", nullable = false, updatable = false)
    private Integer idEspacio;

    @Column(name = "sector_pabellon", length = 50, nullable = false)
    private String sectorPabellon;

    @Column(name = "numero_sepultura", length = 20, nullable = false)
    private String numeroSepultura;

    // Precisión geográfica para el GPS
    @Column(name = "coordenada_latitud", precision = 10, scale = 8)
    private BigDecimal coordenadaLatitud;

    @Column(name = "coordenada_longitud", precision = 11, scale = 8)
    private BigDecimal coordenadaLongitud;

    @Column(name = "material_principal", length = 50)
    private String materialPrincipal;

    @Column(name = "estado_fisico", length = 20)
    private String estadoFisico;

    @Column(name = "url_foto_referencia", length = 500)
    private String urlFotoReferencia;

    // Relación: Muchos Espacios pertenecen a un Cementerio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cementerio", nullable = false)
    private Cementerio cementerio;

    // Relación: Muchos Espacios tienen un Tipo de Espacio (ej. Mausoleo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_espacio", nullable = false)
    private TipoEspacio tipoEspacio;

    @Column(name = "piso_nivel", length = 20)
    private String pisoNivel;

    @Column(name = "pasillo", length = 50)
    private String pasillo;
}