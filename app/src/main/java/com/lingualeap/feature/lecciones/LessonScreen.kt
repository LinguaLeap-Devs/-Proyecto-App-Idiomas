package com.lingualeap.feature.lecciones

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.Lesson
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.feature.iaconsultas.AIViewModel
import java.util.*

// ── COLOR PALETTE GALAXY ─────────────────────
private val DeepNavy      = Color(0xFF0D1B3E)
private val RoyalBlue     = Color(0xFF1A3A7A)
private val AccentBlue    = Color(0xFF2D7DD2)
private val NeonCyan      = Color(0xFF00D4FF)
private val SunYellow     = Color(0xFFFBBF24)
private val CoralOrange   = Color(0xFFF97316)
private val EmeraldGreen  = Color(0xFF10B981)
private val CardBg        = Color(0xFF1E2D4F)
private val SurfaceBg     = Color(0xFF0A1628)
private val TextPrimary   = Color(0xFFEFF6FF)
private val TextSecondary = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lesson: Lesson,
    aiViewModel: AIViewModel,
    onLessonComplete: (Int) -> Unit,
    onQuestionFailed: (Int) -> Unit = {},
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var isAnswerChecked by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showLessonSummary by remember { mutableStateOf(false) }

    val aiExplanation by aiViewModel.explicacionError.collectAsStateWithLifecycle()
    val isAiLoading by aiViewModel.estaCargando.collectAsStateWithLifecycle()
    val ttsManager = remember { com.lingualeap.util.TtsManager.getInstance(context) }
    val currentQuestion = lesson.questions.getOrNull(currentQuestionIndex)

    LaunchedEffect(currentQuestionIndex) {
        currentQuestion?.let { ttsManager.speakSmart(it.text) }
        aiViewModel.limpiarExplicacion()
    }

    if (showLessonSummary) {
        LessonSummaryView(totalXp = lesson.xpReward, onCollectXp = { onLessonComplete(lesson.xpReward) })
        return
    }

    Scaffold(
        containerColor = SurfaceBg,
        topBar = {
            TopAppBar(
                title = {
                    val progressValue = (currentQuestionIndex + 1).toFloat() / lesson.questions.size.toFloat()
                    Box(Modifier.fillMaxWidth().padding(end = 16.dp)) {
                        Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                            Box(
                                Modifier
                                    .fillMaxWidth(progressValue)
                                    .fillMaxHeight()
                                    .background(Brush.horizontalGradient(listOf(AccentBlue, NeonCyan)), CircleShape)
                                    .shadow(8.dp, CircleShape, spotColor = NeonCyan)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, null, tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBg)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Fondo decorativo estelar
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(NeonCyan.copy(alpha = 0.05f), radius = 250.dp.toPx(), center = Offset(size.width, 0f))
                drawCircle(AccentBlue.copy(alpha = 0.03f), radius = 180.dp.toPx(), center = Offset(0f, size.height * 0.6f))
            }

            if (currentQuestion != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "PREGUNTA ${currentQuestionIndex + 1} DE ${lesson.questions.size}",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { ttsManager.speakSmart(currentQuestion.text) },
                                modifier = Modifier.background(NeonCyan.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = NeonCyan)
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = currentQuestion.text,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    currentQuestion.options.forEach { option ->
                        val isSelected = selectedOption == option
                        val optionBorder = when {
                            isAnswerChecked && option == currentQuestion.correctAnswer -> EmeraldGreen
                            isAnswerChecked && isSelected && !isCorrect -> Color(0xFFEF4444)
                            isSelected -> NeonCyan
                            else -> Color.White.copy(alpha = 0.1f)
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable(enabled = !isAnswerChecked) { selectedOption = option },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) Color.White.copy(alpha = 0.08f) else CardBg,
                            border = BorderStroke(2.dp, optionBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .border(2.dp, if (isSelected) optionBorder else Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) Box(Modifier.size(12.dp).background(optionBorder, CircleShape))
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = option,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(100.dp)) // Espacio para el feedback inferior
                }

                // --- FEEDBACK SECTION ---
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    AnimatedVisibility(
                        visible = isAnswerChecked,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (isCorrect) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                            border = BorderStroke(1.dp, if (isCorrect) EmeraldGreen.copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(if (isCorrect) EmeraldGreen else Color(0xFFEF4444), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(if (isCorrect) Icons.Rounded.Check else Icons.Rounded.Close, null, tint = Color.White)
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Text(
                                        text = if (isCorrect) "¡Misión Cumplida!" else "Anomalía detectada",
                                        color = if (isCorrect) EmeraldGreen else Color(0xFFEF4444),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp
                                    )
                                }

                                if (!isCorrect) {
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        text = "La respuesta correcta era:",
                                        color = TextSecondary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = currentQuestion.correctAnswer,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    
                                    // IA EXPLANATION
                                    Spacer(Modifier.height(16.dp))
                                    if (aiExplanation == null) {
                                        Button(
                                            onClick = { aiViewModel.explicarError(currentQuestion.text, selectedOption ?: "", currentQuestion.correctAnswer) },
                                            enabled = !isAiLoading,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            if (isAiLoading) CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NeonCyan)
                                            else {
                                                Icon(Icons.Rounded.AutoAwesome, null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                                Spacer(Modifier.width(8.dp))
                                                Text("Analizar con IA", color = TextPrimary)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(16.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = aiExplanation!!,
                                                modifier = Modifier.padding(16.dp),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextPrimary.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(24.dp))
                                LinguaButton(
                                    text = "CONTINUAR",
                                    onClick = {
                                        if (currentQuestionIndex < lesson.questions.size - 1) {
                                            currentQuestionIndex++
                                            selectedOption = null
                                            isAnswerChecked = false
                                        } else {
                                            showLessonSummary = true
                                        }
                                    },
                                    containerColor = if (isCorrect) EmeraldGreen else Color(0xFFEF4444),
                                    modifier = Modifier.fillMaxWidth().height(56.dp)
                                )
                            }
                        }
                    }

                    if (!isAnswerChecked) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SurfaceBg,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Box(Modifier.padding(24.dp)) {
                                LinguaButton(
                                    text = "COMPROBAR",
                                    onClick = {
                                        isCorrect = selectedOption == currentQuestion.correctAnswer
                                        isAnswerChecked = true
                                        if (!isCorrect) onQuestionFailed(currentQuestion.id)
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
}

@Composable
fun LessonSummaryView(totalXp: Int, onCollectXp: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                Modifier
                    .size(200.dp)
                    .background(Brush.radialGradient(listOf(SunYellow.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
            )
            Icon(
                imageVector = Icons.Rounded.EmojiEvents,
                contentDescription = null,
                tint = SunYellow,
                modifier = Modifier.size(120.dp).shadow(20.dp, CircleShape, spotColor = SunYellow)
            )
        }
        
        Spacer(Modifier.height(40.dp))
        
        Text(
            text = "¡EXPLORACIÓN COMPLETADA!",
            color = SunYellow,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Has conquistado esta lección",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(48.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = CardBg,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(28.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(56.dp).background(SunYellow.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Bolt, null, tint = SunYellow, modifier = Modifier.size(32.dp))
                }
                Spacer(Modifier.width(20.dp))
                Column {
                    Text("RECOMPENSA", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("+$totalXp XP", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        
        Spacer(Modifier.weight(1f))
        
        LinguaButton(
            text = "VOLVER A LA BASE",
            onClick = onCollectXp,
            containerColor = NeonCyan,
            contentColor = DeepNavy,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        )
    }
}
