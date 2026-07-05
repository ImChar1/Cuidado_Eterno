package com.cuidadoeterno.app.modules.servicio.ui.cliente.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle // <-- Importante para leer el ViewModel
import com.cuidadoeterno.app.shared.ui.DrawerMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClienteScreen(
    viewModel: HomeClienteViewModel,
    onServiciosClick: () -> Unit,
    onNosotrosClick: () -> Unit,
    onFaqClick: () -> Unit,
    onVerHistorial: () -> Unit = {},
    onVerPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    // Leemos el estado del ViewModel para sacar el nombre del usuario
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Nota: Cuando descomentes tu DrawerMenu real, pásale el uiState.nombreUsuario
            ModalDrawerSheet {
                DrawerMenu(
                    nombreUsuario = uiState.nombreUsuario,
                    rol = uiState.rol, // <-- Asegúrate de pasar el rol del ViewModel
                    onInicio = { scope.launch { drawerState.close() } },
                    onHistorial = onVerHistorial,
                    onPerfil = onVerPerfil,
                    onCerrarSesion = onCerrarSesion,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("") }, // Sin título en la barra según el wireframe

                    actions = {
                        // Botón de menú hamburguesa al lado DERECHO
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp), // Margen lateral amplio como en el wireframe
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Título grande y centrado
                Text(
                    text = "¡Bienvenido!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(64.dp)) // Espacio grande antes de los botones

                // Botones principales (Grises, anchos y con bordes redondeados)
                MenuButton(texto = "Servicios", onClick = onServiciosClick)
                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(texto = "Nosotros", onClick = onNosotrosClick)
                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(texto = "Preguntas frecuentes", onClick = onFaqClick)
            }
        }
    }
}

// Componente reutilizable para los botones grises del wireframe
@Composable
private fun MenuButton(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), // Botones altos y cómodos
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFAAAAAA), // Color gris tipo wireframe
            contentColor = Color.White
        )
    ) {
        Text(
            text = texto,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}