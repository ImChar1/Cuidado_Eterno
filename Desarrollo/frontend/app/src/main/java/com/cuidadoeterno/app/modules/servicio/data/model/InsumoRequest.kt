package com.cuidadoeterno.app.modules.servicio.data.model

import java.math.BigDecimal

/**
 * Mapea InsumoRequestDTO del backend.
 * Representa un producto del catálogo que el cliente agrega
 * opcionalmente a su solicitud.
 */
data class InsumoRequest(
    val idPuesto: Int,
    val montoTotal: BigDecimal
)
