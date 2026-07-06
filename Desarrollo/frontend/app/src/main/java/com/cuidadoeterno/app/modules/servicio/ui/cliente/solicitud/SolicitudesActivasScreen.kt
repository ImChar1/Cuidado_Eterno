package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesActivasScreen(
    viewModel: SolicitudesActivasViewModel,
    onVolver: () -> Unit,
    // Se dispara solo si hay una orden activa: salta directo al detalle
    // sin mostrar ninguna lista intermedia (regla: máx. 1 activa)
    onIrADetalle: (idOrden: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.ordenActiva) {
        uiState.ordenActiva?.let { orden ->
            onIrADetalle(orden.idOrden)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes Activas") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()

                uiState.error != null -> Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )

                uiState.ordenActiva == null -> Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = "No tienes solicitudes activas en este momento.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Hay orden activa: el LaunchedEffect de arriba ya está
                // navegando al detalle, así que solo mostramos un loader breve.
                else -> CircularProgressIndicator()
            }
        }
    }
}