package com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.material3.MenuAnchorType
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroCuidadorScreen(
    viewModel: RegistroCuidadorViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── Campos del Paso 1 — se mantienen en la Screen para que no se pierdan ──
    var rut by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apPaterno by remember { mutableStateOf("") }
    var apMaterno by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var nombreUsuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }
    var mostrarClave by remember { mutableStateOf(false) }

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // ── Campos del Paso 2 ───────────────────────────────────────────────────────
    var tipoDocumento by remember { mutableStateOf("") }
    var numeroRegistro by remember { mutableStateOf("") }
    var tipoDocumentoExpanded by remember { mutableStateOf(false) }

    val opcionesTipoDocumento = listOf(
        "cedula"                to "Cédula de identidad",
        "certificado_municipal" to "Certificado municipal",
        "registro_cementerio"   to "Registro del cementerio",
        "otro"                  to "Otro documento"
    )

    // Género
    var generoExpanded by remember { mutableStateOf(false) }
    val opcionesGenero = listOf(
        "M" to "Masculino",
        "F" to "Femenino",
        "O" to "Otro"
    )

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.registroExitoso) {
        if (uiState.registroExitoso) onRegistroExitoso()
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.pasoActual == 1)
                            "Registro cuidador — Paso 1/2"
                        else
                            "Registro cuidador — Paso 2/2"
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            if (uiState.pasoActual == 2) viewModel.volverAPaso1()
                            else onVolver()
                        }
                    ) {
                        Text("← Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // ── PASO 1 — Datos personales y disponibilidad ──────────────────────
            if (uiState.pasoActual == 1) {

                Text(
                    text = "Datos personales",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it },
                    label = { Text("RUT *") },
                    placeholder = { Text("12345678-9") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = apPaterno,
                    onValueChange = { apPaterno = it },
                    label = { Text("Apellido paterno *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = apMaterno,
                    onValueChange = { apMaterno = it },
                    label = { Text("Apellido materno") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono *") },
                    placeholder = { Text("+56912345678") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { }, // No permite escribir manualmente
                    readOnly = true,
                    label = { Text("Fecha de nacimiento *") },
                    placeholder = { Text("YYYY-MM-DD") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading,
                    trailingIcon = {
                        IconButton(onClick = { mostrarCalendario = true }) {
                            Text("📅")
                        }
                    }
                )

                if (mostrarCalendario) {
                    DatePickerDialog(
                        onDismissRequest = { mostrarCalendario = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    mostrarCalendario = false
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                        fechaNacimiento = formatter.format(Date(millis))
                                    }
                                }
                            ) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarCalendario = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                // Género
                ExposedDropdownMenuBox(
                    expanded = generoExpanded,
                    onExpandedChange = { generoExpanded = !generoExpanded }
                ) {
                    OutlinedTextField(
                        value = opcionesGenero.find { it.first == genero }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Género *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoExpanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        enabled = !uiState.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = generoExpanded,
                        onDismissRequest = { generoExpanded = false }
                    ) {
                        opcionesGenero.forEach { (valor, etiqueta) ->
                            DropdownMenuItem(
                                text = { Text(etiqueta) },
                                onClick = {
                                    genero = valor
                                    generoExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Datos de acceso",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = nombreUsuario,
                    onValueChange = { nombreUsuario = it },
                    label = { Text("Nombre de usuario *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = clave,
                    onValueChange = { clave = it },
                    label = { Text("Contraseña *") },
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
                    supportingText = { Text("Mínimo 8 caracteres") },
                    enabled = !uiState.isLoading
                )

                OutlinedTextField(
                    value = confirmarClave,
                    onValueChange = { confirmarClave = it },
                    label = { Text("Confirmar contraseña *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = confirmarClave.isNotBlank() && clave != confirmarClave,
                    supportingText = {
                        if (confirmarClave.isNotBlank() && clave != confirmarClave) {
                            Text(
                                "Las contraseñas no coinciden",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    enabled = !uiState.isLoading
                )


                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        viewModel.avanzarAPaso2(
                            rut, nombre, apPaterno, email, telefono,
                            fechaNacimiento, genero, nombreUsuario,
                            clave, confirmarClave
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !uiState.isLoading
                ) {
                    Text("Continuar →")
                }

                // ── PASO 2 — Documentación ──────────────────────────────────────────
            } else {

                Text(
                    text = "Documentación de certificación",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Adjunta un documento que acredite tu actividad como cuidador " +
                            "en cementerios públicos. El administrador lo revisará antes " +
                            "de activar tu cuenta.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tipo de documento
                ExposedDropdownMenuBox(
                    expanded = tipoDocumentoExpanded,
                    onExpandedChange = { tipoDocumentoExpanded = !tipoDocumentoExpanded }
                ) {
                    OutlinedTextField(
                        value = opcionesTipoDocumento
                            .find { it.first == tipoDocumento }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de documento *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = tipoDocumentoExpanded
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        enabled = !uiState.isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = tipoDocumentoExpanded,
                        onDismissRequest = { tipoDocumentoExpanded = false }
                    ) {
                        opcionesTipoDocumento.forEach { (valor, etiqueta) ->
                            DropdownMenuItem(
                                text = { Text(etiqueta) },
                                onClick = {
                                    tipoDocumento = valor
                                    tipoDocumentoExpanded = false
                                }
                            )
                        }
                    }
                }

                // Número de registro (opcional)
                OutlinedTextField(
                    value = numeroRegistro,
                    onValueChange = { numeroRegistro = it },
                    label = { Text("Número de registro (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Botón subir documento
                // Por ahora simula la URL — aquí irá la integración con S3
                OutlinedButton(
                    onClick = {
                        // Placeholder: cuando integres DocumentoPickerHelper
                        // este botón abre el selector de archivos del dispositivo
                        // y sube el archivo a S3 obteniendo la URL real.
                        // Por ahora simulamos con una URL de prueba:
                        viewModel.setUrlDocumento("https://s3.amazonaws.com/doc_prueba.pdf")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        if (uiState.urlDocumentoSubido != null)
                            "✓ Documento cargado"
                        else
                            "Seleccionar documento"
                    )
                }

                // Indicador visual de documento cargado
                if (uiState.urlDocumentoSubido != null) {
                    Text(
                        text = "Documento listo para enviar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botón registrar
                Button(
                    onClick = {
                        viewModel.registrar(
                            rut, nombre, apPaterno, apMaterno,
                            email, telefono, fechaNacimiento, genero,
                            nombreUsuario, clave,
                            tipoDocumento,
                            numeroRegistro
                        )
                    },
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
                        Text("Enviar registro")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}