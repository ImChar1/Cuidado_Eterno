package com.cuidadoeterno.app.modules.servicio.data.model

data class PuestoVentaResponse(
    val idPuesto: Int,
    val nombreLocal: String,
    val ubicacionRef: String?,
    val telefono: String?
)