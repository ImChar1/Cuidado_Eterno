package com.cuidadoeterno.app.modules.servicio.data.model

data class InsumoSeleccionado(
    val idProducto: Int,
    val idPuesto: Int,       // Tienda/Puesto del cementerio de donde se retira el insumo
    val cantidad: Int,
    val montoTotal: Double
)