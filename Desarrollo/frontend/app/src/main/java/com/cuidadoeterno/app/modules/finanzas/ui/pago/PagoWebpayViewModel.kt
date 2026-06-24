package com.cuidadoeterno.app.modules.finanzas.ui.pago

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.finanzas.data.model.WebpayCommitResponse
import com.cuidadoeterno.app.modules.finanzas.data.repository.FinanzasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// La máquina de estados que propusiste
sealed class PagoWebpayUiState {
    object CargandoWebView : PagoWebpayUiState()
    object Confirmando : PagoWebpayUiState() // Cuando interceptamos el retorno y llamamos al commit
    data class Exitoso(val data: WebpayCommitResponse) : PagoWebpayUiState()
    data class Fallido(val mensaje: String) : PagoWebpayUiState()
}

class PagoWebpayViewModel(
    private val repository: FinanzasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PagoWebpayUiState>(PagoWebpayUiState.CargandoWebView)
    val uiState: StateFlow<PagoWebpayUiState> = _uiState.asStateFlow()

    /**
     * Se llama desde la vista (PagoWebpayScreen) en el instante que el WebView
     * detecta la URL de retorno con el token_ws.
     */
    fun confirmarPago(tokenWs: String) {
        viewModelScope.launch {
            // Cambiamos el estado para que la vista muestre un "Procesando pago..." y oculte el WebView
            _uiState.value = PagoWebpayUiState.Confirmando

            when (val result = repository.confirmarTransaccion(tokenWs)) {
                is NetworkResult.Success -> {
                    // El backend (Spring Boot) validó con Transbank y guardó la orden como "PAGADA"
                    _uiState.value = PagoWebpayUiState.Exitoso(result.data!!)
                }
                is NetworkResult.Error -> {
                    // Algo falló (sin fondos, tarjeta bloqueada, etc.)
                    _uiState.value = PagoWebpayUiState.Fallido(result.message ?: "Transacción rechazada")
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}