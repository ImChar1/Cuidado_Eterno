package com.cuidadoeterno.app.modules.servicio.data.model

import com.google.gson.annotations.SerializedName

data class TipoSolicitudResponse(
    @SerializedName("idTipoSolicitud")
    val id: Int,

    @SerializedName("nombreServicio")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("nombreServicio")
    val precioBase: Double,

    // Datos extra que te servirán para la pantalla explicativa
    @SerializedName("duracionEstimadaMin")
    val duracionEstimadaMin: Int,

    @SerializedName("requiereInsumos")
    val requiereInsumos: Boolean
)