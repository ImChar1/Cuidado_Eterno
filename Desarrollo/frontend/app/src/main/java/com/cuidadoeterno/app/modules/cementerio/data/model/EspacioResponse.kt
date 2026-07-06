package com.cuidadoeterno.app.modules.cementerio.data.model

data class EspacioResponse(
    val idEspacio: Int,
    val sectorPabellon: String?,
    val numeroSepultura: String?,
    val pisoNivel: String?,
    val pasillo: String?,
    val tipoEspacio: String?,
    val nombreCementerio: String?
)
