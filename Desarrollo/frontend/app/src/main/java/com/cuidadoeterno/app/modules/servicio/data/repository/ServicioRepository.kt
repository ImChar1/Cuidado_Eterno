package com.cuidadoeterno.app.modules.servicio.data.repository


import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.*
import com.cuidadoeterno.app.modules.servicio.data.remote.ServicioApiService

class ServicioRepository(
    private val api: ServicioApiService
) {

    // ── CLIENTE ─────────────────────────────────────────────────────────────────

    suspend fun crearOrden(request: OrdenRequest): NetworkResult<Int> {
        return try {
            val response = api.crearOrden(request)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Error al crear la solicitud"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerHistorialCliente(
        idCliente: Int
    ): NetworkResult<List<OrdenResponse>> {
        return try {
            val response = api.obtenerHistorialCliente(idCliente)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()?.data ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar el historial")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun calificarServicio(
        idOrden: Int,
        request: CalificacionRequest
    ): NetworkResult<Unit> {
        return try {
            val response = api.calificarServicio(idOrden, request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Error al calificar el servicio"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    // ── CUIDADOR ────────────────────────────────────────────────────────────────

    suspend fun obtenerSolicitudesDisponibles(
        idCementerio: Int
    ): NetworkResult<List<OrdenResponse>> {
        return try {
            val response = api.obtenerSolicitudesDisponibles(idCementerio)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()?.data ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar solicitudes")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun aceptarSolicitud(
        idOrden: Int,
        idCuidador: Int
    ): NetworkResult<Unit> {
        return try {
            val response = api.aceptarSolicitud(idOrden, idCuidador)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Error al aceptar la solicitud"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerHistorialCuidador(
        idCuidador: Int
    ): NetworkResult<List<OrdenResponse>> {
        return try {
            val response = api.obtenerHistorialCuidador(idCuidador)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()?.data ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar el historial")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun subirEvidencia(
        idOrden: Int,
        request: EvidenciaRequest
    ): NetworkResult<Unit> {
        return try {
            val response = api.subirEvidencia(idOrden, request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Error al subir la evidencia"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }
}