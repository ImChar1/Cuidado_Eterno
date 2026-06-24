package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DetalleSolicitudUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val orden: OrdenResponse? = null
)

class DetalleSolicitudViewModel(
    private val repository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleSolicitudUiState())
    val uiState: StateFlow<DetalleSolicitudUiState> = _uiState.asStateFlow()

    fun cargarDetalleOrden(idOrden: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val idCliente = sessionManager.idPersona.first() ?: 0

            when (val result = repository.obtenerHistorialCliente(idCliente)) {
                is NetworkResult.Success -> {
                    // Filtramos la orden específica que el cliente quiere monitorear
                    val ordenEncontrada = result.data?.find { it.idOrden == idOrden }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        orden = ordenEncontrada
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}