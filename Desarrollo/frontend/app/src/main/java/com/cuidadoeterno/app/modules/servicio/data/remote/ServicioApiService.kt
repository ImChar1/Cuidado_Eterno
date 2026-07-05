package com.cuidadoeterno.app.modules.servicio.data.remote


import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.servicio.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ServicioApiService {

    // ── CLIENTE ─────────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/ordenes/tipos-solicitud
     * Obtiene el catálogo de tipos de servicio disponibles.
     */
    @GET("ordenes/tipos-solicitud")
    suspend fun obtenerTiposSolicitud(): Response<ApiResponse<List<TipoSolicitudResponse>>>
    /**
     * POST /api/v1/ordenes
     * El cliente crea una nueva solicitud de servicio.
     * Requiere: ROLE_CLIENTE
     */
    @POST("ordenes")
    suspend fun crearOrden(
        @Body request: OrdenRequest
    ): Response<ApiResponse<Int>>

    /**
     * GET /api/v1/ordenes/historial/cliente/{idCliente}
     * Historial completo de solicitudes del cliente.
     * Requiere: ROLE_CLIENTE
     */
    @GET("ordenes/historial/cliente/{idCliente}")
    suspend fun obtenerHistorialCliente(
        @Path("idCliente") idCliente: Int
    ): Response<ApiResponse<List<OrdenResponse>>>

    /**
     * POST /api/v1/ordenes/{idOrden}/calificar
     * El cliente califica el servicio completado.
     * Requiere: ROLE_CLIENTE
     */
    @POST("ordenes/{idOrden}/calificar")
    suspend fun calificarServicio(
        @Path("idOrden") idOrden: Int,
        @Body request: CalificacionRequest
    ): Response<ApiResponse<Void>>

    // ── CUIDADOR ────────────────────────────────────────────────────────────────

    /**
     * GET /api/v1/ordenes/disponibles?idCementerio=1
     * Solicitudes pendientes filtradas por cementerio.
     * Requiere: ROLE_CUIDADOR
     */
    @GET("ordenes/disponibles")
    suspend fun obtenerSolicitudesDisponibles(
        @Query("idCementerio") idCementerio: Int
    ): Response<ApiResponse<List<OrdenResponse>>>

    /**
     * PUT /api/v1/ordenes/{idOrden}/aceptar?idCuidador=5
     * El cuidador acepta una solicitud disponible.
     * Requiere: ROLE_CUIDADOR
     */
    @PUT("ordenes/{idOrden}/aceptar")
    suspend fun aceptarSolicitud(
        @Path("idOrden") idOrden: Int,
        @Query("idCuidador") idCuidador: Int
    ): Response<ApiResponse<Void>>

    /**
     * GET /api/v1/ordenes/historial/cuidador/{idCuidador}
     * Historial de órdenes del cuidador.
     * Requiere: ROLE_CUIDADOR
     */
    @GET("ordenes/historial/cuidador/{idCuidador}")
    suspend fun obtenerHistorialCuidador(
        @Path("idCuidador") idCuidador: Int
    ): Response<ApiResponse<List<OrdenResponse>>>

    /**
     * POST /api/v1/ordenes/{idOrden}/evidencia
     * El cuidador sube fotos de evidencia.
     * Requiere: ROLE_CUIDADOR
     */
    @POST("ordenes/{idOrden}/evidencia")
    suspend fun subirEvidencia(
        @Path("idOrden") idOrden: Int,
        @Body request: EvidenciaRequest
    ): Response<ApiResponse<Void>>

    /**
     * PATCH /api/v1/ordenes/{idOrden}/sub-estado
     * Actualiza el sub-estado del servicio en tiempo real (Ej: EN_CAMINO, TRABAJANDO)
     */
    @PATCH("ordenes/{idOrden}/sub-estado")
    suspend fun actualizarSubEstado(
        @Path("idOrden") idOrden: Int,
        @Query("idCuidador") idCuidador: Int,
        @Query("nuevoSubEstado") nuevoSubEstado: String
    ): Response<ApiResponse<OrdenResponse>>

    /**
     * PUT /api/v1/ordenes/{idOrden}/finalizar
     * El cuidador finaliza la orden, recibiendo su pago en la billetera virtual.
     * Requiere: ROLE_CUIDADOR
     */
    @PUT("ordenes/{idOrden}/finalizar")
    suspend fun finalizarServicio(
        @Path("idOrden") idOrden: Int
    ): Response<ApiResponse<OrdenResponse>>

    @GET("cementerios")
    suspend fun obtenerCementerios(): Response<ApiResponse<List<ElementoDropdown>>>

    @GET("tipos-espacio")
    suspend fun obtenerTiposEspacio(): Response<ApiResponse<List<ElementoDropdown>>>
}