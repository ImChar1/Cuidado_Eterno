package com.cuidadoeterno.app.modules.servicio.ui.cuidador.disponibles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesDisponiblesScreen(
    viewModel: SolicitudesDisponiblesViewModel,
    onVolver: () -> Unit,
    onSolicitudSeleccionada: (OrdenResponse) -> Unit // Manda el objeto completo a Evaluacion
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var dropdownExpandido by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Solicitudes Disponibles") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 1. SELECTOR DE CEMENTERIO
            ExposedDropdownMenuBox(
                expanded = dropdownExpandido,
                onExpandedChange = { dropdownExpandido = !dropdownExpandido }
            ) {
                OutlinedTextField(
                    value = uiState.cementerioSeleccionado?.nombre ?: "Selecciona un cementerio...",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cementerio") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpandido) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpandido,
                    onDismissRequest = { dropdownExpandido = false }
                ) {
                    uiState.cementerios.forEach { cementerio ->
                        DropdownMenuItem(
                            text = { Text(cementerio.nombre) },
                            onClick = {
                                viewModel.seleccionarCementerio(cementerio)
                                dropdownExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. CONTENIDO INFERIOR (LISTA DE SOLICITUDES)
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
                uiState.cementerioSeleccionado == null -> {
                    Text("Selecciona un cementerio arriba para ver los trabajos disponibles en esa zona.")
                }
                uiState.solicitudes.isEmpty() -> {
                    Text("No hay solicitudes disponibles actualmente en este cementerio.")
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.solicitudes) { solicitud ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { onSolicitudSeleccionada(solicitud) },
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = solicitud.nombreServicio,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Ubicación: ${solicitud.ubicacionEspacio ?: "No especificada"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Pago: $${solicitud.montoTotal}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}