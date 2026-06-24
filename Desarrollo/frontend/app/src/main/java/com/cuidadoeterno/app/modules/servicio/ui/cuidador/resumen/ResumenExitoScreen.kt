package com.cuidadoeterno.app.modules.servicio.ui.cuidador.resumen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ResumenExitoUiState(
    val isLoading: Boolean = true,
    val ordenFinalizada: OrdenResponse? = null,
    val error: String? = null
)

class ResumenExitoViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenExitoUiState())
    val uiState: StateFlow<ResumenExitoUiState> = _uiState.asStateFlow()

    fun cargarResumenOrden(idCuidador: Int, idOrden: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Reutilizamos el endpoint de historial para buscar la orden exacta y su monto final
            when (val result = repository.obtenerHistorialCuidador(idCuidador)) {
                is NetworkResult.Success -> {
                    val ordenEncontrada = result.data?.find { it.idOrden == idOrden }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        ordenFinalizada = ordenEncontrada
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