package com.cuidadoeterno.app.modules.servicio.ui.cliente.seguimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.CalificacionRequest // Asegúrate de tener este DTO
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CalificacionUiState(
    val isLoading: Boolean = false,
    val exito: Boolean = false,
    val error: String? = null
)

class CalificacionViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalificacionUiState())
    val uiState: StateFlow<CalificacionUiState> = _uiState.asStateFlow()

    fun enviarCalificacion(idOrden: Int, puntuacion: Int, comentario: String) {
        viewModelScope.launch {
            _uiState.value = CalificacionUiState(isLoading = true)

            // Asumiendo que tu DTO se llama CalificacionRequest y tiene estos campos
            val request = CalificacionRequest(
                idOrden = idOrden,
                puntuacion = puntuacion,
                comentario = comentario
            )

            when (val result = repository.calificarServicio(idOrden, request)) {
                is NetworkResult.Success -> {
                    _uiState.value = CalificacionUiState(exito = true)
                }
                is NetworkResult.Error -> {
                    _uiState.value = CalificacionUiState(error = result.message)
                }
                is NetworkResult.Loading -> { }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}