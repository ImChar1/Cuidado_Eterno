package com.cuidadoeterno.app.modules.cementerio.data.repository


import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.cementerio.data.model.CatalogItem
import com.cuidadoeterno.app.modules.cementerio.data.model.CementerioResponse
import com.cuidadoeterno.app.modules.cementerio.data.model.EspacioResponse
import com.cuidadoeterno.app.modules.cementerio.data.remote.CementerioApiService

class CementerioRepository(
    private val api: CementerioApiService
) {

    // ── Catálogos ───────────────────────────────────────────────────────────────

    suspend fun obtenerRegiones(): NetworkResult<List<CatalogItem>> {
        return try {
            val response = api.obtenerRegiones()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar regiones")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerProvincias(idRegion: Int): NetworkResult<List<CatalogItem>> {
        return try {
            val response = api.obtenerProvincias(idRegion)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar provincias")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerComunas(idProvincia: Int): NetworkResult<List<CatalogItem>> {
        return try {
            val response = api.obtenerComunas(idProvincia)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar comunas")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerTiposEspacio(): NetworkResult<List<CatalogItem>> {
        return try {
            val response = api.obtenerTiposEspacio()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar tipos de espacio")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    // ── Cementerios ─────────────────────────────────────────────────────────────

    /**
     * Si idComuna es null llama a buscar con nombre vacío para traer todos.
     * Usado en HomeCuidadorViewModel para cargar el selector de cementerios.
     */
    suspend fun obtenerCementeriosPorComuna(
        idComuna: Int?
    ): NetworkResult<List<CementerioResponse>> {
        return try {
            val response = if (idComuna != null) {
                api.obtenerCementeriosPorComuna(idComuna)
            } else {
                // Sin filtro de comuna — busca con string vacío para traer todos
                api.buscarCementerios("")
            }
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("Error al cargar cementerios")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun buscarCementerios(nombre: String): NetworkResult<List<CementerioResponse>> {
        return try {
            val response = api.buscarCementerios(nombre)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body() ?: emptyList())
            } else {
                NetworkResult.Error("No se encontraron cementerios")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    // ── Espacios ────────────────────────────────────────────────────────────────

    suspend fun registrarEspacioCliente(
        idCementerio: Int,
        idTipoEspacio: Int,
        sectorPabellon: String,
        numeroSepultura: String,
        pisoNivel: String?,
        pasillo: String?,
        materialPrincipal: String?
    ): NetworkResult<EspacioResponse> {
        return try {
            val body = mapOf(
                "idCementerio"     to idCementerio,
                "idTipoEspacio"    to idTipoEspacio,
                "sectorPabellon"   to sectorPabellon,
                "numeroSepultura"  to numeroSepultura,
                "pisoNivel"        to pisoNivel,
                "pasillo"          to pasillo,
                "materialPrincipal" to materialPrincipal
            )
            val response = api.registrarEspacioCliente(body)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(
                    response.body()?.message ?: "Error al registrar el espacio"
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }
}