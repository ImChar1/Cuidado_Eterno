package com.cuidadoeterno.app.modules.admin.data.repository
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.admin.data.remote.AdminApiService
import com.cuidadoeterno.app.modules.admin.data.model.CuidadorAdminResponse

class AdminRepository(private val api: AdminApiService) {

    suspend fun listarCuidadores(estado: String = "pendiente"): NetworkResult<List<CuidadorAdminResponse>> {
        return try {
            val response = api.listarCuidadores(estado)
            // OJO AQUÍ: Extraemos el body()?.data porque tu backend devuelve ApiResponse
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Error al obtener los cuidadores")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun actualizarEstado(id: Int, estado: String): NetworkResult<Unit> {
        return try {
            val response = api.cambiarEstadoCuidador(id, estado)
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error(response.body()?.message ?: "Error al actualizar estado")
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun eliminarCuidador(id: Int): NetworkResult<Unit> {
        return try {
            val response = api.eliminarCuidador(id)
            if (response.isSuccessful) NetworkResult.Success(Unit)
            else NetworkResult.Error(response.body()?.message ?: "Error al eliminar")
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }
}