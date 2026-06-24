package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DetalleProductoUiState(
    val idProducto: Int = 0,
    val nombre: String = "",
    val precioBase: Double = 0.0,
    val descripcion: String = "",
    val cantidadSeleccionada: Int = 1
) {
    val montoTotal get() = precioBase * cantidadSeleccionada
}

class DetalleProductoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleProductoUiState())
    val uiState: StateFlow<DetalleProductoUiState> = _uiState.asStateFlow()

    fun cargarProducto(idProducto: Int) {
        // En la vida real, aquí harías un GET al repositorio. Usaremos un mock:
        _uiState.value = DetalleProductoUiState(
            idProducto = idProducto,
            nombre = "Arreglo Rosas Blancas",
            precioBase = 12500.0,
            descripcion = "Arreglo floral fresco para sepulturas, incluye base de espuma para mayor duración."
        )
    }

    fun aumentarCantidad() {
        _uiState.update { it.copy(cantidadSeleccionada = it.cantidadSeleccionada + 1) }
    }

    fun disminuirCantidad() {
        _uiState.update {
            if (it.cantidadSeleccionada > 1) it.copy(cantidadSeleccionada = it.cantidadSeleccionada - 1) else it
        }
    }
}