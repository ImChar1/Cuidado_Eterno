package com.cuidadoeterno.app.modules.servicio.ui.cliente.flow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoRequest
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
        nombreF: String,
        apellidoF: String,
        idCementerio: Int,
        idTipoEspacio: Int,
        sector: String,
        pisoNivel: String?,
        pasillo: String?,
        numSepultura: String
    ) {
        _draft.update { actual ->
            actual.copy(
                nombreFallecido = nombreF,
                apellidoFallecido = apellidoF,
                idCementerio = idCementerio,
                idTipoEspacio = idTipoEspacio,
                sectorPabellon = sector,
                pisoNivel = pisoNivel,
                pasillo = pasillo,
                numeroSepultura = numSepultura
            )
        }
    }

    fun calcularTotal(): Double {
        _draft.update { actual ->
            actual.copy().apply { actualizarTotales() }
        }
        return _draft.value.montoTotal
    }

    fun confirmarOrdenTrasPagoExitoso(
        idCliente: Int,
        onExito: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val draft = _draft.value

            // MAPEO DEL CARRITO: Transformamos los insumos seleccionados al formato que pide el backend
            val insumosParaBackend = if (draft.productosAdicionales.isNotEmpty()) {
                draft.productosAdicionales.map { insumo ->
                    InsumoRequest(
                        idPuesto = insumo.idPuesto,
                        montoTotal = insumo.montoTotal.toBigDecimal() // El backend exige BigDecimal
                    )
                }
            } else {
                null // Si no hay productos, enviamos null
            }

            // Construimos la petición EXACTAMENTE como la pide tu data class
            val request = OrdenRequest(
                idCliente = idCliente,
                idTipoSolicitud = draft.idTipoSolicitud ?: 0,
                // Nota: Si luego añades un DatePicker en la app, cambias este texto por la fecha elegida
                fechaProgramada = "2026-12-31T10:00:00",
                montoTotalServicio = draft.montoTotal.toBigDecimal(),
                observaciones = "Solicitud generada desde la App Móvil",

                // Datos del Espacio
                idCementerio = draft.idCementerio ?: 0,
                idTipoEspacio = draft.idTipoEspacio ?: 0,
                sectorPabellon = draft.sectorPabellon.ifBlank { "S/N" },
                numeroSepultura = draft.numeroSepultura.ifBlank { "S/N" },
                pisoNivel = draft.pisoNivel,
                pasillo = draft.pasillo,
                materialPrincipal = null,

                // Datos del Fallecido
                nombres = draft.nombreFallecido.ifBlank { "Desconocido" },
                apellidos = draft.apellidoFallecido,
                fechaNacimiento = null,
                fechaDefuncion = null,
                epitafio = null,

                // Insumos listos para el backend
                insumos = insumosParaBackend
            )

            // Evaluamos la respuesta
            when (val result = repository.crearOrden(request)) {
                is NetworkResult.Success -> {
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

    fun agregarInsumo(nuevoInsumo: InsumoSeleccionado) {
        val currentDraft = _draft.value
        val listaActual = currentDraft.productosAdicionales.toMutableList()
        val index = listaActual.indexOfFirst { it.idProducto == nuevoInsumo.idProducto }

        if (index != -1) {
            val itemExistente = listaActual[index]
            listaActual[index] = itemExistente.copy(
                cantidad = itemExistente.cantidad + 1,
                montoTotal = itemExistente.montoTotal + nuevoInsumo.montoTotal
            )
        } else {
            listaActual.add(nuevoInsumo)
        }

        // Corregido: Se cambia el nombre a nuevoMontoInsumos
        val nuevoMontoInsumos = listaActual.sumOf { it.montoTotal }
        _draft.value = currentDraft.copy(
            productosAdicionales = listaActual,
            montoInsumos = nuevoMontoInsumos,
            montoTotal = currentDraft.precioBase + nuevoMontoInsumos
        )
    }

    fun removerInsumo(idProducto: Int, precioUnitario: Double) {
        val currentDraft = _draft.value
        val listaActual = currentDraft.productosAdicionales.toMutableList()
        val index = listaActual.indexOfFirst { it.idProducto == idProducto }

        if (index != -1) {
            val itemExistente = listaActual[index]
            if (itemExistente.cantidad > 1) {
                listaActual[index] = itemExistente.copy(
                    cantidad = itemExistente.cantidad - 1,
                    montoTotal = itemExistente.montoTotal - precioUnitario
                )
            } else {
                listaActual.removeAt(index)
            }

            // Corregido: Se cambia el nombre a nuevoMontoInsumos
            val nuevoMontoInsumos = listaActual.sumOf { it.montoTotal }
            _draft.value = currentDraft.copy(
                productosAdicionales = listaActual,
                montoInsumos = nuevoMontoInsumos,
                montoTotal = currentDraft.precioBase + nuevoMontoInsumos
            )
        }
    }

    fun limpiarInsumos() {
        val currentDraft = _draft.value
        _draft.value = currentDraft.copy(
            productosAdicionales = emptyList(),
            montoInsumos = 0.0,
            montoTotal = currentDraft.precioBase
        )
    }
}