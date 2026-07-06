package com.cuidadoeterno.app.modules.servicio.ui.cliente.home
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.shared.ui.DrawerMenu
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeClienteScreen(
    viewModel: HomeClienteViewModel,
    onServiciosClick: () -> Unit,
    onNosotrosClick: () -> Unit,
    onFaqClick: () -> Unit,
    onVerSolicitudesActivas: () -> Unit, // <-- REEMPLAZA a onVerSeguimiento
    onVerHistorial: () -> Unit = {},
    onVerPerfil: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerMenu(
                    nombreUsuario = uiState.nombreUsuario,
                    rol = uiState.rol,
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
                    title = { Text("") },
                    actions = {
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
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¡Bienvenido!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                // =========================================================
                // ── BOTÓN: SOLICITUDES ACTIVAS (reemplaza la lista inline) ─
                // =========================================================
                MenuButton(texto = "Solicitudes Activas", onClick = onVerSolicitudesActivas)

                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(texto = "Servicios", onClick = onServiciosClick)
                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(texto = "Nosotros", onClick = onNosotrosClick)
                Spacer(modifier = Modifier.height(24.dp))

                MenuButton(texto = "Preguntas frecuentes", onClick = onFaqClick)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun MenuButton(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFAAAAAA),
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