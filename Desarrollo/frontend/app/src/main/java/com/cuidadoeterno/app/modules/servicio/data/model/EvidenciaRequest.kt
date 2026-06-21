package com.cuidadoeterno.app.modules.servicio.data.model

/**
 * Mapea EvidenciaRequestDTO del backend.
 * El cuidador sube fotos del espacio antes y después del servicio.
 *
 * tipoMomento: "antes" o "despues"
 */
data class EvidenciaRequest(
    val idOrden: Int,
    val urlFoto: String,
    val tipoMomento: String,
    val descripcionEstado: String?
)