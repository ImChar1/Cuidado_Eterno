package com.cuidadoeterno.app.modules.servicio.ui.cliente.seguimiento

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalificacionScreen(
    idOrden: Int,
    viewModel: CalificacionViewModel,
    onVolver: () -> Unit,
    onCalificacionExitosa: () -> Unit // Se ejecuta al terminar de calificar
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados locales para el formulario de estrellas
    var puntuacion by remember { mutableIntStateOf(0) }
    var comentario by remember { mutableStateOf("") }

    // Reaccionar a cambios en el ViewModel (Errores o Éxito)
    LaunchedEffect(uiState.exito, uiState.error) {
        if (uiState.exito) {
            onCalificacionExitosa()
        }
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Calificar Servicio") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡El servicio ha concluido!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿Cómo evaluarías el trabajo realizado por el cuidador?",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Componente de 5 estrellas clickeables
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    val isSelected = i <= puntuacion
                    Icon(
                        imageVector = if (isSelected) Icons.Default.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Estrella $i",
                        tint = if (isSelected) Color(0xFFFFC107) else Color.Gray, // Amarillo o Gris
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { puntuacion = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de texto opcional para comentarios
            OutlinedTextField(
                value = comentario,
                onValueChange = { comentario = it },
                label = { Text("Comentarios adicionales (Opcional)") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón de Enviar (Deshabilitado si no hay estrellas marcadas)
            Button(
                onClick = { viewModel.enviarCalificacion(idOrden, puntuacion, comentario) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = puntuacion > 0 && !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Calificar y Cerrar Servicio", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}