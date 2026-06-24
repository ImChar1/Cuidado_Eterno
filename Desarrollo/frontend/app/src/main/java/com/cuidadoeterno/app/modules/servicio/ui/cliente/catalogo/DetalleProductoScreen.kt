package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoSeleccionado
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleProductoScreen(
    idProducto: Int,
    viewModel: DetalleProductoViewModel,
    flowViewModel: SolicitudFlowViewModel, // El compartido para guardar el dato
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(idProducto) { viewModel.cargarProducto(idProducto) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Detalle de Producto") }, navigationIcon = { IconButton(onClick = onBack) { Text("←") } }) },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        flowViewModel.agregarInsumo(
                            InsumoSeleccionado(
                                idProducto = state.idProducto, idPuesto = 1,
                                cantidad = state.cantidadSeleccionada, montoTotal = state.montoTotal
                            )
                        )
                        onBack() // Regresa al catálogo tras agregar
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp)
                ) { Text("Agregar a solicitud por $${state.montoTotal.toInt()}") }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray)) // Foto
            Spacer(modifier = Modifier.height(16.dp))
            Text(state.nombre, style = MaterialTheme.typography.headlineSmall)
            Text(state.descripcion, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(24.dp))

            // Selector de cantidad
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Cantidad:", modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.disminuirCantidad() }) { Text("-", style = MaterialTheme.typography.titleLarge) }
                Text("${state.cantidadSeleccionada}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 16.dp))
                IconButton(onClick = { viewModel.aumentarCantidad() }) { Text("+", style = MaterialTheme.typography.titleLarge) }
            }
        }
    }
}