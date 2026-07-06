package com.cuidadoeterno.backend.modules.inventario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "catalogo_producto", uniqueConstraints = {
    // Garantiza que un puesto no tenga duplicado el mismo producto
    @UniqueConstraint(columnNames = {"id_puesto", "id_producto"})
})
@Getter
@Setter
@NoArgsConstructor
public class CatalogoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo", nullable = false, updatable = false)
    private Integer idCatalogo;

    @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "hay_stock", nullable = false)
    private Boolean hayStock = true;

    // Relación: Este registro pertenece a un Puesto de Venta específico
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_puesto", nullable = false)
    private PuestoVenta puestoVenta;

    // Relación: Este registro hace referencia a un Producto global
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;
}