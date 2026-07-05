package com.cuidadoeterno.app.modules.servicio.data.model

data class ProductoCatalogoResponse(
    val idProducto: Int,
    val nombre: String,
    val descripcion: String?,
    val categoria: String?,
    val urlImagen: String?,
    val precioVenta: Double,
    val hayStock: Boolean,
    var idPuesto: Int = 0 // Lo añadiremos manualmente para saber a qué puesto pertenece
)