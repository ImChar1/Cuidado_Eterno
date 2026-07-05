package com.cuidadoeterno.app.modules.servicio.data.remote

import com.cuidadoeterno.app.modules.servicio.data.model.ProductoCatalogoResponse
import com.cuidadoeterno.app.modules.servicio.data.model.PuestoVentaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface InventarioApiService {
    @GET("inventario/cementerio/{idCementerio}/puestos")
    suspend fun obtenerPuestosPorCementerio(@Path("idCementerio") idCementerio: Int): Response<List<PuestoVentaResponse>>

    @GET("inventario/puestos/{idPuesto}/catalogo")
    suspend fun obtenerCatalogoPorPuesto(@Path("idPuesto") idPuesto: Int): Response<List<ProductoCatalogoResponse>>
}