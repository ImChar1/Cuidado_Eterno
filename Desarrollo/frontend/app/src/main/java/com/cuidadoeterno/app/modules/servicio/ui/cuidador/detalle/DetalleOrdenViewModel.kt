package com.cuidadoeterno.app.modules.servicio.ui.cuidador.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class DetalleOrdenCuidadorUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val orden: OrdenResponse? = null,
    val mensajeExito: String? = null
)

class DetalleOrdenCuidadorViewModel(
    private val servicioRepository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleOrdenCuidadorUiState())
    val uiState: StateFlow<DetalleOrdenCuidadorUiState> = _uiState

    // Inicializa la pantalla cargando los datos iniciales de la orden seleccionada
    fun inicializarOrden(ordenInicial: OrdenResponse) {
        _uiState.value = _uiState.value.copy(orden = ordenInicial)
    }

    fun modificarSubEstado(idOrden: Int, nuevoSubEstado: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, mensajeExito = null)

            // Recuperamos el ID del cuidador autenticado en la app
            val idCuidador = sessionManager.idPersona.first() ?: 0

            if (idCuidador == 0) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sesión no válida. Identifícate de nuevo."
                )
                return@launch
            }

            // Llamada al repositorio que creamos en el paso anterior
            when (val result = servicioRepository.actualizarSubEstado(idOrden, idCuidador, nuevoSubEstado)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        orden = result.data,
                        mensajeExito = "Progreso actualizado a: ${formatearSubEstado(nuevoSubEstado)}"
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

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, mensajeExito = null)
    }

    // Auxiliar estético para mostrarle un texto limpio al usuario en la app
    private fun formatearSubEstado(estado: String): String {
        return when (estado) {
            "SIN_ASIGNAR" -> "Sin asignar"
            "ASIGNADO" -> "Asignado"
            "EN_CAMINO" -> "En camino al cementerio"
            "COMPRANDO_INSUMOS" -> "Comprando insumos"
            "EN_SITIO" -> "En el sepulcro"
            "TRABAJANDO" -> "Ejecutando mantenimiento"
            "EVIDENCIA_SUBIDA" -> "Evidencias registradas"
            "TERMINADO" -> "Servicio terminado"
            else -> estado
        }
    }
}