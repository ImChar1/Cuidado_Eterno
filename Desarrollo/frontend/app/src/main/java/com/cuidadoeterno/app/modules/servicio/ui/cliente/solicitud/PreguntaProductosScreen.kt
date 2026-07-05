package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cuidadoeterno.app.modules.servicio.ui.shared.StepperSolicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreguntaProductosScreen(
    onSi: () -> Unit,
    onNo: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Complementos", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StepperSolicitud(pasoActual = 3) // Siguiente paso en el stepper

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "¿Deseas añadir productos adicionales a tu solicitud?",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Puedes agregar flores, velas o placas conmemorativas ahora o continuar directamente al resumen.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Opción SI
            Button(
                onClick = onSi,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Sí, ver catálogo de productos")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Opción NO
            OutlinedButton(
                onClick = onNo,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("No, continuar al resumen")
            }
        }
    }
}