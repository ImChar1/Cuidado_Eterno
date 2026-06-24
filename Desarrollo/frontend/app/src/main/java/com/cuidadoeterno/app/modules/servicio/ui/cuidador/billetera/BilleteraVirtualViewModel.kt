package com.cuidadoeterno.app.modules.servicio.ui.cuidador.billetera

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

data class BilleteraUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val saldoDisponible: Double = 0.0,
    val pagosCompletados: List<OrdenResponse> = emptyList()
)

class BilleteraViewModel(
    private val repository: ServicioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BilleteraUiState())
    val uiState: StateFlow<BilleteraUiState> = _uiState.asStateFlow()

    fun cargarBilletera() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val idCuidador = sessionManager.idPersona.first() ?: 0

            when (val result = repository.obtenerHistorialCuidador(idCuidador)) {
                is NetworkResult.Success -> {
                    // Filtramos solo las órdenes que el cliente ya validó/calificó o que están formalmente terminadas
                    val completadas = result.data?.filter { it.estadoOrden == "completada" } ?: emptyList()

                    // Sumamos las ganancias
                    val totalGanado = completadas.sumOf { it.montoTotal.toDouble() }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        saldoDisponible = totalGanado,
                        pagosCompletados = completadas
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is NetworkResult.Loading -> { }
            }
        }
    }
}