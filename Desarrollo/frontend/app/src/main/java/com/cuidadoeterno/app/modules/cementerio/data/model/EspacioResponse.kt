package com.cuidadoeterno.app.modules.cementerio.data.model


/**
 * Mapea EspacioResponseDTO del backend.
 * Se retorna cuando el cliente registra o reutiliza un espacio.
 * Usado en CrearSolicitudScreen para confirmar el espacio registrado.
 */
data class EspacioResponse(
    val idEspacio: Int,
    val sectorPabellon: String,
    val numeroSepultura: String,
    val pisoNivel: String?,
    val pasillo: String?,
    val tipoEspacio: String,
    val nombreCementerio: String
)
