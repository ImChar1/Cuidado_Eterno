package com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.TipoSolicitudResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ServiciosUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val tiposServicio: List<TipoSolicitudResponse> = emptyList()
)

class ServiciosViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiciosUiState())
    val uiState: StateFlow<ServiciosUiState> = _uiState.asStateFlow()

    init {
        cargarTiposDeServicio()
    }

    fun cargarTiposDeServicio() {
        viewModelScope.launch {
            _uiState.value = ServiciosUiState(isLoading = true)

            when (val result = repository.obtenerTiposSolicitud()) {
                is NetworkResult.Success -> {
                    _uiState.value = ServiciosUiState(
                        isLoading = false,
                        tiposServicio = result.data ?: emptyList()
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = ServiciosUiState(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}