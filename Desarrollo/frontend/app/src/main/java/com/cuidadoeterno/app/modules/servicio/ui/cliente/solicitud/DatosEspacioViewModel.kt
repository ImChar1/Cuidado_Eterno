package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.ElementoDropdown
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Eliminamos ubicacionSugerida de aquí
data class DatosEspacioUiState(
    val isLoading: Boolean = false,
    val cementerios: List<ElementoDropdown> = emptyList(),
    val tiposEspacio: List<ElementoDropdown> = emptyList(),
    val error: String? = null
)

class DatosEspacioViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DatosEspacioUiState())
    val uiState: StateFlow<DatosEspacioUiState> = _uiState.asStateFlow()

    init {
        cargarDatosIniciales()
    }

    fun cargarDatosIniciales() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val cementeriosDef = async { repository.obtenerCementerios() }
            val tiposEspacioDef = async { repository.obtenerTiposEspacio() }

            val resCementerios = cementeriosDef.await()
            val resTiposEspacio = tiposEspacioDef.await()

            if (resCementerios is NetworkResult.Error || resTiposEspacio is NetworkResult.Error) {
                val errorMsg = (resCementerios as? NetworkResult.Error)?.message
                    ?: (resTiposEspacio as? NetworkResult.Error)?.message
                    ?: "Error al cargar los catálogos del servidor"

                _uiState.value = _uiState.value.copy(isLoading = false, error = errorMsg)
                return@launch
            }


            // 1. Extraemos las listas (Ya vienen como ElementoDropdown desde el Repository)
            val listaCementerios = (resCementerios as? NetworkResult.Success)?.data ?: emptyList()
            val listaTipos = (resTiposEspacio as? NetworkResult.Success)?.data ?: emptyList()

            // 2. Actualizamos el estado limpio y directo
            _uiState.value = DatosEspacioUiState(
                isLoading = false,
                cementerios = listaCementerios,
                tiposEspacio = listaTipos,
                error = null
            )
        }
    }

    private fun Any.map(function: Any) {}
}