package com.cuidadoeterno.app.modules.servicio.data.model

/**
 * Mapea CalificacionRequestDTO del backend.
 * El cliente califica el servicio una vez completado.
 *
 * puntuacion: 1 a 5
 */
data class CalificacionRequest(
    val idOrden: Int,
    val puntuacion: Int,
    val comentario: String?
)
