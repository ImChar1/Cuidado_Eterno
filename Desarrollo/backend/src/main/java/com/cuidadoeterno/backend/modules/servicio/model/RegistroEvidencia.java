package com.cuidadoeterno.backend.modules.servicio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_evidencia")
@Getter
@Setter
@NoArgsConstructor
public class RegistroEvidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia", nullable = false, updatable = false)
    private Integer idEvidencia;

    @Column(name = "url_foto", nullable = false, length = 500)
    private String urlFoto;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "descripcion_estado", length = 255)
    private String descripcionEstado;

    @Column(name = "tipo_momento", nullable = false, length = 20)
    private String tipoMomento; // Almacena valores como 'antes' o 'despues'

    @Column(name = "validado", nullable = false)
    private Boolean validado = false; // TINYINT(1) mapeado a Boolean

    // Relación: Muchas evidencias pertenecen a una sola Orden (CONSTRAINT EVIDENCIA_ORDEN_FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    private DetalleOrden detalleOrden;
}