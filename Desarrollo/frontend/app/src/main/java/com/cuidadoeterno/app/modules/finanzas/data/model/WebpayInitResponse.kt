package com.cuidadoeterno.app.modules.finanzas.data.model

import com.google.gson.annotations.SerializedName

data class WebpayInitResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("url") // <-- ESTO ES CLAVE: Transbank lo envía como "url"
    val urlWebpay: String
)