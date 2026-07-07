package com.cuidadoeterno.app.modules.finanzas.data.repository
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.finanzas.data.model.WebpayCommitResponse
import com.cuidadoeterno.app.modules.finanzas.data.model.WebpayInitRequest
import com.cuidadoeterno.app.modules.finanzas.data.model.WebpayInitResponse
import com.cuidadoeterno.app.modules.finanzas.data.remote.FinanzasApiService

class FinanzasRepository(
    private val api: FinanzasApiService
) {
    suspend fun iniciarTransaccion(request: WebpayInitRequest): NetworkResult<WebpayInitResponse> {
        return try {
            val response = api.iniciarTransaccion(request)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Error al iniciar conexión con Webpay")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Excepción al iniciar pago: ${e.message}")
        }
    }

    suspend fun confirmarTransaccion(tokenWs: String): NetworkResult<WebpayCommitResponse> {
        return try {
            val response = api.confirmarTransaccion(tokenWs)
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Transacción rechazada o inválida")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Excepción al confirmar pago: ${e.message}")
        }
    }
}