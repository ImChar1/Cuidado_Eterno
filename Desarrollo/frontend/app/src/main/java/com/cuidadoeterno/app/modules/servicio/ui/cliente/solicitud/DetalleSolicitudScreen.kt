package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
fun DetalleSolicitudScreen(
    idOrden: Int,
    viewModel: DetalleSolicitudViewModel,
    onVolver: () -> Unit,
    onCalificar: (idOrden: Int) -> Unit // Para cuando el servicio termine
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(idOrden) {
        viewModel.cargarDetalleOrden(idOrden)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguimiento de Solicitud") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            uiState.orden?.let { orden ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta de Resumen
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(orden.nombreServicio, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Ubicación: ${orden.ubicacionEspacio ?: "No especificada"}")
                            Text("Cuidador asignado: ${orden.nombreCuidador}", color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text("Progreso en Vivo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    // LÍNEA DE TIEMPO (TIMELINE)
                    val subEstado = orden.subEstadoOrden ?: "SIN_ASIGNAR"

                    TimelineItem(texto = "Solicitud Creada", isCompletado = true) // Siempre true si existe
                    TimelineItem(texto = "Cuidador Asignado", isCompletado = subEstado != "SIN_ASIGNAR")
                    TimelineItem(texto = "En Camino al Cementerio", isCompletado = subEstado in listOf("EN_CAMINO", "COMPRANDO_INSUMOS", "EN_SITIO", "TRABAJANDO", "EVIDENCIA_SUBIDA", "TERMINADO"))
                    TimelineItem(texto = "Comprando Insumos Extras", isCompletado = subEstado in listOf("COMPRANDO_INSUMOS", "EN_SITIO", "TRABAJANDO", "EVIDENCIA_SUBIDA", "TERMINADO"))
                    TimelineItem(texto = "En el Sepulcro", isCompletado = subEstado in listOf("EN_SITIO", "TRABAJANDO", "EVIDENCIA_SUBIDA", "TERMINADO"))
                    TimelineItem(texto = "Trabajo en Proceso", isCompletado = subEstado in listOf("TRABAJANDO", "EVIDENCIA_SUBIDA", "TERMINADO"))
                    TimelineItem(texto = "Evidencia Fotográfica Lista", isCompletado = subEstado in listOf("EVIDENCIA_SUBIDA", "TERMINADO"))

                    Spacer(modifier = Modifier.weight(1f))

                    // Botón para Recargar/Actualizar manualmente
                    OutlinedButton(
                        onClick = { viewModel.cargarDetalleOrden(idOrden) }, // Usar idOrden directamente
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Actualizar estado")
                    }

                    // Botón de Calificar
                    if (subEstado == "EVIDENCIA_SUBIDA" || subEstado == "TERMINADO" || orden.estadoOrden == "completada") {
                        Button(
                            onClick = { onCalificar(idOrden) }, // Usar idOrden directamente aquí también
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Ver Evidencias y Calificar Servicio")
                        }
                    }
                }
            }
        }
    }
}

// Sub-componente gráfico para la línea de tiempo
@Composable
fun TimelineItem(texto: String, isCompletado: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (isCompletado) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompletado) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isCompletado) MaterialTheme.colorScheme.onSurface else Color.Gray,
            fontWeight = if (isCompletado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}