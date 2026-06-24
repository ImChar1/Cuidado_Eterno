package com.cuidadoeterno.app.modules.finanzas.ui.pago

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmacionPagoScreen(
    esExitoso: Boolean,
    numeroOrden: String,
    codigoAutorizacion: String,
    onVerMisSolicitudes: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (esExitoso) {
            // Un icono de éxito en verde
            Surface(shape = MaterialTheme.shapes.extraLarge, color = Color(0xFF4CAF50)) {
                Text("✓", modifier = Modifier.padding(24.dp), color = Color.White, style = MaterialTheme.typography.displayMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("¡Pago Exitoso!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Tu solicitud está confirmada y en cola.", style = MaterialTheme.typography.bodyLarge)

            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Orden N°: $numeroOrden")
                    Text("Código de Autorización: $codigoAutorizacion")
                }
            }

            Button(onClick = onVerMisSolicitudes, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Ver mis solicitudes")
            }

        } else {
            // Pantalla de error
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.error) {
                Text("✕", modifier = Modifier.padding(24.dp), color = Color.White, style = MaterialTheme.typography.displayMedium)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Transacción Fallida", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("No se realizó ningún cargo a tu tarjeta.", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(onClick = onVerMisSolicitudes, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Volver al inicio")
            }
        }
    }
}