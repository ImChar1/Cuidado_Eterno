package com.cuidadoeterno.app.modules.servicio.ui.home


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClienteScreen(
    viewModel: HomeClienteViewModel,
    onNuevaSolicitud: () -> Unit,
    onVerHistorial: () -> Unit,
    onVerPerfil: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                rol = "CLIENTE",
                onInicio = {},          // ya estamos en inicio
                onHistorial = onVerHistorial,
                onPerfil = onVerPerfil,
                onCerrarSesion = onCerrarSesion,
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
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
                            // Ícono de hamburguesa ≡
                            Text(
                                text = "≡",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onNuevaSolicitud,
                    text = { Text("Nueva solicitud") },
                    icon = { Text("+") }
                )
            }
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
                        text = "¡Bienvenido, ${uiState.nombreUsuario}!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "¿Qué necesitas hoy?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Servicios disponibles — accesos directos
                item {
                    Text(
                        text = "Servicios disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaServicio(
                            nombre = "Mantención",
                            descripcion = "Limpieza y cuidado general",
                            modifier = Modifier.weight(1f),
                            onClick = onNuevaSolicitud
                        )
                        TarjetaServicio(
                            nombre = "Jardinería",
                            descripcion = "Plantas y flores naturales",
                            modifier = Modifier.weight(1f),
                            onClick = onNuevaSolicitud
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TarjetaServicio(
                            nombre = "Ornato",
                            descripcion = "Decoración y conmemoración",
                            modifier = Modifier.weight(1f),
                            onClick = onNuevaSolicitud
                        )
                        // Espacio vacío para mantener la grilla
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Solicitudes activas
                item {
                    Text(
                        text = "Mis solicitudes activas",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (uiState.solicitudesActivas.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "No tienes solicitudes activas.\nUsa el botón + para crear una.",
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
                    items(uiState.solicitudesActivas) { solicitud ->
                        TarjetaSolicitudActiva(solicitud = solicitud)
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) } // espacio para el FAB
            }
        }
    }
}

// ── Componentes privados ────────────────────────────────────────────────────────

@Composable
private fun TarjetaServicio(
    nombre: String,
    descripcion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TarjetaSolicitudActiva(solicitud: OrdenResponse) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = solicitud.nombreServicio,
                    style = MaterialTheme.typography.titleSmall
                )
                EstadoBadge(estado = solicitud.estadoOrden)
            }
            Text(
                text = solicitud.ubicacionEspacio ?: "Sin ubicación",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (solicitud.nombreCuidador != "Sin asignar") {
                Text(
                    text = "Cuidador: ${solicitud.nombreCuidador}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: String) {
    val (color, texto) = when (estado) {
        "pendiente"  -> MaterialTheme.colorScheme.surfaceVariant to "Pendiente"
        "en_proceso" -> MaterialTheme.colorScheme.primaryContainer to "En proceso"
        "completada" -> MaterialTheme.colorScheme.secondaryContainer to "Completada"
        else         -> MaterialTheme.colorScheme.errorContainer to estado
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}