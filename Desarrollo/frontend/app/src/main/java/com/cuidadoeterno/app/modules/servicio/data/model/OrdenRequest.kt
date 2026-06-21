package com.cuidadoeterno.app.modules.servicio.data.model

import java.math.BigDecimal

/**
 * Mapea OrdenRequestDTO del backend.
 * Lo construye CrearSolicitudViewModel al confirmar la solicitud.
 * NO incluye idCuidador — el cuidador acepta después por su cuenta.
 */
data class OrdenRequest(
    // Datos de la solicitud
    val idCliente: Int,
    val idTipoSolicitud: Int,
    val fechaProgramada: String,        // "yyyy-MM-ddTHH:mm:ss"
    val montoTotalServicio: BigDecimal,
    val observaciones: String?,

    // Datos del espacio del difunto
    val idCementerio: Int,
    val idTipoEspacio: Int,
    val sectorPabellon: String,
    val numeroSepultura: String,
    val pisoNivel: String?,
    val pasillo: String?,
    val materialPrincipal: String?,

    // Datos del fallecido
    val nombres: String,
    val apellidos: String,
    val fechaNacimiento: String?,       // "yyyy-MM-dd" opcional
    val fechaDefuncion: String?,        // "yyyy-MM-dd" opcional
    val epitafio: String?,

    // Insumos opcionales
    val insumos: List<InsumoRequest>?
)