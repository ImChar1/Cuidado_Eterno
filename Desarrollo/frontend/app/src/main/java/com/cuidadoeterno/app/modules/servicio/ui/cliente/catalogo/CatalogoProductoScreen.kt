package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.InsumoSeleccionado
import com.cuidadoeterno.app.modules.servicio.data.model.ProductoCatalogoResponse
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductoScreen(
    viewModel: CatalogoProductoViewModel,
    flowViewModel: SolicitudFlowViewModel,
    onBack: () -> Unit,
    onVerCarrito: () -> Unit // Te lleva al resumen final
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val draft by flowViewModel.draft.collectAsStateWithLifecycle()

    var textoBusqueda by remember { mutableStateOf("") }

    // Al iniciar la pantalla, cargamos el catálogo del cementerio que el usuario eligió en el paso anterior
    LaunchedEffect(draft.idEspacio) {
        val id = draft.idEspacio
        if (id != null && id != 0) {
            viewModel.cargarCatalogo(id)
        }
    }

    val productosFiltrados = uiState.productosDisponibles.filter {
        it.nombre.contains(textoBusqueda, ignoreCase = true)
    }

    // Calcula cantidad total de items para el botón flotante
    val totalItemsCarrito = draft.productosAdicionales.sumOf { it.cantidad }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) }
                }
            )
        },
        bottomBar = {
            if (totalItemsCarrito > 0) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = onVerCarrito,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ver Carrito", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("($totalItemsCarrito)")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Barra de Búsqueda
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else if (productosFiltrados.isEmpty()) {
                // Estado Vacío (Igual al wireframe)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("☹️", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No se encontraron resultados",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vuelve a intentarlo...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "Recomendado para ti",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Lista de productos
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio para el botón inferior
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoItemRow(
                            producto = producto,
                            cantidadEnCarrito = draft.productosAdicionales.find { it.idProducto == producto.idProducto }?.cantidad ?: 0,
                            onIncrementar = {
                                val insumo = InsumoSeleccionado(
                                    idProducto = producto.idProducto,
                                    idPuesto = producto.idPuesto,
                                    cantidad = 1,
                                    montoTotal = producto.precioVenta
                                )
                                flowViewModel.agregarInsumo(insumo)
                            },
                            onDecrementar = {
                                flowViewModel.removerInsumo(producto.idProducto, producto.precioVenta)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductoItemRow(
    producto: ProductoCatalogoResponse,
    cantidadEnCarrito: Int,
    onIncrementar: () -> Unit,
    onDecrementar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Imagen (Placeholder gris con bordes redondeados como en el wireframe)
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Textos
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$ ${producto.precioVenta.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Controles de cantidad (+ / -)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (cantidadEnCarrito > 0) {
                IconButton(onClick = onDecrementar) {
                    Text("—", style = MaterialTheme.typography.titleMedium)
                }
                Text(
                    text = "($cantidadEnCarrito)",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            IconButton(onClick = onIncrementar) {
                Text("+", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}