package com.cuidadoeterno.backend.modules.inventario.model;

import com.cuidadoeterno.backend.modules.cementerio.model.Cementerio; // Importación cruzada de módulos
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "puesto_venta")
@Getter
@Setter
@NoArgsConstructor
public class PuestoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puesto", nullable = false, updatable = false)
    private Integer idPuesto;

    @Column(name = "nombre_local", nullable = false, length = 100)
    private String nombreLocal;

    @Column(name = "ubicacion_ref", nullable = false, length = 255)
    private String ubicacionRef;

    @Column(name = "telefono", nullable = false, length = 15)
    private String telefono;

    @Column(name = "estado_puesto", nullable = false)
    private Boolean estadoPuesto = true;

    // Relación: Muchos Puestos de Venta pueden estar en un Cementerio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cementerio", nullable = false)
    private Cementerio cementerio;
}