package com.lingualeap.feature.lecciones

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.components.LinguaTextField
import com.lingualeap.feature.iaconsultas.AIViewModel

// ── COLOR PALETTE GALAXY ─────────────────────
private val DeepNavy      = Color(0xFF0D1B3E)
private val RoyalBlue     = Color(0xFF1A3A7A)
private val AccentBlue    = Color(0xFF2D7DD2)
private val NeonCyan      = Color(0xFF00D4FF)
private val SunYellow     = Color(0xFFFBBF24)
private val EmeraldGreen  = Color(0xFF10B981)
private val CardBg        = Color(0xFF1E2D4F)
private val SurfaceBg     = Color(0xFF0A1628)
private val TextPrimary   = Color(0xFFEFF6FF)
private val TextSecondary = Color(0xFF94A3B8)

@Composable
fun QuizScreen(
    aiViewModel: AIViewModel,
    onQuizComplete: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) } 
    var userText by remember { mutableStateOf("") }
    
    val aiResponse by aiViewModel.respuestaIA.collectAsStateWithLifecycle()
    val aiLevel by aiViewModel.nivelDetectado.collectAsStateWithLifecycle()
    val isLoading by aiViewModel.estaCargando.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Fondo decorativo galáctico
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.07f), Color.Transparent)),
                radius = 300.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.2f)
            )
            drawCircle(
                brush = Brush.radialGradient(listOf(AccentBlue.copy(alpha = 0.05f), Color.Transparent)),
                radius = 250.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (step) {
                0 -> {
                    // BIENVENIDA
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            Modifier.size(120.dp).background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
                        )
                        Icon(Icons.Rounded.Psychology, null, tint = NeonCyan, modifier = Modifier.size(80.dp))
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Text(
                        "Nivelación Inteligente", 
                        fontSize = 28.sp, 
                        fontWeight = FontWeight.Black, 
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        "Nuestra IA analizará tu escritura para asignarte el material que mejor se adapte a tu nivel actual.",
                        textAlign = TextAlign.Center, 
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                    
                    Spacer(Modifier.height(48.dp))
                    
                    LinguaButton(
                        text = "EMPEZAR ANÁLISIS", 
                        onClick = { step = 1 }, 
                        containerColor = NeonCyan,
                        contentColor = DeepNavy,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }

                1 -> {
                    // ENTRADA DE TEXTO
                    Text(
                        "Escribe en Inglés", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        "Preséntate brevemente (nombre, gustos, metas...)", 
                        fontSize = 13.sp, 
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = userText,
                                onValueChange = { userText = it },
                                placeholder = { Text("Escribe aquí...", color = TextSecondary.copy(alpha = 0.5f)) },
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    cursorColor = NeonCyan
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, lineHeight = 24.sp)
                            )
                            
                            Text(
                                text = "${userText.length} caracteres",
                                color = if (userText.length > 10) EmeraldGreen else SunYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    LinguaButton(
                        text = "EVALUAR NIVEL",
                        onClick = { 
                            aiViewModel.evaluarNivel(userText)
                            step = 2 
                        },
                        enabled = userText.length > 10 && !isLoading,
                        containerColor = if (userText.length > 10) NeonCyan else Color.White.copy(alpha = 0.1f),
                        contentColor = if (userText.length > 10) DeepNavy else TextSecondary,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )
                }

                2 -> {
                    // RESULTADO
                    if (isLoading) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                            CircularProgressIndicator(color = NeonCyan, strokeWidth = 4.dp, modifier = Modifier.fillMaxSize())
                            Icon(Icons.Rounded.AutoAwesome, null, tint = NeonCyan, modifier = Modifier.size(40.dp))
                        }
                        Spacer(Modifier.height(32.dp))
                        Text(
                            "Procesando en la nube...", 
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Analizando gramática y vocabulario", 
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(Icons.Rounded.CheckCircle, null, tint = EmeraldGreen, modifier = Modifier.size(80.dp))
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Text(
                            "¡Análisis Completado!", 
                            fontSize = 26.sp, 
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Surface(
                            modifier = Modifier.shadow(20.dp, RoundedCornerShape(20.dp), spotColor = NeonCyan),
                            color = CardBg,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(2.dp, Brush.linearGradient(listOf(NeonCyan, AccentBlue)))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("NIVEL DETECTADO", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text(
                                    text = aiLevel ?: "B1 Intermedio",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NeonCyan
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(24.dp))
                        
                        Surface(
                            color = Color.White.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = aiResponse, 
                                modifier = Modifier.padding(20.dp),
                                textAlign = TextAlign.Center, 
                                color = TextPrimary.copy(alpha = 0.9f),
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            )
                        }
                        
                        Spacer(Modifier.height(48.dp))
                        
                        LinguaButton(
                            text = "CONTINUAR", 
                            onClick = onQuizComplete, 
                            containerColor = EmeraldGreen,
                            modifier = Modifier.fillMaxWidth().height(60.dp)
                        )
                    }
                }
            }
        }
    }
}
