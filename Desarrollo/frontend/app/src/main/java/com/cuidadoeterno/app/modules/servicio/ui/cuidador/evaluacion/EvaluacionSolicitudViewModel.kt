package com.cuidadoeterno.app.modules.servicio.ui.cuidador.evaluacion

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

data class EvaluacionUiState(
    val isLoading: Boolean = false,
    val ordenDetalle: OrdenResponse? = null,
    val isAceptando: Boolean = false,
    val aceptacionExitosa: Boolean = false,
    val error: String? = null
)

class EvaluacionSolicitudViewModel(
    private val servicioRepository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(EvaluacionUiState())
    val uiState: StateFlow<EvaluacionUiState> = _uiState.asStateFlow()

    // En un caso real, aquí harías un GET para traer el detalle completo de la orden.
    // Como ya tenemos el objeto desde el Home, podemos inyectarlo directamente para ahorrar carga.
    fun cargarDetalleOrden(orden: OrdenResponse) {
        _uiState.value = _uiState.value.copy(ordenDetalle = orden)
    }

    fun aceptarTrabajo() {
        val ordenActual = _uiState.value.ordenDetalle ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAceptando = true, error = null)
            val idCuidador = sessionManager.idPersona.first() ?: return@launch

            // Llamada al endpoint que ya tienes en ServicioRepository
            when (val result = servicioRepository.aceptarSolicitud(ordenActual.idOrden!!, idCuidador)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isAceptando = false,
                        aceptacionExitosa = true
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isAceptando = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }
}