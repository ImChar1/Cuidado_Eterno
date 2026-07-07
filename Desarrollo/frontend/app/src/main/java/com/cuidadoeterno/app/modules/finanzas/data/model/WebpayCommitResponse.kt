package com.cuidadoeterno.app.modules.finanzas.data.model
import com.google.gson.annotations.SerializedName

data class WebpayCommitResponse(
    @SerializedName("status")
    val estadoPago: String = "",

    @SerializedName("amount")
    val monto: Double = 0.0,

    @SerializedName("authorizationCode")
    val codigoAutorizacion: String = "",

    @SerializedName("buyOrder")
    val numeroOrden: String = "" // Agregado para pasarlo a la navegación
)