package com.cuidadoeterno.app.modules.admin.ui.cuidador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.admin.data.model.CuidadorAdminResponse
import com.cuidadoeterno.app.modules.admin.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GestionCuidadoresUiState(
    val isLoading: Boolean = false,
    val listaCuidadores: List<CuidadorAdminResponse> = emptyList(),
    val error: String? = null
)

class GestionCuidadoresViewModel(private val repository: AdminRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(GestionCuidadoresUiState())
    val uiState: StateFlow<GestionCuidadoresUiState> = _uiState.asStateFlow()

    init { cargarCuidadores() }

    fun cargarCuidadores() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.listarCuidadores()) {
                is NetworkResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, listaCuidadores = result.data ?: emptyList())
                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun procesarValidacion(id: Int, aprobar: Boolean) {
        val nuevoEstado = if (aprobar) "VALIDADO" else "RECHAZADO"
        viewModelScope.launch {
            if (repository.actualizarEstado(id, nuevoEstado) is NetworkResult.Success) {
                cargarCuidadores() // refrescar lista
            }
        }
    }

    fun eliminarCuidador(id: Int) {
        viewModelScope.launch {
            if (repository.eliminarCuidador(id) is NetworkResult.Success) {
                cargarCuidadores()
            }
        }
    }
}