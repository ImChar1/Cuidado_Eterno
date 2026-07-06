package com.cuidadoeterno.app.modules.finanzas.data.model

data class WebpayInitRequest(
    val idSolicitud: Int,
    val returnUrl: String
)