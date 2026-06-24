package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel
import com.cuidadoeterno.app.modules.servicio.ui.shared.StepperSolicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenSolicitudScreen(
    viewModel: ResumenSolicitudViewModel,
    flowViewModel: SolicitudFlowViewModel,
    onBack: () -> Unit,
    onNavegarAWebpay: (token: String, url: String) -> Unit
) {
    val draft by flowViewModel.draft.collectAsStateWithLifecycle()
    val checkoutState by viewModel.uiState.collectAsStateWithLifecycle()

    // Escuchamos el éxito de la orden para gatillar la navegación a Transbank
    LaunchedEffect(checkoutState) {
        if (checkoutState is CheckoutUiState.Success) {
            val data = checkoutState as CheckoutUiState.Success
            onNavegarAWebpay(data.tokenWebpay, data.urlTransbank)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Solicitud", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            StepperSolicitud(pasoActual = 4)
            Spacer(modifier = Modifier.height(24.dp))

            Text("Verifica los datos antes de pagar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // 1. Tarjeta Servicio
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Servicio Contratado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(draft.nombreServicio ?: "Sin definir", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text("$${draft.precioBase.toInt()}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 2. Tarjeta Sepultura
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Beneficiario y Ubicación", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Q.E.P.D: ${draft.nombreFallecido} ${draft.apellidoFallecido}", fontWeight = FontWeight.Bold)
                    Text("Sector: ${draft.sector}", style = MaterialTheme.typography.bodyMedium)
                    Text("Sepultura N°: ${draft.numeroSepultura}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // 3. Tarjeta Insumos Adicionales
            if (draft.productosAdicionales.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Productos Adicionales", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        draft.productosAdicionales.forEach { insumo ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Producto ID #${insumo.idProducto} (x${insumo.cantidad})", style = MaterialTheme.typography.bodyMedium)
                                Text("$${insumo.montoTotal.toInt()}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(modifier = Modifier.height(16.dp))

            // TOTALES
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal Servicio:", style = MaterialTheme.typography.bodyMedium)
                Text("$${draft.precioBase.toInt()}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal Insumos:", style = MaterialTheme.typography.bodyMedium)
                Text("$${draft.montoInsumos.toInt()}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("TOTAL A PAGAR:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("$${draft.montoTotal.toInt()}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }

            if (checkoutState is CheckoutUiState.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text((checkoutState as CheckoutUiState.Error).mensaje, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Pago Transbank
            Button(
                onClick = { viewModel.confirmarOrden(draft) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = checkoutState !is CheckoutUiState.Loading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0004D)) // Color Carmesí/Corporativo de Webpay
            ) {
                if (checkoutState is CheckoutUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Generando orden segura...")
                } else {
                    Text("Proceder al pago con Webpay", style = MaterialTheme.typography.titleMedium, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}