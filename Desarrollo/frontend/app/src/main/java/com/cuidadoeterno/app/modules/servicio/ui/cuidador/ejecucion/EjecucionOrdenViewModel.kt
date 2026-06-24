package com.cuidadoeterno.app.modules.servicio.ui.cuidador.ejecucion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// La máquina de estados estricta que exige tu tesis
enum class PasoEjecucion {
    COMPRA_INSUMOS,
    LLEGADA,
    FOTO_ANTES,
    CHECKLIST_EPP,
    FOTO_DESPUES,
    FINALIZADO
}

data class EjecucionUiState(
    val isLoading: Boolean = false,
    val orden: OrdenResponse? = null,
    val pasoActual: PasoEjecucion = PasoEjecucion.LLEGADA,
    val fotoBoletaUri: String? = null,
    val fotoAntesUri: String? = null,
    val fotoDespuesUri: String? = null,
    val checkEppConfirmado: Boolean = false,
    val error: String? = null
)

class EjecucionOrdenViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EjecucionUiState())
    val uiState: StateFlow<EjecucionUiState> = _uiState.asStateFlow()

    fun inicializarOrden(orden: OrdenResponse, tieneInsumos: Boolean) {
        // Regla de negocio: Si tiene insumos, forzamos el paso de compra. Si no, directo a llegada.
        val pasoInicial = if (tieneInsumos) PasoEjecucion.COMPRA_INSUMOS else PasoEjecucion.LLEGADA

        _uiState.value = EjecucionUiState(
            orden = orden,
            pasoActual = pasoInicial
        )
    }

    // --- ACCIONES DE CADA PASO ---

    fun registrarBoletaInsumos(uriFoto: String) {
        _uiState.value = _uiState.value.copy(fotoBoletaUri = uriFoto)
        avanzarPaso(PasoEjecucion.LLEGADA)
    }

    fun registrarLlegada() {
        // Aquí podrías hacer un update al backend para cambiar el estado a "en_proceso"
        avanzarPaso(PasoEjecucion.FOTO_ANTES)
    }

    fun registrarFotoAntes(uriFoto: String) {
        _uiState.value = _uiState.value.copy(fotoAntesUri = uriFoto)
        avanzarPaso(PasoEjecucion.CHECKLIST_EPP)
    }

    fun confirmarChecklist() {
        _uiState.value = _uiState.value.copy(checkEppConfirmado = true)
        avanzarPaso(PasoEjecucion.FOTO_DESPUES)
    }

    fun registrarFotoDespues(uriFoto: String) {
        _uiState.value = _uiState.value.copy(fotoDespuesUri = uriFoto)
        finalizarServicio()
    }

    private fun finalizarServicio() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val state = _uiState.value

            // Aquí simulas la subida de todas las fotos a tu API usando el repositorio
            // val request = EvidenciaRequest(state.fotoAntesUri, state.fotoDespuesUri...)
            // repository.subirEvidencia(state.orden!!.idOrden!!, request)

            // Simulación de éxito
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                pasoActual = PasoEjecucion.FINALIZADO
            )
        }
    }

    private fun avanzarPaso(nuevoPaso: PasoEjecucion) {
        _uiState.value = _uiState.value.copy(pasoActual = nuevoPaso)
    }
}