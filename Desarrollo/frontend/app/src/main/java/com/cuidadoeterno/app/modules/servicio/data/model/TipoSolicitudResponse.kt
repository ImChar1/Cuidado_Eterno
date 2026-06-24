package com.cuidadoeterno.app.modules.servicio.data.model

import com.google.gson.annotations.SerializedName

data class TipoSolicitudResponse(
    @SerializedName("id_tipo_solicitud")
    val id: Int,

    @SerializedName("nombre_servicio")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("precio_base")
    val precioBase: Double,

    // Datos extra que te servirán para la pantalla explicativa
    @SerializedName("duracion_estimada_min")
    val duracionEstimadaMin: Int,

    @SerializedName("requiere_insumos")
    val requiereInsumos: Boolean
)