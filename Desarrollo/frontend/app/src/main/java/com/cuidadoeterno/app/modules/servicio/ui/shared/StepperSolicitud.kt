package com.cuidadoeterno.app.modules.servicio.ui.shared
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StepperSolicitud(
    pasoActual: Int,
    modifier: Modifier = Modifier
) {
    // Definimos los 4 pasos exactos de tu flujo actual
    val pasos = listOf("Servicio", "Sepultura", "Insumos", "Resumen")
    val totalPasos = pasos.size

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FILA 1: Círculos y líneas conectoras
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..totalPasos) {
                val isCompletado = i < pasoActual
                val isActual = i == pasoActual

                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompletado || isActual) MaterialTheme.colorScheme.primary
                            else Color.LightGray
                        )
                )

                if (i < totalPasos) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (isCompletado) MaterialTheme.colorScheme.primary else Color.LightGray)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // FILA 2: Textos auto-alineados con los círculos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            for (i in 1..totalPasos) {
                val isActual = i == pasoActual

                Box(
                    // Un ancho fijo pequeño para que el texto quede centrado bajo el círculo
                    modifier = Modifier.width(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pasos[i - 1],
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = if (isActual || i < pasoActual) MaterialTheme.colorScheme.primary else Color.Gray,
                        fontWeight = if (isActual) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }

                if (i < totalPasos) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}