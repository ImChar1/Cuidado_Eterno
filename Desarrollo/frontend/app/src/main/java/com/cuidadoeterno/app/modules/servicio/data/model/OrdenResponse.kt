package com.cuidadoeterno.app.modules.servicio.data.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Mapea OrdenResponseDTO del backend.
 * Usado en:
 * - HomeClienteScreen: solicitudes activas
 * - HomeCuidadorScreen: solicitudes disponibles y orden en proceso
 * - HistorialScreen: historial completo
 */
data class OrdenResponse(
    val idOrden: Int,
    val idSolicitud: Int,
    val nombreServicio: String,
    val nombreCuidador: String,
    val ubicacionEspacio: String?,
    val fechaCreacion: String?,
    val fechaProgramada: String?,
    val estadoOrden: String,
    val montoTotal: Double,
    val tieneEvidencia: Boolean,
    val tieneCalificacion: Boolean,
    val subEstadoOrden: String?
)