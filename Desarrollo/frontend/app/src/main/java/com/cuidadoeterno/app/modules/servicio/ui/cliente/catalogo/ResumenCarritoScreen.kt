package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenCarritoScreen(
    flowViewModel: SolicitudFlowViewModel,
    onBack: () -> Unit,
    onContinuar: () -> Unit // Te lleva al resumen FINAL de toda la orden
) {
    val draft by flowViewModel.draft.collectAsStateWithLifecycle()
    val carrito = draft.productosAdicionales

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = { IconButton(onClick = onBack) { Text("←") } }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal Insumos:", style = MaterialTheme.typography.titleMedium)
                        Text("$${draft.montoInsumos.toInt()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onContinuar,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Confirmar y ver Resumen Final")
                    }
                }
            }
        }
    ) { padding ->
        if (carrito.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("El carrito está vacío")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
                items(carrito) { insumo ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Producto ID: ${insumo.idProducto}", fontWeight = FontWeight.Bold) // Aquí idealmente mostrarías el nombre, pero tu DTO InsumoSeleccionado solo guarda el ID.
                                Text("Precio Unitario: $${(insumo.montoTotal / insumo.cantidad).toInt()}")
                                Text("Total: $${insumo.montoTotal.toInt()}", color = MaterialTheme.colorScheme.primary)
                            }

                            // Botones para editar cantidad directo en el carrito
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { flowViewModel.removerInsumo(insumo.idProducto, insumo.montoTotal / insumo.cantidad) }) {
                                    Text("-", style = MaterialTheme.typography.titleLarge)
                                }
                                Text("${insumo.cantidad}", style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { flowViewModel.agregarInsumo(insumo.copy(cantidad = 1, montoTotal = insumo.montoTotal / insumo.cantidad)) }) {
                                    Text("+", style = MaterialTheme.typography.titleLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}