package com.cuidadoeterno.app.modules.admin.ui.cuidador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.admin.data.model.CuidadorAdminResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionCuidadoresScreen(
    viewModel: GestionCuidadoresViewModel,
    onBack: () -> Unit,
    onIrASoporteTecnicoCreacion: () -> Unit // Botón de emergencia técnica
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var tabSeleccionada by remember { mutableIntStateOf(0) }
    val categorias = listOf("PENDIENTES", "VALIDADOS", "TODOS")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Cuidadores") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onIrASoporteTecnicoCreacion,
                icon = { Text("+") },
                text = { Text("Soporte: Crear Cuenta") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Pestañas de Filtrado de Estado
            TabRow(selectedTabIndex = tabSeleccionada) {
                categorias.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val listaFiltrada = when (tabSeleccionada) {
                    0 -> uiState.listaCuidadores.filter { it.estadoValidacion == "PENDIENTE" }
                    1 -> uiState.listaCuidadores.filter { it.estadoValidacion == "VALIDADO" }
                    else -> uiState.listaCuidadores
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaFiltrada) { cuidador ->
                        TarjetaCuidadorAdmin(
                            cuidador = cuidador,
                            onApropar = { viewModel.procesarValidacion(cuidador.idCuidador, true) },
                            onRechazar = { viewModel.procesarValidacion(cuidador.idCuidador, false) },
                            onEliminar = { viewModel.eliminarCuidador(cuidador.idCuidador) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaCuidadorAdmin(
    cuidador: CuidadorAdminResponse,
    onApropar: () -> Unit,
    onRechazar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = cuidador.nombre, style = MaterialTheme.typography.titleMedium)
                Badge { Text(cuidador.estadoValidacion) }
            }
            Text("RUT: ${cuidador.rut}", style = MaterialTheme.typography.bodySmall)
            Text("Email: ${cuidador.correo}", style = MaterialTheme.typography.bodySmall)

            // Simulación de revisión documental (requisito de tesis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = { /* Abrir PDF externo o visor de imagen */ }, label = { Text("Ver Antecedentes 📄") })
                SuggestionChip(onClick = { /* Abrir visor */ }, label = { Text("Ver Certificado 🎓") })
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Acciones Operativas según el estado
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                if (cuidador.estadoValidacion == "PENDIENTE") {
                    TextButton(onClick = onRechazar, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text("Rechazar")
                    }
                    Button(onClick = onApropar) {
                        Text("Validar Cuidador")
                    }
                } else {
                    IconButton(onClick = onEliminar) {
                        Text("🗑️", color = MaterialTheme.colorScheme.error) // Icono simple o texto para eliminar
                    }
                }
            }
        }
    }
}