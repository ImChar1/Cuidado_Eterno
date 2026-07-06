package com.cuidadoeterno.app.modules.servicio.ui.cliente.home

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

data class HomeClienteUiState(
    val nombreUsuario: String = "Cargando...",
    val rol: String = "CLIENTE"
)

class HomeClienteViewModel(
    private val sessionManager: SessionManager,
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeClienteUiState())
    val uiState: StateFlow<HomeClienteUiState> = _uiState.asStateFlow()

}