package com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.usuario.data.model.RegistroCuidadorRequest
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegistroCuidadorUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registroExitoso: Boolean = false,
    // Control del flujo de 2 pasos
    val pasoActual: Int = 1,             // 1 = datos personales, 2 = documentación
    // Estado de subida de documento
    val archivoBytes: ByteArray? = null,
    val nombreArchivo: String? = null,
    val cargandoDocumento: Boolean = false
)

class RegistroCuidadorViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroCuidadorUiState())
    val uiState: StateFlow<RegistroCuidadorUiState> = _uiState

    // ── Paso 1 → Paso 2 ────────────────────────────────────────────────────────

    fun avanzarAPaso2(
        rut: String,
        nombre: String,
        apPaterno: String,
        email: String,
        telefono: String,
        fechaNacimiento: String,
        genero: String,
        nombreUsuario: String,
        clave: String,
        confirmarClave: String
    ) {
        // Validaciones del paso 1 antes de avanzar
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

        // Todo ok — avanzar al paso 2
        _uiState.value = _uiState.value.copy(pasoActual = 2, error = null)
    }

    fun volverAPaso1() {
        _uiState.value = _uiState.value.copy(pasoActual = 1, error = null)
    }

    // ── Subida de documento (Paso 2) ────────────────────────────────────────────
    fun setDocumentoSeleccionado(bytes: ByteArray, nombre: String) {
        _uiState.value = _uiState.value.copy(
            archivoBytes = bytes,
            nombreArchivo = nombre,
            error = null
        )
    }

    // ── Registro final (Paso 2) ─────────────────────────────────────────────────

    fun registrar(
        // Datos del paso 1 (los mantenemos en la Screen con remember)
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
        // Datos del paso 2
        tipoDocumento: String,
        numeroRegistro: String
    ) {
        val bytes = _uiState.value.archivoBytes
        val nombreArc = _uiState.value.nombreArchivo

        if (bytes == null || nombreArc == null) {
            _uiState.value = _uiState.value.copy(error = "Debes seleccionar tu documento de certificación")
            return
        }

        if (tipoDocumento.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Selecciona el tipo de documento")
            return
        }

        val tipoDocumentoBackend = when (tipoDocumento) {
            "Cédula de Identidad" -> "cedula"
            "Certificado Municipal" -> "certificado_municipal"
            "Registro de Cementerio" -> "registro_cementerio"
            else -> "otro" // Si no calza, enviamos "otro" o puedes manejar el error.
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val request = RegistroCuidadorRequest(
                rut = rut,
                nombre = nombre,
                apPaterno = apPaterno,
                apMaterno = apMaterno.ifBlank { null },
                email = email,
                telefono = telefono,
                fechaNacimiento = fechaNacimiento,
                genero = genero,
                nombreUsuario = nombreUsuario,
                clave = clave,
                tipoDocumento = tipoDocumentoBackend,
                numeroRegistro = numeroRegistro.ifBlank { null }
            )

            when (val result = repository.registrarCuidador(request, bytes, nombreArc)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, registroExitoso = true)
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = result.message)
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