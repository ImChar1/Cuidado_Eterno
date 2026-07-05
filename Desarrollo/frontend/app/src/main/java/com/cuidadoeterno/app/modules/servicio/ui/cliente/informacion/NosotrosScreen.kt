package com.cuidadoeterno.app.modules.informacion.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NosotrosScreen(onVolver: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nosotros") },
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
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Cuidado Eterno",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dignificando el recuerdo, profesionalizando un oficio histórico.",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Este proyecto nace como Proyecto de Título desarrollado por estudiantes de Duoc UC. Nuestra mayor motivación es crear una herramienta tecnológica que genere un impacto real y positivo en la sociedad chilena.",
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sabemos que la mantención de las sepulturas en los cementerios públicos es una preocupación constante para miles de familias. Al mismo tiempo, los cuidadores realizan una labor fundamental y de gran esfuerzo físico que, muchas veces, se mantiene en la informalidad.",
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "A través de Cuidado Eterno, buscamos conectar directamente a las familias con los cuidadores mediante una plataforma segura. Nuestra meta es facilitar, transparentar y profesionalizar su noble labor, otorgándoles el reconocimiento y las oportunidades que merecen, mientras brindamos tranquilidad a quienes confían el cuidado de sus seres queridos en sus manos.",
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}