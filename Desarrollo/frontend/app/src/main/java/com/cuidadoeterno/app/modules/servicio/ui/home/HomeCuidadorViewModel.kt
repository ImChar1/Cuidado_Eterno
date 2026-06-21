package com.cuidadoeterno.app.modules.servicio.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.cementerio.data.model.CementerioResponse
import com.cuidadoeterno.app.modules.cementerio.data.repository.CementerioRepository
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class HomeCuidadorUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nombreUsuario: String = "",
    val idCuidador: Int = 0,
    // Cementerios disponibles para filtrar
    val cementerios: List<CementerioResponse> = emptyList(),
    val cementerioSeleccionado: CementerioResponse? = null,
    // Solicitudes disponibles en el cementerio seleccionado
    val solicitudesDisponibles: List<OrdenResponse> = emptyList(),
    // Orden activa del cuidador (si tiene una en proceso)
    val ordenEnProceso: OrdenResponse? = null,
    val aceptandoSolicitud: Boolean = false
)

class HomeCuidadorViewModel(
    private val servicioRepository: ServicioRepository,
    private val cementerioRepository: CementerioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeCuidadorUiState())
    val uiState: StateFlow<HomeCuidadorUiState> = _uiState

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val nombre = sessionManager.nombre.first() ?: ""
            val idPersona = sessionManager.idPersona.first() ?: 0

            _uiState.value = _uiState.value.copy(
                nombreUsuario = nombre,
                idCuidador = idPersona
            )

            // Cargar cementerios disponibles para que el cuidador filtre
            when (val result = cementerioRepository.obtenerCementeriosPorComuna(null)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        cementerios = result.data ?: emptyList()
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

            // Verificar si tiene una orden en proceso
            when (val result = servicioRepository.obtenerHistorialCuidador(idPersona)) {
                is NetworkResult.Success -> {
                    val enProceso = result.data?.find { it.estadoOrden == "en_proceso" }
                    _uiState.value = _uiState.value.copy(ordenEnProceso = enProceso)
                }
                else -> Unit
            }
        }
    }

    fun seleccionarCementerio(cementerio: CementerioResponse) {
        _uiState.value = _uiState.value.copy(
            cementerioSeleccionado = cementerio,
            solicitudesDisponibles = emptyList()
        )
        cargarSolicitudesDisponibles(cementerio.idCementerio)
    }

    private fun cargarSolicitudesDisponibles(idCementerio: Int) {
        viewModelScope.launch {
            when (val result = servicioRepository.obtenerSolicitudesDisponibles(idCementerio)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        solicitudesDisponibles = result.data ?: emptyList()
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                is NetworkResult.Loading -> Unit
            }
        }
    }

    fun aceptarSolicitud(idOrden: Int) {
        val idCuidador = _uiState.value.idCuidador
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(aceptandoSolicitud = true)
            when (val result = servicioRepository.aceptarSolicitud(idOrden, idCuidador)) {
                is NetworkResult.Success -> {
                    // Recargar para reflejar el cambio
                    cargarDatos()
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        aceptandoSolicitud = false,
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
