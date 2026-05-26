package com.cuidadoeterno.backend.modules.servicio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "SOLICITUD_SERVICIO")
@Getter
@Setter
@NoArgsConstructor
public class SolicitudServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud", nullable = false, updatable = false)
    private Integer idSolicitud;

    @Column(name = "fecha_solicitud", nullable = false, updatable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    // Relación: De qué tipo es esta solicitud (Jardinería, etc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_solicitud", nullable = false)
    private TipoSolicitud tipoSolicitud;

    // Aquí iría la relación con Cliente (Persona/Usuario) cuando unamos los módulos
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente; 
}