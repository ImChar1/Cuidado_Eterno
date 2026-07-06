package com.cuidadoeterno.app.modules.finanzas.data.remote

import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.finanzas.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanzasApiService {
    @POST("finanzas/iniciar")
    suspend fun iniciarTransaccion(@Body request: WebpayInitRequest): Response<ApiResponse<WebpayInitResponse>>

    @POST("finanzas/confirmar")
    suspend fun confirmarTransaccion(@Query("token_ws") token: String): Response<ApiResponse<WebpayCommitResponse>>
}