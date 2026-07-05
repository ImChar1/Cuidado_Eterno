package com.cuidadoeterno.app.modules.informacion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preguntas Frecuentes") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            FaqItem(
                pregunta = "¿Cómo funciona la solicitud de servicios?",
                respuesta = "Seleccionas el tipo de servicio que necesitas (Mantenimiento, Jardinería u Ornato), ingresas los datos de ubicación de la sepultura, y un cuidador certificado aceptará tu solicitud para realizar el trabajo en el cementerio."
            )

            FaqItem(
                pregunta = "¿Quiénes son los cuidadores?",
                respuesta = "Son trabajadores históricos de los cementerios públicos. Su identidad, experiencia y antecedentes son validados por nuestro equipo administrador antes de que puedan ofrecer sus servicios en la plataforma."
            )

            FaqItem(
                pregunta = "¿Cómo realizo el pago del servicio?",
                respuesta = "El pago se realiza de forma 100% segura a través de la aplicación. El dinero queda retenido y solo se libera al cuidador una vez que finaliza el servicio de manera exitosa."
            )

            FaqItem(
                pregunta = "¿Cómo sé que el trabajo realmente se hizo?",
                respuesta = "Podrás hacer un seguimiento en vivo del estado de tu solicitud. Una vez finalizado, el cuidador subirá evidencia fotográfica (un 'antes' y un 'después') para que puedas revisarla y calificar su trabajo."
            )

            FaqItem(
                pregunta = "¿Qué pasa si hay un problema con el servicio?",
                respuesta = "Si las fotografías no coinciden con lo acordado o hay algún inconveniente, puedes levantar un reporte al momento de calificar. Nuestro administrador revisará el caso para ofrecerte una solución oportuna."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun FaqItem(pregunta: String, respuesta: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = pregunta,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = respuesta,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}