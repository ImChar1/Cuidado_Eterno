package com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
fun ServiciosScreen(
    viewModel: ServiciosViewModel,
    flowViewModel: SolicitudFlowViewModel, // El compartido
    onBack: () -> Unit,
    onSiguiente: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarModalInfo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servicios", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Stepper en Paso 1
            StepperSolicitud(pasoActual = 1)

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                // 🛑 AQUÍ ESTÁ EL GRITO
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🚨 Ocurrió un error:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Text("${uiState.error}", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.cargarTiposDeServicio() }) {
                        Text("Reintentar")
                    }
                }
            } else if (uiState.tiposServicio.isEmpty()) {
                // 📭 POR SI LA BASE DE DATOS ESTÁ VACÍA
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("No hay servicios disponibles en este momento.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(uiState.tiposServicio) { servicio ->
                        BotonServicioPill(
                            texto = servicio.nombre ?: "",
                            onClick = {
                                flowViewModel.setTipoServicio(
                                    id = servicio.id,
                                    nombre = servicio.nombre ?: "Servicio sin nombre",
                                    precioBase = servicio.precioBase
                                )
                                onSiguiente()
                            }
                        )
                    }
                }
            }

            // Botón inferior de orientación al cliente
            OutlinedButton(
                onClick = { mostrarModalInfo = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text("¿En qué consisten los servicios?", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // MODAL ORIENTADOR (Se despliega al tocar el botón inferior)
    if (mostrarModalInfo) {
        ModalBottomSheet(onDismissRequest = { mostrarModalInfo = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Detalle de nuestros servicios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.tiposServicio) { serv ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(serv.nombre ?: "Servicio sin nombre", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(serv.descripcion ?: "Sin descripcion", style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Tiempo estimado: ${serv.duracionEstimadaMin} min | Precio base: $${serv.precioBase.toInt()}",
                                    style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun BotonServicioPill(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = MaterialTheme.shapes.extraLarge, // Forma de píldora del wireframe
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(texto, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
            Text("→", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}