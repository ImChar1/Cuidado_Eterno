package com.cuidadoeterno.app.modules.usuario.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: (rol: String) -> Unit,
    onIrARegistro: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Campos de texto locales — no van en el ViewModel porque son solo UI
    var nombreUsuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }

    // Navegar cuando el login es exitoso
    LaunchedEffect(uiState.loginExitoso) {
        if (uiState.loginExitoso && uiState.rol != null) {
            onLoginExitoso(uiState.rol!!)
        }
    }

    // Mostrar error como Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Cuidado Eterno",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inicia sesión para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo nombre de usuario
            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña con toggle de visibilidad
            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (mostrarClave)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    TextButton(onClick = { mostrarClave = !mostrarClave }) {
                        Text(if (mostrarClave) "Ocultar" else "Ver")
                    }
                },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de login con estado de carga
            Button(
                onClick = { viewModel.login(nombreUsuario, clave) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link a registro
            TextButton(onClick = onIrARegistro) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }
        }
    }
}