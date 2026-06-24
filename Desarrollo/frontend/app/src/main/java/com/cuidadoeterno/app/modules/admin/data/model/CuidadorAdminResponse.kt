package com.cuidadoeterno.app.modules.admin.data.model

data class CuidadorAdminResponse(
    val idCuidador: Int,
    val nombre: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val estadoValidacion: String, // "PENDIENTE", "VALIDADO", "RECHAZADO"
    val urlAntecedentes: String?,
    val urlCertificadoCurso: String?,
    val fechaRegistro: String?
)