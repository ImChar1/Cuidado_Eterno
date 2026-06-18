package com.cuidadoeterno.backend.modules.usuario.model;

import com.cuidadoeterno.backend.modules.cementerio.model.Horario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabla: CUIDADOR
 * Subtipo de PERSONA. Es el actor que ejecuta los servicios en el cementerio.
 *
 * Relaciones propias:
 * - ManyToOne → HORARIO (el horario en que está disponible para trabajar)
 */
@Entity
@Table(name = "CUIDADOR")
@PrimaryKeyJoinColumn(name = "id_persona")
@Getter
@Setter
@NoArgsConstructor
public class Cuidador extends Persona {

    
    /**
     * Promedio de calificaciones recibidas. Se actualiza al completar un servicio.
     * precision = 3, scale = 2 → valores posibles: 0.00 a 5.00
     */
    @Column(name = "calificacion_promedio",
            nullable = false,
            precision = 3,
            scale = 2)
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    /**
     * Estado de verificación de identidad: 'pendiente', 'verificado', 'rechazado'
     */
    @Column(name = "estado_verificacion", nullable = false, length = 20)
    private String estadoVerificacion = "pendiente";

    /**
     * Disponibilidad operativa: 'disponible', 'ocupado', 'inactivo'
     */
    @Column(name = "estado_disponibilidad", nullable = false, length = 20)
    private String estadoDisponibilidad = "disponible";

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    // Agregar estas columnas al modelo:
    @Column(name = "url_certificacion", length = 500)
    private String urlCertificacion;

    @Column(name = "tipo_documento", length = 50)
    private String tipoDocumento;

    @Column(name = "numero_registro", length = 50)
    private String numeroRegistro;

    @Column(name = "disponibilidad_dias", length = 100)
    private String disponibilidadDias;

    @Column(name = "disponibilidad_hora_inicio")
    private java.time.LocalTime disponibilidadHoraInicio;

    @Column(name = "disponibilidad_hora_fin")
    private java.time.LocalTime disponibilidadHoraFin;
}