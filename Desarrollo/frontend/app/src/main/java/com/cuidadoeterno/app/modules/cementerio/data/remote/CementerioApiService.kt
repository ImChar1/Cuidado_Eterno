package com.cuidadoeterno.app.modules.cementerio.data.remote


import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.cementerio.data.model.CatalogItem
import com.cuidadoeterno.app.modules.cementerio.data.model.CementerioResponse
import com.cuidadoeterno.app.modules.cementerio.data.model.EspacioResponse
import retrofit2.Response
import retrofit2.http.*

interface CementerioApiService {

    // ── Catálogos para desplegables ─────────────────────────────────────────────

    /**
     * GET /api/v1/cementerios/regiones
     * Listado de regiones para el desplegable de región.
     */
    @GET("cementerios/regiones")
    suspend fun obtenerRegiones(): Response<List<CatalogItem>>

    /**
     * GET /api/v1/cementerios/provincias/region/{idRegion}
     * Provincias de una región — se carga al seleccionar región.
     */
    @GET("cementerios/provincias/region/{idRegion}")
    suspend fun obtenerProvincias(
        @Path("idRegion") idRegion: Int
    ): Response<List<CatalogItem>>

    /**
     * GET /api/v1/cementerios/comunas/provincia/{idProvincia}
     * Comunas de una provincia — se carga al seleccionar provincia.
     */
    @GET("cementerios/comunas/provincia/{idProvincia}")
    suspend fun obtenerComunas(
        @Path("idProvincia") idProvincia: Int
    ): Response<List<CatalogItem>>

    /**
     * GET /api/v1/cementerios/tipos-espacio
     * Tipos de espacio (Nicho, Mausoleo, Bóveda, etc.)
     */
    @GET("cementerios/tipos-espacio")
    suspend fun obtenerTiposEspacio(): Response<List<CatalogItem>>

    // ── Cementerios ─────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/cementerios/comuna/{idComuna}
     * Cementerios de una comuna.
     * Usado en HomeCuidadorScreen para filtrar solicitudes por cementerio.
     * Cuando idComuna es null carga todos — útil para el home del cuidador.
     */
    @GET("cementerios/comuna/{idComuna}")
    suspend fun obtenerCementeriosPorComuna(
        @Path("idComuna") idComuna: Int
    ): Response<List<CementerioResponse>>

    /**
     * GET /api/v1/cementerios/buscar?nombre=xxx
     * Búsqueda de cementerio por nombre.
     */
    @GET("cementerios/buscar")
    suspend fun buscarCementerios(
        @Query("nombre") nombre: String
    ): Response<List<CementerioResponse>>

    // ── Espacios ────────────────────────────────────────────────────────────────

    /**
     * POST /api/v1/cementerios/espacios/cliente
     * El cliente registra el espacio de su difunto.
     * Si ya existe lo retorna, si no lo crea.
     * Requiere: ROLE_CLIENTE
     */
    @POST("cementerios/espacios/cliente")
    suspend fun registrarEspacioCliente(
        @Body request: Map<String, Any?>
    ): Response<ApiResponse<EspacioResponse>>
}