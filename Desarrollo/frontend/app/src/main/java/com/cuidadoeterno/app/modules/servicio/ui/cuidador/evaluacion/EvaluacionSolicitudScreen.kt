package com.cuidadoeterno.app.modules.servicio.ui.cuidador.evaluacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionSolicitudScreen(
    viewModel: EvaluacionSolicitudViewModel,
    onBack: () -> Unit,
    onIrAEjecucion: (idOrden: Int) -> Unit // Navega a la pantalla de trabajo tras aceptar
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orden = uiState.ordenDetalle

    // Si acepta con éxito, lo mandamos a trabajar automáticamente
    LaunchedEffect(uiState.aceptacionExitosa) {
        if (uiState.aceptacionExitosa && orden?.idOrden != null) {
            onIrAEjecucion(orden.idOrden)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Oferta") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) } }
            )
        }
    ) { paddingValues ->
        if (orden == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Cabecera (El Monto a Ganar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ganancia Estimada", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("$${orden.montoTotal.toInt()}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Resumen del Trabajo
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Servicio a realizar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(orden.nombreServicio, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(orden.ubicacionEspacio ?: "Ubicación específica no detallada")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Advertencias / Tips (Basado en tu documento)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Requerimiento EPP: Este servicio exige uso obligatorio de guantes y productos no abrasivos.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Errores
            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }

            // 4. Botón de Aceptar (Swipe o Botón Grande)
            Button(
                onClick = { viewModel.aceptarTrabajo() },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp),
                enabled = !uiState.isAceptando,
                shape = MaterialTheme.shapes.large
            ) {
                if (uiState.isAceptando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("ACEPTAR TRABAJO", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}