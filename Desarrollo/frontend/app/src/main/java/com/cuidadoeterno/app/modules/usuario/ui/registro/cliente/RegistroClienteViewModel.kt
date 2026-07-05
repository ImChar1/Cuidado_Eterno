package com.cuidadoeterno.app.modules.usuario.ui.registro.cliente


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.usuario.data.model.RegistroClienteRequest
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegistroClienteUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false
)

class RegistroClienteViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroClienteUiState())
    val uiState: StateFlow<RegistroClienteUiState> = _uiState

    fun registrar(
        rut: String,
        nombre: String,
        apPaterno: String,
        apMaterno: String,
        email: String,
        telefono: String,
        fechaNacimiento: String,
        genero: String,
        nombreUsuario: String,
        clave: String,
        confirmarClave: String
    ) {
        // Validaciones locales antes de llamar al backend
        if (rut.isBlank() || nombre.isBlank() || apPaterno.isBlank() ||
            email.isBlank() || telefono.isBlank() || fechaNacimiento.isBlank() ||
            genero.isBlank() || nombreUsuario.isBlank() || clave.isBlank()
        ) {
            _uiState.value = _uiState.value.copy(
                error = "Todos los campos obligatorios deben estar completos"
            )
            return
        }

        if (clave != confirmarClave) {
            _uiState.value = _uiState.value.copy(
                error = "Las contraseñas no coinciden"
            )
            return
        }

        if (clave.length < 8) {
            _uiState.value = _uiState.value.copy(
                error = "La contraseña debe tener al menos 8 caracteres"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = RegistroClienteRequest(
                rut = rut,
                nombre = nombre,
                apPaterno = apPaterno,
                apMaterno = apMaterno.ifBlank { null },
                email = email,
                telefono = telefono,
                fechaNacimiento = fechaNacimiento, // "yyyy-MM-dd"
                genero = genero,
                nombreUsuario = nombreUsuario,
                clave = clave,
                prefNotificacion = "push"
            )

            when (val result = repository.registrarCliente(request)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        registroExitoso = true
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    fun formatearRutParaBackend(rutIngresado: String): String {
        val limpio = rutIngresado.replace(".", "").replace("-", "").trim()
        if (limpio.length < 2) return limpio
        val cuerpo = limpio.substring(0, limpio.length - 1)
        val dv = limpio.substring(limpio.length - 1).uppercase()
        return "$cuerpo-$dv"
    }
}