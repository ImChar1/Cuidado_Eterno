package com.cuidadoeterno.app.modules.usuario.data.remote

import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.usuario.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>

    @POST("auth/registro/cliente")
    suspend fun registrarCliente(
        @Body request: RegistroClienteRequest
    ): Response<ApiResponse<Void>>

    @Multipart
    @POST("auth/registro/cuidador")
    suspend fun registrarCuidador(
        @Part("datos") datos: okhttp3.RequestBody, // Envía el JSON del DTO
        @Part documento: okhttp3.MultipartBody.Part // Envía el archivo binario
    ): Response<ApiResponse<Void>>

    @GET("auth/perfil")
    suspend fun obtenerPerfil(): Response<ApiResponse<PerfilResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Void>>
}
