package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.modules.finanzas.data.model.WebpayInitRequest
import com.cuidadoeterno.app.modules.finanzas.data.repository.FinanzasRepository
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoRequest
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenRequest
import com.cuidadoeterno.app.modules.servicio.data.model.SolicitudDraft
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CheckoutUiState {
    object Idle : CheckoutUiState()
    object Loading : CheckoutUiState()
    data class Success(val idOrden: Long, val tokenWebpay: String, val urlTransbank: String) : CheckoutUiState()
    data class Error(val mensaje: String) : CheckoutUiState()
}

class ResumenSolicitudViewModel(
    private val servicioRepository: ServicioRepository,
    private val finanzasRepository: FinanzasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Idle)
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun confirmarOrden(draft: SolicitudDraft, idCliente: Int) {
        viewModelScope.launch {
            _uiState.value = CheckoutUiState.Loading

            try {
                // =================================================================
                // 1. CREAR LA ORDEN EN EL BACKEND PRIMERO
                // =================================================================
                val insumosParaBackend = if (draft.productosAdicionales.isNotEmpty()) {
                    draft.productosAdicionales.map { insumo ->
                        InsumoRequest(
                            idPuesto = insumo.idPuesto,
                            montoTotal = insumo.montoTotal.toBigDecimal()
                        )
                    }
                } else null

                val requestOrden = OrdenRequest(
                    idCliente = idCliente,
                    idTipoSolicitud = draft.idTipoSolicitud ?: 0,
                    fechaProgramada = "2026-12-31T10:00:00",
                    montoTotalServicio = draft.montoTotal.toBigDecimal(),
                    observaciones = "Solicitud generada desde la App Móvil",
                    idCementerio = draft.idCementerio ?: 0,
                    idTipoEspacio = draft.idTipoEspacio ?: 0,
                    sectorPabellon = draft.sectorPabellon.ifBlank { "S/N" },
                    numeroSepultura = draft.numeroSepultura.ifBlank { "S/N" },
                    pisoNivel = draft.pisoNivel,
                    pasillo = draft.pasillo,
                    materialPrincipal = null,
                    nombres = draft.nombreFallecido.ifBlank { "Desconocido" },
                    apellidos = draft.apellidoFallecido,
                    fechaNacimiento = null,
                    fechaDefuncion = null,
                    epitafio = null,
                    insumos = insumosParaBackend
                )

                when (val resultOrden = servicioRepository.crearOrden(requestOrden)) {
                    is NetworkResult.Success -> {
                        val idNuevaSolicitud = resultOrden.data

                        if (idNuevaSolicitud != null) {
                            // =================================================================
                            // 2. INICIAR WEBPAY USANDO EL ID DE LA ORDEN RECIÉN CREADA
                            // =================================================================
                            // returnUrl debe tener el path que nuestro WebView interceptará
                            val requestWebpay = WebpayInitRequest(
                                idSolicitud = idNuevaSolicitud,
                                returnUrl = "https://tu-backend.com/api/v1/finanzas/webpay/confirmar"
                            )

                            when (val resultWebpay = finanzasRepository.iniciarTransaccion(requestWebpay)) {
                                is NetworkResult.Success -> {
                                    val token = resultWebpay.data?.token ?: ""
                                    val url = resultWebpay.data?.urlWebpay ?: ""

                                    if (token.isNotBlank() && url.isNotBlank()) {
                                        _uiState.value = CheckoutUiState.Success(
                                            idOrden = idNuevaSolicitud.toLong(),
                                            tokenWebpay = token,
                                            urlTransbank = url
                                        )
                                    } else {
                                        _uiState.value = CheckoutUiState.Error("Servidor no devolvió Token Webpay")
                                    }
                                }
                                is NetworkResult.Error -> {
                                    _uiState.value = CheckoutUiState.Error(resultWebpay.message ?: "Error al iniciar Webpay")
                                }
                                else -> {}
                            }
                        } else {
                            _uiState.value = CheckoutUiState.Error("La orden se creó pero el ID es nulo")
                        }
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = CheckoutUiState.Error(resultOrden.message ?: "Error al crear la orden")
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.value = CheckoutUiState.Error("Fallo de red: ${e.message}")
            }
        }
    }
}