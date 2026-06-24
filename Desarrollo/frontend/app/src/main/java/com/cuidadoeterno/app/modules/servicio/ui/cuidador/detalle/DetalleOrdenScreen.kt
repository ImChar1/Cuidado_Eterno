package com.cuidadoeterno.app.modules.servicio.ui.cuidador.detalle
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOrdenCuidadorScreen(
    viewModel: DetalleOrdenCuidadorViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error, uiState.mensajeExito) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        // CORRECCIÓN 1: Parámetro correcto para el Scaffold en Material 3
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Orden en Ejecución") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            uiState.orden?.let { orden ->
                // CORRECCIÓN 2: Extracción segura del ID para evitar el error de tipo Int?
                val idOrdenNonNull = orden.idOrden ?: 0

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = orden.nombreServicio,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ubicación: ${orden.ubicacionEspacio ?: "No asignada"}")
                            Text("Monto Ganancia: $${orden.montoTotal}")
                            Text("Estado General: ${orden.estadoOrden.uppercase()}")
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            // CORRECCIÓN 3: Uso de padding estándar de Compose
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Fase Actual de la Transmisión:", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = orden.subEstadoOrden ?: "ASIGNADO",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Actualizar mi estado para el cliente:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    // Control Dinámico usando la variable segura no-nula
                    when (orden.subEstadoOrden) {
                        "ASIGNADO", null -> {
                            Button(
                                onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "EN_CAMINO") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Marcar: Saliendo en Camino")
                            }
                        }
                        "EN_CAMINO" -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "COMPRANDO_INSUMOS") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Marcar: Comprando Insumos/Flores")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "EN_SITIO") },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Omitir insumos e ir directo al Sitio")
                                }
                            }
                        }
                        "COMPRANDO_INSUMOS" -> {
                            Button(
                                onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "EN_SITIO") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Marcar: Llegué al Cementerio/Sitio")
                            }
                        }
                        "EN_SITIO" -> {
                            Button(
                                onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "TRABAJANDO") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Marcar: Iniciando Mantenimiento")
                            }
                        }
                        "TRABAJANDO" -> {
                            Button(
                                onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "EVIDENCIA_SUBIDA") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Siguiente: Tomar y Subir Evidencia")
                            }
                        }
                        "EVIDENCIA_SUBIDA" -> {
                            Button(
                                onClick = { viewModel.modificarSubEstado(idOrdenNonNull, "TERMINADO") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Text("Concluir Servicio (Solicitar Cierre)")
                            }
                        }
                        "TERMINADO" -> {
                            Text(
                                text = "¡Excelente trabajo! Esperando confirmación o calificación del cliente.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}