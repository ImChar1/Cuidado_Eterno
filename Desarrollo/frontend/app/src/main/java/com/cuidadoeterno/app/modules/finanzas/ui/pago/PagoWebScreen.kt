package com.cuidadoeterno.app.modules.finanzas.ui.pago

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PagoWebpayScreen(
    urlWebpay: String,
    token: String,
    viewModel: PagoWebpayViewModel, // <- Agregamos el ViewModel
    onIrAConfirmacion: (esExitoso: Boolean, numOrden: String, codAuth: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observador para navegar cuando termine el flujo
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PagoWebpayUiState.Exitoso -> {
                onIrAConfirmacion(true, "ORD-${state.data.codigoAutorizacion}", state.data.codigoAutorizacion)
            }
            is PagoWebpayUiState.Fallido -> {
                onIrAConfirmacion(false, "", "") // Falló
            }
            else -> Unit // Sigue en WebView o Confirmando
        }
    }

    // Dibujamos la UI dependiendo del estado
    when (uiState) {
        is PagoWebpayUiState.CargandoWebView -> {
            // Aquí va tu código del AndroidView con el WebView (el que hicimos antes),
            // pero en lugar de llamar a onPagoTerminado, llamamos a viewModel.confirmarPago(tokenWs)
        }
        is PagoWebpayUiState.Confirmando -> {
            // Un overlay de carga mientras esperamos al backend
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Validando tu pago seguro con Transbank...")
            }
        }
        else -> { /* El LaunchedEffect se encarga de navegar fuera de aquí */ }
    }
}