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

data class SolicitudesActivasUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    // Regla de negocio: un cliente solo puede tener 1 solicitud activa a la vez
    val ordenActiva: OrdenResponse? = null
)

class SolicitudesActivasViewModel(
    private val repository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudesActivasUiState())
    val uiState: StateFlow<SolicitudesActivasUiState> = _uiState.asStateFlow()

    init {
        cargarSolicitudActiva()
    }

    fun cargarSolicitudActiva() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val idCliente = sessionManager.idPersona.first() ?: 0

            when (val result = repository.obtenerHistorialCliente(idCliente)) {
                is NetworkResult.Success -> {
                    val activa = result.data?.firstOrNull {
                        it.estadoOrden != "completada" && it.estadoOrden != "cancelada"
                    }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        ordenActiva = activa
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