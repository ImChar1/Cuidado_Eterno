package com.cuidadoeterno.app.modules.servicio.ui.cliente.flow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoSeleccionado
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenRequest
import com.cuidadoeterno.app.modules.servicio.data.model.SolicitudDraft
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolicitudFlowViewModel(
    private val repository: ServicioRepository
) : ViewModel() {

    private val _draft = MutableStateFlow(SolicitudDraft())
    val draft: StateFlow<SolicitudDraft> = _draft.asStateFlow()

    // Paso 1: Guardar el servicio elegido
    fun setTipoServicio(id: Int, nombre: String, precioBase: Double) {
        _draft.update { actual ->
            actual.copy(
                idTipoSolicitud = id,
                nombreServicio = nombre,
                precioBase = precioBase
            ).apply { actualizarTotales() }
        }
    }

    // Paso 2: Guardar los datos del formulario de la sepultura
    fun setDatosEspacio(
        nombreF: String, apellidoF: String,
        idEspacio: Int, sector: String, numSepultura: String
    ) {
        _draft.update { actual ->
            actual.copy(
                nombreFallecido = nombreF,
                apellidoFallecido = apellidoF,
                idEspacio = idEspacio,
                sector = sector,
                numeroSepultura = numSepultura
            )
        }
    }

    fun calcularTotal(): Double {
        _draft.value.actualizarTotales()
        return _draft.value.montoTotal
    }

    fun confirmarOrdenTrasPagoExitoso(
        idCliente: Int,
        onExito: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val draft = _draft.value

            // 1. Construimos la petición EXACTAMENTE como la pide tu data class
            val request = OrdenRequest(
                idCliente = idCliente,
                idTipoSolicitud = draft.idTipoSolicitud ?: 0, // Usamos ?: 0 para evitar nulos
                fechaProgramada = "2024-12-31T10:00:00",      // TODO: Reemplazar por la fecha real elegida si la hay
                montoTotalServicio = draft.montoTotal.toBigDecimal(), // Convertimos de Double a BigDecimal
                observaciones = "Solicitud desde App Cliente",

                // Datos del Espacio
                idCementerio = 1, // TODO: Si tienes este id en el draft, ponlo aquí (ej: draft.idCementerio ?: 1)
                idTipoEspacio = 1, // TODO: Igual que arriba
                sectorPabellon = draft.sector ?: "S/N",
                numeroSepultura = draft.numeroSepultura ?: "S/N",
                pisoNivel = null,
                pasillo = null,
                materialPrincipal = null,

                // Datos del Fallecido
                nombres = draft.nombreFallecido ?: "Desconocido",
                apellidos = draft.apellidoFallecido ?: "",
                fechaNacimiento = null,
                fechaDefuncion = null,
                epitafio = null,

                // Insumos
                insumos = null // TODO: Aquí debes mapear draft.productosAdicionales a InsumoRequest si es necesario
            )

            // 2. Evaluamos la respuesta de forma clara (sin anidar lets)
            when (val result = repository.crearOrden(request)) {
                is NetworkResult.Success -> {
                    // result.data YA ES el Int (el ID de la orden) gracias a tu excelente Repository
                    val idNuevo = result.data

                    if (idNuevo != null) {
                        onExito(idNuevo)
                        _draft.value = SolicitudDraft() // Limpiamos la memoria
                    } else {
                        onError("El servidor guardó la orden, pero no devolvió el ID.")
                    }
                }
                is NetworkResult.Error -> {
                    onError(result.message ?: "Error al procesar la orden en el servidor")
                }
                is NetworkResult.Loading -> { }
            }
        }

    }

    // =======================================================================
    // ── GESTIÓN DEL CATÁLOGO DE PRODUCTOS (CARRITO) ────────────────────────
    // =======================================================================

    /**
     * Agrega un nuevo producto o incrementa su cantidad si ya está en la lista.
     */
    fun agregarInsumo(nuevoInsumo: InsumoSeleccionado) {
        val currentDraft = _draft.value
        val listaActual = currentDraft.productosAdicionales.toMutableList()

        val index = listaActual.indexOfFirst { it.idProducto == nuevoInsumo.idProducto }

        if (index != -1) {
            // El producto ya está en el carrito, incrementamos su cantidad
            val itemExistente = listaActual[index]
            listaActual[index] = itemExistente.copy(
                cantidad = itemExistente.cantidad + 1,
                // Sumamos el precio unitario (que viene en nuevoInsumo.montoTotal) al total del item
                montoTotal = itemExistente.montoTotal + nuevoInsumo.montoTotal
            )
        } else {
            // Es un producto nuevo, lo agregamos a la lista
            listaActual.add(nuevoInsumo)
        }

        // Recalculamos el total a pagar por los insumos
        val nuevoMontoTotal = listaActual.sumOf { it.montoTotal }

        _draft.value = currentDraft.copy(
            productosAdicionales = listaActual,
            montoInsumos = nuevoMontoTotal
        )
    }

    /**
     * Disminuye la cantidad de un producto. Si llega a 0, lo elimina del carrito.
     */
    fun removerInsumo(idProducto: Int, precioUnitario: Double) {
        val currentDraft = _draft.value
        val listaActual = currentDraft.productosAdicionales.toMutableList()

        val index = listaActual.indexOfFirst { it.idProducto == idProducto }

        if (index != -1) {
            val itemExistente = listaActual[index]

            if (itemExistente.cantidad > 1) {
                // Restamos 1 a la cantidad y descontamos el precio unitario
                listaActual[index] = itemExistente.copy(
                    cantidad = itemExistente.cantidad - 1,
                    montoTotal = itemExistente.montoTotal - precioUnitario
                )
            } else {
                // Si solo quedaba 1, eliminamos el producto de la lista
                listaActual.removeAt(index)
            }

            // Recalculamos el total
            val nuevoMontoTotal = listaActual.sumOf { it.montoTotal }

            _draft.value = currentDraft.copy(
                productosAdicionales = listaActual,
                montoInsumos = nuevoMontoTotal
            )
        }
    }

    /**
     * Limpia completamente la selección de productos.
     * Útil si el cliente retrocede y decide presionar "No".
     */
    fun limpiarInsumos() {
        _draft.value = _draft.value.copy(
            productosAdicionales = emptyList(),
            montoInsumos = 0.0
        )
    }
}