package com.cuidadoeterno.backend.modules.usuario.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Tabla: CLIENTE
 * Subtipo de PERSONA. Usa herencia JOINED:
 * JPA hace JOIN entre PERSONA y CLIENTE usando id_persona como FK/PK.
 *
 * El id_persona actúa como PK de CLIENTE Y como FK hacia PERSONA,
 * lo que refleja exactamente el DDL generado.
 */
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_persona")
@Getter
@Setter
@NoArgsConstructor
public class Cliente extends Persona {

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    /**
     * Preferencia de notificación: 'email', 'push', 'sms'
     */
    @Column(name = "pref_notificacion", nullable = false, length = 20)
    private String prefNotificacion = "email";

    /**
     * Estado del cliente: 'activo', 'inactivo', 'suspendido'
     */
    @Column(name = "estado_cliente", nullable = false, length = 20)
    private String estadoCliente = "activo";
}