package com.cuidadoeterno.app.modules.finanzas.data.remote

import com.cuidadoeterno.app.core.network.ApiResponse
import com.cuidadoeterno.app.modules.finanzas.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface FinanzasApiService {
    @POST("finanzas/webpay/iniciar")
    suspend fun iniciarTransaccion(@Body request: WebpayInitRequest): Response<ApiResponse<WebpayInitResponse>>

    @GET("finanzas/webpay/confirmar")
    suspend fun confirmarTransaccion(@Query("token_ws") token: String): Response<ApiResponse<WebpayCommitResponse>>
}