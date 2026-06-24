package com.cuidadoeterno.app.modules.admin.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAdminScreen(
    onIrAGestionCuidadores: () -> Unit,
    onIrASolicitudes: () -> Unit,
    onIrAEspacios: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control Admin") },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Text("Salir", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido, Administrador",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Módulos de Gestión del Sistema",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Grid centralizado de botones / tarjetas de acceso rápido
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    TarjetaModulo(
                        titulo = "Cuidadores",
                        descripcion = "Validar y dar soporte a personal",
                        onClick = onIrAGestionCuidadores,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
                item {
                    TarjetaModulo(
                        titulo = "Solicitudes",
                        descripcion = "Ver historial global de órdenes",
                        onClick = onIrASolicitudes,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
                item {
                    TarjetaModulo(
                        titulo = "Espacios",
                        descripcion = "Monitorear tumbas registradas",
                        onClick = onIrAEspacios,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaModulo(
    titulo: String,
    descripcion: String,
    onClick: () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = descripcion, style = MaterialTheme.typography.bodySmall)
        }
    }
}