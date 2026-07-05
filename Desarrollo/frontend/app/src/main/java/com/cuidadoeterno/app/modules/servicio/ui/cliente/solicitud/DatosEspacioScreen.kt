package com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cuidadoeterno.app.modules.servicio.data.model.ElementoDropdown
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel
import com.cuidadoeterno.app.modules.servicio.ui.shared.StepperSolicitud

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatosEspacioScreen(
    viewModel: DatosEspacioViewModel,
    flowViewModel: SolicitudFlowViewModel,
    onBack: () -> Unit,
    onSiguiente: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val draftActual by flowViewModel.draft.collectAsStateWithLifecycle()

    var nombresDifunto by remember { mutableStateOf(draftActual.nombreFallecido) }
    var apellidosDifunto by remember { mutableStateOf(draftActual.apellidoFallecido) }
    var cementerioSeleccionado by remember { mutableStateOf<ElementoDropdown?>(null) }
    var tipoEspacioSeleccionado by remember { mutableStateOf<ElementoDropdown?>(null) }

    var sectorPatio by remember { mutableStateOf(draftActual.sector) }
    var pabellonCalle by remember { mutableStateOf("") }
    var pisoNivel by remember { mutableStateOf("") }
    var pasillo by remember { mutableStateOf("") }
    var numEspacio by remember { mutableStateOf(draftActual.numeroSepultura) }

    var errorValidacion by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubicación", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = { IconButton(onClick = onBack) { Text("←", style = MaterialTheme.typography.titleLarge) } }
            )
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // ── MANEJO DE ESTADOS DE CARGA Y ERROR ────────────────────────────────
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                return@Scaffold
            }

            if (uiState.error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🚨 Ocurrió un error:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    Text("${uiState.error}", textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    Button(onClick = { viewModel.cargarDatosIniciales() }) {
                        Text("Reintentar")
                    }
                }
                return@Scaffold
            }

            // ── FORMULARIO PRINCIPAL ──────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                StepperSolicitud(pasoActual = 2)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Ingresa los datos de ubicación en terreno", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))



                // ── CAMPOS DE TEXTO NORMALES ──
                OutlinedTextField(value = nombresDifunto, onValueChange = { nombresDifunto = it }, label = { Text("Nombres del difunto *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = apellidosDifunto, onValueChange = { apellidosDifunto = it }, label = { Text("Apellidos del difunto *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Cementerio
                var expandidoCem by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandidoCem, onExpandedChange = { expandidoCem = it }) {
                    OutlinedTextField(
                        value = cementerioSeleccionado?.nombre ?: "Seleccione el cementerio *",
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCem) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandidoCem, onDismissRequest = { expandidoCem = false }) {
                        uiState.cementerios.forEach { c ->
                            DropdownMenuItem(text = { Text(c.nombre) }, onClick = { cementerioSeleccionado = c; expandidoCem = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Tipo Espacio
                var expandidoTipo by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = expandidoTipo, onExpandedChange = { expandidoTipo = it }) {
                    OutlinedTextField(
                        value = tipoEspacioSeleccionado?.nombre ?: "Seleccione el tipo de construcción *",
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoTipo) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expandidoTipo, onDismissRequest = { expandidoTipo = false }) {
                        uiState.tiposEspacio.forEach { t ->
                            DropdownMenuItem(text = { Text(t.nombre) }, onClick = { tipoEspacioSeleccionado = t; expandidoTipo = false })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = sectorPatio, onValueChange = { sectorPatio = it }, label = { Text("Patio principal / Sector *") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = pabellonCalle, onValueChange = { pabellonCalle = it }, label = { Text("Pabellón o Calle de la sepultura") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pisoNivel, onValueChange = { pisoNivel = it }, label = { Text("Piso/Nivel") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = pasillo, onValueChange = { pasillo = it }, label = { Text("Pasillo") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = numEspacio, onValueChange = { numEspacio = it }, label = { Text("Número de Sepultura/Nicho *") }, modifier = Modifier.fillMaxWidth())

                errorValidacion?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nombresDifunto.isBlank() || apellidosDifunto.isBlank() || cementerioSeleccionado == null || sectorPatio.isBlank() || numEspacio.isBlank()) {
                            errorValidacion = "Por favor completa todos los campos obligatorios (*)"
                        } else {
                            errorValidacion = null
                            flowViewModel.setDatosEspacio(
                                nombreF = nombresDifunto,
                                apellidoF = apellidosDifunto,
                                idEspacio = cementerioSeleccionado!!.id,
                                sector = "$sectorPatio (Pab: $pabellonCalle, Piso: $pisoNivel, Pas: $pasillo)",
                                numSepultura = numEspacio
                            )
                            onSiguiente()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Siguiente", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}