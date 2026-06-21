package com.cuidadoeterno.app.modules.cementerio.data.model


/**
 * Mapea CatalogDTO del backend.
 * Se usa para los desplegables de:
 * - Regiones, Provincias, Comunas
 * - Tipos de espacio
 */
data class CatalogItem(
    val id: Int,
    val nombre: String
)