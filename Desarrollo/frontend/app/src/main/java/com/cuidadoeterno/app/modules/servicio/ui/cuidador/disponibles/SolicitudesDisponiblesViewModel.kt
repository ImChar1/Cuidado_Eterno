package com.cuidadoeterno.app.modules.servicio.ui.cuidador.disponibles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.ElementoDropdown
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SolicitudesDisponiblesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val cementerios: List<ElementoDropdown> = emptyList(),
    val cementerioSeleccionado: ElementoDropdown? = null,
    val solicitudes: List<OrdenResponse> = emptyList()
)

class SolicitudesDisponiblesViewModel(
    private val servicioRepository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SolicitudesDisponiblesUiState())
    val uiState: StateFlow<SolicitudesDisponiblesUiState> = _uiState.asStateFlow()

    init {
        cargarCementerios()
    }

    private fun cargarCementerios() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = servicioRepository.obtenerCementerios()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cementerios = result.data ?: emptyList()
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

    fun seleccionarCementerio(cementerio: ElementoDropdown) {
        _uiState.value = _uiState.value.copy(cementerioSeleccionado = cementerio)
        cargarSolicitudes(cementerio.id)
    }

    private fun cargarSolicitudes(idCementerio: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            // Asumo que esta función existe en tu repositorio ya que estaba en tu HomeCuidadorViewModel viejo
            when (val result = servicioRepository.obtenerSolicitudesDisponibles(idCementerio)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        solicitudes = result.data ?: emptyList()
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