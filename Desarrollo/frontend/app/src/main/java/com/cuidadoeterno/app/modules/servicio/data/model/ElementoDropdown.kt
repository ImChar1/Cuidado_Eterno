package com.cuidadoeterno.app.modules.servicio.data.model

import com.google.gson.annotations.SerializedName

data class ElementoDropdown(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String
)