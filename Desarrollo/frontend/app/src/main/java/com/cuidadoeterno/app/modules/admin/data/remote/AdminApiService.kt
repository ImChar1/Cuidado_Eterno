package com.cuidadoeterno.app.modules.admin.data.remote
import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.admin.data.model.CuidadorAdminResponse
import retrofit2.Response
import retrofit2.http.*

interface AdminApiService {

    // Coincide con: GET /api/v1/admin/cuidadores?estado=...
    @GET("api/v1/admin/cuidadores")
    suspend fun listarCuidadores(
        @Query("estado") estado: String
    ): Response<ApiResponse<List<CuidadorAdminResponse>>>

    // Coincide con: PUT /api/v1/admin/cuidadores/{idPersona}/verificar?estado=...
    @PUT("api/v1/admin/cuidadores/{idPersona}/verificar")
    suspend fun cambiarEstadoCuidador(
        @Path("idPersona") idPersona: Int,
        @Query("estado") estado: String
    ): Response<ApiResponse<Unit>>

    // Coincide con: DELETE /api/v1/admin/cuidadores/{idPersona}
    @DELETE("api/v1/admin/cuidadores/{idPersona}")
    suspend fun eliminarCuidador(
        @Path("idPersona") idPersona: Int
    ): Response<ApiResponse<Unit>>
}