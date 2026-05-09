package com.lingualeap.feature.iaconsultas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.feature.iaconsultas.AIViewModel

@Composable
fun PanelConsultasBrevesIA(viewModel: AIViewModel) {
    val respuesta by viewModel.respuestaIA.collectAsStateWithLifecycle()
    val cargando by viewModel.estaCargando.collectAsStateWithLifecycle()
    var consulta by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp)
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Text(
            "Consultas rápidas con IA",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
        Text(
            "Resuelve tus dudas al instante.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        if (cargando) {
            LinearProgressIndicator(modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape))
            Spacer(Modifier.height(16.dp))
        } else if (respuesta.isNotBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    respuesta,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = consulta,
                onValueChange = { consulta = it },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Ej: ¿Cómo se dice 'reunión'?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                shape = RoundedCornerShape(24.dp),
                maxLines = 2
            )
            Spacer(Modifier.width(8.dp))
            IconButton(
                onClick = { if (consulta.isNotBlank()) viewModel.enviarConsulta(consulta) },
                enabled = !cargando && consulta.isNotBlank(),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Enviar",
                    tint =
                    if (!cargando && consulta.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            }
        }
    }
}
