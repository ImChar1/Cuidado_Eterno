package com.cuidadoeterno.app.modules.servicio.ui.cuidador.resumen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenExitoScreen(
    idCuidador: Int,
    idOrden: Int,
    viewModel: ResumenExitoViewModel,
    onVolverAlHome: () -> Unit // El botón de escape
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.cargarResumenOrden(idCuidador, idOrden)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Resumen de Pago") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.weight(1f))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Generando tu comprobante...")
                Spacer(modifier = Modifier.weight(1f))
                return@Column
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.weight(1f))
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                Button(onClick = onVolverAlHome, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Volver al Inicio")
                }
                Spacer(modifier = Modifier.weight(1f))
                return@Column
            }

            val orden = uiState.ordenFinalizada
            if (orden != null) {
                // ICONO DE ÉXITO GIGANTE
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Éxito",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Excelente Trabajo!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "La orden #${orden.idOrden} ha sido completada.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // TARJETA TIPO "TICKET/VOUCHER" DE GANANCIas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Ganancia Total Abonada",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$${orden.montoTotal.toInt()}", // El monto mágico
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "El dinero ya está disponible en tu Billetera Virtual.",
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onVolverAlHome,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Volver al Inicio")
                }
            }
        }
    }
}