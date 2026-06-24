package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.modules.servicio.data.model.SolicitudDraft
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    data class Success(val idOrden: Long, val tokenWebpay: String, val urlTransbank: String) : CheckoutUiState()
    data class Error(val mensaje: String) : CheckoutUiState()
}

class ResumenSolicitudViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun confirmarOrden(draft: SolicitudDraft) {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading

            // Simulamos el armado del Body y la llamada a tu Endpoint real:
            // val request = OrdenRequest(draft.idTipoSolicitud, draft.montoTotal...)
            // val response = repository.crearOrden(request)

            try {
                // Mock de la respuesta exitosa del Backend con datos de Transbank
                _uiState.value = CheckoutUiState.Success(
                    idOrden = 45982L,
                    tokenWebpay = "01ab9823c_tbk_mock_token",
                    urlTransbank = "https://webpay3gint.transbank.cl/webpayserver/initTransaction"
                )
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error("Falló la comunicación con el servidor: ${e.message}")
            }
        }
    }
}