package com.cuidadoeterno.app.modules.usuario.data.model

import java.time.LocalDate

data class RegistroCuidadorRequest(
    val rut: String,
    val nombre: String,
    val apPaterno: String,
    val apMaterno: String?,        // nullable
    val email: String,
    val telefono: String,
    val fechaNacimiento: String,   // "yyyy-MM-dd"
    val genero: String,
    val nombreUsuario: String,
    val clave: String,
    // idHorario eliminado — el cuidador ya no se asigna a un horario fijo
    // Disponibilidad declarada por el cuidador
    val disponibilidadDias: String,          // "lunes,martes,miercoles"
    val disponibilidadHoraInicio: String,    // "08:00"
    val disponibilidadHoraFin: String,       // "18:00"
    // Documentación
    val urlCertificacion: String,
    val tipoDocumento: String,
    val numeroRegistro: String?              // nullable — es opcional
)