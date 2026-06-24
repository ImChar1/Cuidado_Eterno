package com.cuidadoeterno.app.modules.servicio.ui.cuidador.ejecucion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EjecucionOrdenScreen(
    viewModel: EjecucionOrdenViewModel,
    onVolverAlHome: () -> Unit // Se llama al finalizar el servicio completo
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Ejecución de Servicio") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Cabecera indicadora de estado
            Text(
                text = "Orden #${uiState.orden?.idOrden ?: "---"}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Procesando evidencia y subiendo al servidor...")
                return@Column
            }

            // MÁQUINA DE ESTADOS VISUAL
            when (uiState.pasoActual) {

                PasoEjecucion.COMPRA_INSUMOS -> {
                    PantallaCompraInsumos { fotoUri -> viewModel.registrarBoletaInsumos(fotoUri) }
                }

                PasoEjecucion.LLEGADA -> {
                    PantallaLlegada { viewModel.registrarLlegada() }
                }

                PasoEjecucion.FOTO_ANTES -> {
                    PantallaTomarFoto(
                        titulo = "Paso 1: Foto del Estado Inicial",
                        descripcion = "Toma una fotografía de la sepultura antes de comenzar los trabajos de mantenimiento para el registro del cliente.",
                        onFotoTomada = { uri -> viewModel.registrarFotoAntes(uri) }
                    )
                }

                PasoEjecucion.CHECKLIST_EPP -> {
                    PantallaChecklistEPP { viewModel.confirmarChecklist() }
                }

                PasoEjecucion.FOTO_DESPUES -> {
                    PantallaTomarFoto(
                        titulo = "Paso Final: Foto del Trabajo Terminado",
                        descripcion = "Toma la fotografía final. Esta imagen será enviada al cliente como comprobante de tu servicio.",
                        onFotoTomada = { uri -> viewModel.registrarFotoDespues(uri) }
                    )
                }

                PasoEjecucion.FINALIZADO -> {
                    PantallaFinalizado(onVolver = onVolverAlHome)
                }
            }
        }
    }
}

// --- SUB-COMPONENTES PARA CADA PASO ---

@Composable
fun PantallaCompraInsumos(onBoletaSubida: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Compra de Insumos Requerida", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Esta orden incluye productos adicionales. Dirígete a la pérgola y sube la foto de la boleta de compra para habilitar la orden.", textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onBoletaSubida("uri_foto_boleta_mock.jpg") },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Tomar foto a la Boleta")
        }
    }
}

@Composable
fun PantallaLlegada(onLlegadaMarcada: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Dirígete a la ubicación de la sepultura", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLlegadaMarcada,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Registrar Llegada a la Tumba", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun PantallaChecklistEPP(onConfirmar: () -> Unit) {
    var check1 by remember { mutableStateOf(false) }
    var check2 by remember { mutableStateOf(false) }

    Column {
        Text("Normativa de Seguridad y Patrimonio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = check1, onCheckedChange = { check1 = it })
            Text("Confirmo que estoy usando los Elementos de Protección Personal (EPP).")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = check2, onCheckedChange = { check2 = it })
            Text("Confirmo el uso de líquidos NO abrasivos para proteger la tumba.")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onConfirmar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = check1 && check2 // Solo habilita si marcó ambos
        ) {
            Text("Confirmar y Continuar")
        }
    }
}

@Composable
fun PantallaTomarFoto(titulo: String, descripcion: String, onFotoTomada: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(descripcion, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(24.dp))

        // Placeholder del marco de la cámara
        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.DarkGray)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onFotoTomada("mock_uri.jpg") },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Capturar Fotografía")
        }
    }
}

@Composable
fun PantallaFinalizado(onVolver: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("¡Trabajo Completado!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("La evidencia ha sido enviada al cliente. El pago se reflejará en tu cuenta en las próximas 48 horas.", textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Volver al Inicio")
        }
    }
}