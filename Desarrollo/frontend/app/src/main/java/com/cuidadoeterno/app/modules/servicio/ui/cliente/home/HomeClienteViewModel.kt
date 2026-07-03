package com.cuidadoeterno.app.modules.servicio.ui.cliente.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeClienteUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nombreUsuario: String = "",
    val solicitudesActivas: List<com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse> = emptyList()
)

class HomeClienteViewModel(
    private val servicioRepository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeClienteUiState())
    // Encapsulado correcto mediante asStateFlow
    val uiState: StateFlow<HomeClienteUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val nombre = sessionManager.nombre.first() ?: ""
            val idPersona = sessionManager.idPersona.first() ?: 0

            _uiState.value = _uiState.value.copy(nombreUsuario = nombre)

            when (val result = servicioRepository.obtenerHistorialCliente(idPersona)) {
                is NetworkResult.Success -> {
                    val activas = result.data
                        ?.filter { it.estadoOrden == "pendiente" || it.estadoOrden == "en_proceso" }
                        ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        solicitudesActivas = activas
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}