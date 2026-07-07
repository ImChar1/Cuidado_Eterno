package com.cuidadoeterno.app.modules.admin.data.model

data class CuidadorAdminResponse(
    val idPersona: Int,           // Antes decía idCuidador
    val rut: String,
    val nombre: String,
    val apPaterno: String?,       // Agregado del backend
    val email: String,            // Antes decía correo
    val telefono: String,
    val estadoVerificacion: String, // Antes decía estadoValidacion
    val fechaRegistro: String?      // El JSON mandará un String "YYYY-MM-DD"
)