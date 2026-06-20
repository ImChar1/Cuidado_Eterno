package com.cuidadoeterno.app.modules.usuario.ui.perfil


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.usuario.data.model.PerfilResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onLogout: () -> Unit    // navega al login y limpia el backstack
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDialogoLogout by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Navegar al login cuando el logout es exitoso
    LaunchedEffect(uiState.logoutExitoso) {
        if (uiState.logoutExitoso) onLogout()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Diálogo de confirmación de logout
    if (mostrarDialogoLogout) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoLogout = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarDialogoLogout = false
                    viewModel.logout()
                }) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoLogout = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                actions = {
                    TextButton(onClick = { mostrarDialogoLogout = true }) {
                        Text(
                            text = "Salir",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // Estado de carga
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                return@Scaffold
            }

            // Sin datos
            if (uiState.perfil == null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No se pudo cargar el perfil",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.cargarPerfil() }) {
                        Text("Reintentar")
                    }
                }
                return@Scaffold
            }

            // Contenido del perfil
            val perfil = uiState.perfil!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Encabezado con nombre y rol
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${perfil.nombre} ${perfil.apPaterno}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = perfil.rol,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Sección: Datos personales (comunes a todos los roles)
                SeccionPerfil(titulo = "Datos personales") {
                    FilaPerfil(etiqueta = "Usuario", valor = perfil.nombreUsuario)
                    FilaPerfil(etiqueta = "Email", valor = perfil.email)
                    FilaPerfil(etiqueta = "Teléfono", valor = perfil.telefono)
                    perfil.apMaterno?.let {
                        FilaPerfil(
                            etiqueta = "Nombre completo",
                            valor = "${perfil.nombre} ${perfil.apPaterno} $it"
                        )
                    }
                    FilaPerfil(
                        etiqueta = "Estado cuenta",
                        valor = perfil.estadoCuenta.replaceFirstChar { it.uppercase() }
                    )
                }

                // Sección específica según rol
                when (perfil.rol) {
                    "CLIENTE" -> SeccionCliente(perfil)
                    "CUIDADOR" -> SeccionCuidador(perfil)
                    "ADMINISTRADOR" -> SeccionAdministrador(perfil)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── Sección exclusiva para CLIENTE ─────────────────────────────────────────────

@Composable
private fun SeccionCliente(perfil: PerfilResponse) {
    SeccionPerfil(titulo = "Información de cliente") {
        perfil.estadoCliente?.let {
            FilaPerfil(etiqueta = "Estado", valor = it.replaceFirstChar { c -> c.uppercase() })
        }
        perfil.prefNotificacion?.let {
            FilaPerfil(etiqueta = "Notificaciones", valor = it)
        }
    }
}

// ── Sección exclusiva para CUIDADOR ────────────────────────────────────────────

@Composable
private fun SeccionCuidador(perfil: PerfilResponse) {
    SeccionPerfil(titulo = "Información de cuidador") {

        // Estado de verificación con color según estado
        perfil.estadoVerificacion?.let { estado ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Verificación",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = when (estado) {
                        "verificado" -> MaterialTheme.colorScheme.primaryContainer
                        "rechazado"  -> MaterialTheme.colorScheme.errorContainer
                        else         -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = estado.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelMedium,
                        color = when (estado) {
                            "verificado" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "rechazado"  -> MaterialTheme.colorScheme.onErrorContainer
                            else         -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Mensaje informativo si está pendiente
            if (estado == "pendiente") {
                Text(
                    text = "Tu cuenta está siendo revisada por el administrador. " +
                            "Podrás aceptar solicitudes una vez verificado.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        perfil.estadoDisponibilidad?.let {
            FilaPerfil(
                etiqueta = "Disponibilidad",
                valor = it.replaceFirstChar { c -> c.uppercase() }
            )
        }

        perfil.calificacionPromedio?.let {
            FilaPerfil(
                etiqueta = "Calificación promedio",
                valor = "★ ${"%.1f".format(it)} / 5.0"
            )
        }
    }
}

// ── Sección exclusiva para ADMINISTRADOR ───────────────────────────────────────

@Composable
private fun SeccionAdministrador(perfil: PerfilResponse) {
    SeccionPerfil(titulo = "Información administrativa") {
        perfil.cargo?.let {
            FilaPerfil(etiqueta = "Cargo", valor = it)
        }
        perfil.nivelAcceso?.let {
            FilaPerfil(etiqueta = "Nivel de acceso", valor = it)
        }
    }
}

// ── Componentes reutilizables ───────────────────────────────────────────────────

@Composable
private fun SeccionPerfil(
    titulo: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            contenido()
        }
    }
}

@Composable
private fun FilaPerfil(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}