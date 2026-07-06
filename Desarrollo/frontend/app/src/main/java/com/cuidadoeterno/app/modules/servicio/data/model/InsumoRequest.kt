package com.cuidadoeterno.app.modules.servicio.data.model

import java.math.BigDecimal

data class InsumoRequest(
    val idPuesto: Int,
    val montoTotal: BigDecimal
)
