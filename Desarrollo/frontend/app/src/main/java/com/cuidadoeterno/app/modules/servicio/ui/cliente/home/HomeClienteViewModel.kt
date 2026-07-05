package com.cuidadoeterno.app.modules.servicio.ui.cliente.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeClienteUiState(
    val nombreUsuario: String = "",
    val rol: String = ""
)

class HomeClienteViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeClienteUiState())
    val uiState: StateFlow<HomeClienteUiState> = _uiState.asStateFlow()

    init {
        cargarDatosSesion()
    }

    private fun cargarDatosSesion() {
        viewModelScope.launch {
            // Solo leemos el nombre guardado en el teléfono al iniciar sesión
            val nombre = sessionManager.nombre.first() ?: "Cliente"
            val rol = sessionManager.rol.first() ?: "CLIENTE"
            _uiState.value = _uiState.value.copy(nombreUsuario = nombre)
        }
    }
}