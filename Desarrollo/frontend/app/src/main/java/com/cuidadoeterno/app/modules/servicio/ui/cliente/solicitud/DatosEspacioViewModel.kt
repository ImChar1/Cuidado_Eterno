package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// DTOs internos ultra-limpios para los Dropdowns
data class ElementoDropdown(val id: Int, val nombre: String)

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
        cargarCatalogosGeograficos()
    }

    private fun cargarCatalogosGeograficos() {
        viewModelScope.launch {
            _uiState.value = DatosEspacioUiState(isLoading = true)

            // Simulación de la llamada concurrente a tus futuros endpoints de catálogo
            // repository.obtenerCementerios() y repository.obtenerTiposEspacio()

            val listaCementeriosMock = listOf(
                ElementoDropdown(1, "Cementerio General de Santiago"),
                ElementoDropdown(2, "Cementerio Municipal de Concepción")
            )
            val listaTiposMock = listOf(
                ElementoDropdown(1, "Mausoleo Monumental"),
                ElementoDropdown(2, "Nicho en Pabellón"),
                ElementoDropdown(3, "Sepultura en Tierra")
            )

            _uiState.value = DatosEspacioUiState(
                isLoading = false,
                cementerios = listaCementeriosMock,
                tiposEspacio = listaTiposMock
            )
        }
    }
}