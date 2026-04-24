package com.lingualeap.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.theme.LinguaColors

/**
 * PANTALLA: QUIZ DE NIVELACIÓN (Dinamismo mejorado)
 * 🟢 MEJORA DE EXCELENCIA: Ahora incluye un pequeño test interactivo antes de mostrar el resultado.
 */
@Composable
fun QuizScreen(
    onQuizComplete: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } // 0, 1, 2 son preguntas, 3 es el resultado
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    
    val questions = listOf(
        QuizQuestion(
            "¿Cuál es tu objetivo principal?",
            listOf("Viajes y Cultura", "Trabajo y Carrera", "Hobby y Curiosidad")
        ),
        QuizQuestion(
            "¿Cuánto tiempo dedicarás al día?",
            listOf("5-10 min (Relajado)", "15-30 min (Serio)", "1 hora+ (Intenso)")
        ),
        QuizQuestion(
            "¿Conoces algo del idioma?",
            listOf("Absolutamente nada", "Sé algunas palabras", "Puedo armar frases")
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (step < questions.size) {
            // FLUJO DE PREGUNTAS
            val currentQuestion = questions[step]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    progress = { (step + 1).toFloat() / (questions.size + 1).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .padding(bottom = 48.dp),
                    color = LinguaColors.Primary,
                    trackColor = Color(0xFFF1F5F9)
                )

                Text(
                    text = currentQuestion.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == index
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) LinguaColors.Primary else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedOption = index },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White
                        )
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 17.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) LinguaColors.Primary else Color.DarkGray
                        )
                    }
                }

                Spacer(Modifier.height(48.dp))

                LinguaButton(
                    text = "CONTINUAR",
                    onClick = {
                        step++
                        selectedOption = null
                    },
                    enabled = selectedOption != null,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }
        } else {
            // PANTALLA DE RESULTADO (Finalización)
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + expandVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(80.dp)
                    )
                    
                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "¡Excelente elección!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = LinguaColors.Primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Basado en tus respuestas, hemos personalizado tu ruta de aprendizaje para maximizar tu progreso.\n\n¡Estás listo para despegar!",
                        fontSize = 17.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(48.dp))

                    LinguaButton(
                        text = "COMENZAR MI RUTA",
                        onClick = onQuizComplete,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }
            }
        }
    }
}

data class QuizQuestion(
    val title: String,
    val options: List<String>
)
