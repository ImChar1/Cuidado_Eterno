package com.cuidadoeterno.app.modules.servicio.data.repository

import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.ProductoCatalogoResponse
import com.cuidadoeterno.app.modules.servicio.data.remote.InventarioApiService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class InventarioRepository(private val api: InventarioApiService) {

    suspend fun obtenerCatalogoCompleto(idCementerio: Int): NetworkResult<List<ProductoCatalogoResponse>> {
        return try {
            // 1. Obtenemos los puestos del cementerio
            val puestosResponse = api.obtenerPuestosPorCementerio(idCementerio)

            if (puestosResponse.isSuccessful && puestosResponse.body() != null) {
                val puestos = puestosResponse.body()!!

                // 2. Obtenemos el catálogo de CADA puesto en paralelo
                val catalogoCompleto = coroutineScope {
                    puestos.map { puesto ->
                        async {
                            val catalogoResp = api.obtenerCatalogoPorPuesto(puesto.idPuesto)
                            if (catalogoResp.isSuccessful) {
                                // Asignamos el idPuesto a cada producto para guardarlo en el InsumoSeleccionado después
                                catalogoResp.body()?.map { it.apply { this.idPuesto = puesto.idPuesto } } ?: emptyList()
                            } else {
                                emptyList()
                            }
                        }
                    }.awaitAll().flatten() // Unimos todas las listas en una sola
                }

                // Filtramos solo los que tienen stock
                NetworkResult.Success(catalogoCompleto.filter { it.hayStock })
            } else {
                NetworkResult.Error("Error al obtener los puestos del cementerio")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }
}