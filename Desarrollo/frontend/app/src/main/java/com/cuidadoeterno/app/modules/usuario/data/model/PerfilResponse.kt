package com.cuidadoeterno.app.modules.usuario.data.model

data class PerfilResponse(
    val idPersona: Int,
    val nombre: String,
    val apPaterno: String,
    val apMaterno: String?,
    val email: String,
    val telefono: String,
    val rol: String,
    val nombreUsuario: String,
    val estadoCuenta: String,
    // Solo CLIENTE
    val estadoCliente: String?,
    val prefNotificacion: String?,
    // Solo CUIDADOR
    val calificacionPromedio: Double?,
    val estadoVerificacion: String?,
    val estadoDisponibilidad: String?,
    // Solo ADMINISTRADOR
    val nivelAcceso: String?,
    val cargo: String?
)