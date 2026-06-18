package com.cuidadoeterno.app.modules.usuario.data.model

import java.time.LocalDate

data class RegistroCuidadorRequest (
    val rut: String,
    val nombre: String,
    val apPaterno: String,
    val apMaterno: String,
    val email: String,
    val telefono: String,
    val fechaNacimiento: LocalDate,
    val genero: String,
    val nombreUsuario: String,
    val clave: String
)