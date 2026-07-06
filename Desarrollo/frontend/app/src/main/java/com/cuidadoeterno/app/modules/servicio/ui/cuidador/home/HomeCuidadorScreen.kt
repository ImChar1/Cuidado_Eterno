package com.cuidadoeterno.app.modules.servicio.ui.cuidador.home


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.shared.ui.DrawerMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCuidadorScreen(
    viewModel: HomeCuidadorViewModel,
    onVerOrdenActiva: (Int) -> Unit,
    onVerHistorial: () -> Unit,
    onVerPagos: () -> Unit,
    onVerPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dropdown de cementerios
    var cementerioExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerMenu(
                nombreUsuario = uiState.nombreUsuario,
                rol = "CUIDADOR",
                onInicio = {},
                onHistorial = onVerHistorial,
                onPagos = onVerPagos,
                onPerfil = onVerPerfil,
                onCerrarSesion = onCerrarSesion,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Cuidado Eterno") },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } }
                        ) {
                            Text(
                                text = "≡",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                return@Scaffold
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hola, ${uiState.nombreUsuario}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Orden en proceso — si tiene una activa la muestra primero
                uiState.ordenEnProceso?.let { orden ->
                    item {
                        Text(
                            text = "Tu orden activa",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TarjetaOrdenEnProceso(orden = orden,
                            onClick = { onVerOrdenActiva(orden.idOrden ?: 0) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Selector de cementerio
                item {
                    Text(
                        text = "Solicitudes disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Solo muestra el selector si no tiene orden en proceso
                    if (uiState.ordenEnProceso == null) {
                        ExposedDropdownMenuBox(
                            expanded = cementerioExpanded,
                            onExpandedChange = { cementerioExpanded = !cementerioExpanded }
                        ) {
                            OutlinedTextField(
                                value = uiState.cementerioSeleccionado?.nombreCementerio
                                    ?: "Selecciona un cementerio",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Cementerio") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = cementerioExpanded
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = cementerioExpanded,
                                onDismissRequest = { cementerioExpanded = false }
                            ) {
                                uiState.cementerios.forEach { cementerio ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(cementerio.nombreCementerio)
                                                Text(
                                                    text = cementerio.nombreComuna,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.seleccionarCementerio(cementerio)
                                            cementerioExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        // Si tiene orden activa no puede tomar otra
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Completa tu orden activa antes de tomar una nueva solicitud.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                }

                // Lista de solicitudes disponibles
                if (uiState.ordenEnProceso == null) {
                    if (uiState.cementerioSeleccionado == null) {
                        item {
                            Text(
                                text = "Selecciona un cementerio para ver las solicitudes disponibles.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else if (uiState.solicitudesDisponibles.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "No hay solicitudes disponibles en este cementerio.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp)
                                )
                            }
                        }
                    } else {
                        items(uiState.solicitudesDisponibles) { solicitud ->
                            TarjetaSolicitudDisponible(
                                solicitud = solicitud,
                                cargando = uiState.aceptandoSolicitud,
                                onAceptar = { viewModel.aceptarSolicitud(solicitud.idOrden!!) }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

// ── Componentes privados ────────────────────────────────────────────────────────

@Composable
private fun TarjetaOrdenEnProceso(orden: OrdenResponse, onClick: () -> Unit) {
    Card(
        onClick = onClick, // <-- Haz la tarjeta clickeable
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = orden.nombreServicio,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = orden.ubicacionEspacio ?: "Sin ubicación",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            orden.fechaProgramada?.let {
                Text(
                    text = "Programada: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun TarjetaSolicitudDisponible(
    solicitud: OrdenResponse,
    cargando: Boolean,
    onAceptar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = solicitud.nombreServicio,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = solicitud.ubicacionEspacio ?: "Sin ubicación",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            solicitud.fechaProgramada?.let {
                Text(
                    text = "Fecha: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$ ${solicitud.montoTotal}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onAceptar,
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Aceptar solicitud")
                }
            }
        }
    }
}