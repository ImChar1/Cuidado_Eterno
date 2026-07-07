package com.cuidadoeterno.app.modules.servicio.ui.cuidador.home
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeCuidadorUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nombreUsuario: String = "",
    val idCuidador: Int = 0,
    val estadoVerificacion: String = "pendiente" // Agregamos el estado vital
)

class HomeCuidadorViewModel(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository // Inyectamos AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeCuidadorUiState())
    val uiState: StateFlow<HomeCuidadorUiState> = _uiState

    init {
        cargarDatosBasicos()
        verificarEstadoCuidador()
    }

    private fun cargarDatosBasicos() {
        viewModelScope.launch {
            val nombre = sessionManager.nombre.first() ?: "Cuidador"
            val id = sessionManager.idPersona.first() ?: 0
            _uiState.value = _uiState.value.copy(
                nombreUsuario = nombre,
                idCuidador = id
            )
        }
    }

    // Refresca el estado consultando el perfil real al backend
    fun verificarEstadoCuidador() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = authRepository.obtenerPerfil()) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        estadoVerificacion = result.data?.estadoVerificacion ?: "pendiente"
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