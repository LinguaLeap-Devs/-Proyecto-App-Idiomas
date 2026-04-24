package com.lingualeap.ui.screens

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.data.model.Lesson
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.theme.LinguaColors
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lesson: Lesson,
    onLessonComplete: (Int) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val tts = remember {
        TextToSpeech(context) { _ -> }
    }

    val speakSmart = { text: String ->
        val segments = text.split(Regex("[\'\"“”]")).filter { it.isNotBlank() }
        
        segments.forEachIndexed { index, segment ->
            val lower = segment.lowercase()
            val isSpanish = lower.contains(Regex("[¿áéíóúñ]")) || 
                            lower.contains("como") || 
                            lower.contains("dice") || 
                            lower.contains("traduce") ||
                            lower.contains("hola")

            tts.language = if (isSpanish) Locale("es", "ES") else Locale.US
            
            val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts.speak(segment, queueMode, null, null)
        }
    }

    val currentQuestion = lesson.questions.getOrNull(currentQuestionIndex)

    LaunchedEffect(currentQuestionIndex) {
        currentQuestion?.let { speakSmart(it.text) }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val progressValue = (currentQuestionIndex + 1).toFloat() / lesson.questions.size.toFloat()
                    LinearProgressIndicator(
                        progress = { progressValue },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = LinguaColors.Primary,
                        trackColor = Color(0xFFF1F5F9)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cerrar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Escucha la pregunta:",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { speakSmart(currentQuestion.text) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                                contentDescription = "Repetir voz",
                                tint = LinguaColors.Primary
                            )
                        }
                    }
                    
                    Text(
                        text = currentQuestion.text,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 32.sp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    currentQuestion.options.forEach { option ->
                        val isSelected = selectedOption == option
                        val borderColor = when {
                            isAnswerChecked && option == currentQuestion.correctAnswer -> Color(0xFF4CAF50)
                            isAnswerChecked && isSelected && !isCorrect -> Color(0xFFF44336)
                            isSelected -> LinguaColors.Primary
                            else -> Color(0xFFE2E8F0)
                        }
                        val bgColor = when {
                            isAnswerChecked && option == currentQuestion.correctAnswer -> Color(0xFFE8F5E9)
                            isAnswerChecked && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                            isSelected -> Color(0xFFEFF6FF)
                            else -> Color.White
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .border(width = 2.5.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                                .clickable(enabled = !isAnswerChecked) { selectedOption = option },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor)
                        ) {
                            Text(
                                text = option,
                                modifier = Modifier.padding(20.dp),
                                fontSize = 18.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected || isAnswerChecked) Color.Black else Color.DarkGray
                            )
                        }
                    }
                }

                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    AnimatedVisibility(
                        visible = isAnswerChecked,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isCorrect) Color(0xFFD7FFB7) else Color(0xFFFFDFE0),
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isCorrect) Icons.Rounded.CheckCircle else Icons.Rounded.Error,
                                        contentDescription = null,
                                        tint = if (isCorrect) Color(0xFF4B9100) else Color(0xFFEA2B2B),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = if (isCorrect) "¡Muy bien hecho!" else "Sigue intentando...",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isCorrect) Color(0xFF4B9100) else Color(0xFFEA2B2B)
                                    )
                                }
                                if (!isCorrect) {
                                    Text(
                                        text = "La respuesta correcta era: ${currentQuestion.correctAnswer}",
                                        color = Color(0xFFEA2B2B),
                                        modifier = Modifier.padding(top = 8.dp, start = 44.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(24.dp))
                                LinguaButton(
                                    text = if (currentQuestionIndex < lesson.questions.size - 1) "CONTINUAR" else "FINALIZAR",
                                    onClick = {
                                        if (currentQuestionIndex < lesson.questions.size - 1) {
                                            currentQuestionIndex++
                                            selectedOption = null
                                            isAnswerChecked = false
                                        } else {
                                            onLessonComplete(lesson.xpReward)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    containerColor = if (isCorrect) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                                )
                            }
                        }
                    }

                    if (!isAnswerChecked) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                            LinguaButton(
                                text = "COMPROBAR",
                                onClick = {
                                    isCorrect = selectedOption == currentQuestion.correctAnswer
                                    isAnswerChecked = true
                                },
                                enabled = selectedOption != null,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
