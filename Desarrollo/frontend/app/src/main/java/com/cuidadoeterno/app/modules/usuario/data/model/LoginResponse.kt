package com.cuidadoeterno.app.modules.usuario.data.model

data class LoginResponse(
    val token: String,
    val tipo: String,
    val rol: String,
    val idPersona: Int,
    val nombre: String,
    val apPaterno: String,
    val email: String
)