package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo


import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoSeleccionado
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel
import com.cuidadoeterno.app.modules.servicio.ui.shared.StepperSolicitud

// Modelo visual temporal para la vista
data class ProductoUi(val id: Int, val nombre: String, val precio: Double, val puesto: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductoScreen(
    flowViewModel: SolicitudFlowViewModel,
    onBack: () -> Unit,
    onSiguiente: () -> Unit
) {
    val draft by flowViewModel.draft.collectAsStateWithLifecycle()
    var textoBusqueda by remember { mutableStateOf("") }

    // Mock de catálogo (En el futuro vendrá de CatalogoProductoViewModel)
    val productosDisponibles = listOf(
        ProductoUi(101, "Arreglo Rosas Blancas", 12500.0, "Puesto Las Pérgolas #4"),
        ProductoUi(102, "Placa Recordatoria Acrílico", 18000.0, "Grabados El Carmen"),
        ProductoUi(103, "Ramo Crisantemos Amarillos", 8500.0, "Puesto Las Pérgolas #4"),
        ProductoUi(104, "Vela de cera 48 hrs (Par)", 4000.0, "Almacén Central")
    ).filter { it.nombre.contains(textoBusqueda, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos Opcionales", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) } }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Insumos:", style = MaterialTheme.typography.labelMedium)
                        Text("$${draft.montoInsumos.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(onClick = onSiguiente, modifier = Modifier.height(48.dp)) {
                        Text(if (draft.productosAdicionales.isEmpty()) "Omitir paso →" else "Continuar (${draft.productosAdicionales.size}) →")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(8.dp))
            StepperSolicitud(pasoActual = 3)
            Spacer(modifier = Modifier.height(16.dp))

            // Botón de escape rápido
            OutlinedButton(onClick = onSiguiente, modifier = Modifier.fillMaxWidth()) {
                Text("No deseo productos, continuar al resumen")
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = textoBusqueda, onValueChange = { textoBusqueda = it }, label = { Text("Buscar flores, placas, etc...") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(productosDisponibles) { prod ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(modifier = Modifier.fillMaxWidth().height(90.dp).background(Color.LightGray)) // Placeholder Imagen
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(prod.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 2)
                            Text(prod.puesto, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("$${prod.precio.toInt()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    flowViewModel.agregarInsumo(
                                        InsumoSeleccionado(idProducto = prod.id, idPuesto = 1, cantidad = 1, montoTotal = prod.precio)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp)
                            ) { Text("+ Agregar", style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }
        }
    }
}