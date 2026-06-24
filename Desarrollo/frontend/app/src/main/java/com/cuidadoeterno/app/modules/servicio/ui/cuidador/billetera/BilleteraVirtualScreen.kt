package com.cuidadoeterno.app.modules.servicio.ui.cuidador.billetera

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BilleteraScreen(
    viewModel: BilleteraViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cargar los datos automáticamente al abrir la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarBilletera()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Billetera Virtual") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
                return@Scaffold
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                // 1. Tarjeta Principal de Saldo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Saldo Disponible",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "$${uiState.saldoDisponible.toInt()}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Historial de Abonos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 2. Lista de trabajos pagados
                if (uiState.pagosCompletados.isEmpty()) {
                    Text(
                        text = "Aún no tienes pagos completados. ¡Completa tu primer servicio para ver tus ganancias aquí!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.pagosCompletados) { orden ->
                            ItemAbono(orden)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemAbono(orden: OrdenResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = orden.nombreServicio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Orden #${orden.idOrden}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+$${orden.montoTotal.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF4CAF50) // Verde positivo
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = "Pagado", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            }
        }
    }
}