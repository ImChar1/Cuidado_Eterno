package com.cuidadoeterno.app.modules.usuario.data.model

data class LoginRequest(
    val nombreUsuario: String,
    val clave: String
)