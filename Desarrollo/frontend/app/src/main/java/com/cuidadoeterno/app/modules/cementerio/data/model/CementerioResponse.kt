package com.cuidadoeterno.app.modules.cementerio.data.model


/**
 * Mapea CementerioResponseDTO del backend.
 * Usado en:
 * - HomeCuidadorScreen: selector de cementerio
 * - CrearSolicitudScreen: desplegable de cementerio
 */
data class CementerioResponse(
    val idCementerio: Int,
    val nombreCementerio: String,
    val direccion: String,
    val nombreComuna: String
)