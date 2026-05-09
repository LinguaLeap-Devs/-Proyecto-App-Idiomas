package com.lingualeap.feature.lecciones

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
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
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.ui.components.LinguaButton
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
fun RepasoScreen(
    authViewModel: AuthViewModel,
    lessonsViewModel: LessonsViewModel,
    onClose: () -> Unit
) {
    val contexto = LocalContext.current
    val usuarioActual by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val leccionesDisponibles by lessonsViewModel.lecciones.collectAsStateWithLifecycle()
    
    val idsPreguntasFallidas = usuarioActual?.wrongQuestionsIds ?: emptyList()
    
    val listaPreguntasFallidas = remember(idsPreguntasFallidas, leccionesDisponibles) {
        val todasLasPreguntas = leccionesDisponibles.flatMap { it.questions }
        idsPreguntasFallidas.mapNotNull { idFallido -> todasLasPreguntas.find { it.id == idFallido } }
    }

    var indicePreguntaActual by remember { mutableIntStateOf(0) }
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }
    var respuestaHaSidoComprobada by remember { mutableStateOf(false) }
    var esRespuestaCorrecta by remember { mutableStateOf(false) }
    
    var erroresCorregidosEnSesion by remember { mutableIntStateOf(0) }

    val motorTextoAVoz = remember {
        TextToSpeech(contexto) { _ -> }
    }

    val pronunciarTexto = { texto: String ->
        val segmentos = texto.split(Regex("['\"“”]")).filter { it.isNotBlank() }
        segmentos.forEachIndexed { index, segmento ->
            val textoEnMinuscula = segmento.lowercase()
            val esEspanol = textoEnMinuscula.contains(Regex("[¿áéíóúñ]")) || 
                            textoEnMinuscula.contains("como") || 
                            textoEnMinuscula.contains("dice") || 
                            textoEnMinuscula.contains("traduce")

            motorTextoAVoz.language = if (esEspanol) Locale("es", "ES") else Locale.US
            val modoDeCola = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            motorTextoAVoz.speak(segmento, modoDeCola, null, null)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            motorTextoAVoz.stop()
            motorTextoAVoz.shutdown()
        }
    }

    if (listaPreguntasFallidas.isEmpty() || indicePreguntaActual >= listaPreguntasFallidas.size) {
        PantallaDeExitoRepaso(erroresCorregidosEnSesion, onClose)
        return
    }

    val preguntaActual = listaPreguntasFallidas[indicePreguntaActual]

    LaunchedEffect(indicePreguntaActual) {
        pronunciarTexto(preguntaActual.text)
    }

    Scaffold(
        containerColor = SurfaceBg,
        topBar = {
            TopAppBar(
                title = {
                    val progresoActual = (indicePreguntaActual).toFloat() / listaPreguntasFallidas.size.toFloat()
                    Box(Modifier.fillMaxWidth().padding(end = 16.dp)) {
                        Box(Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f))) {
                            Box(
                                Modifier
                                    .fillMaxWidth(progresoActual)
                                    .fillMaxHeight()
                                    .background(Brush.horizontalGradient(listOf(AccentBlue, NeonCyan)), CircleShape)
                                    .shadow(8.dp, CircleShape, spotColor = NeonCyan)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cerrar Repaso", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBg)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Fondo decorativo
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(NeonCyan.copy(alpha = 0.04f), radius = 200.dp.toPx(), center = Offset(size.width * 0.9f, 0f))
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "PROTOCOLO DE RECUPERACIÓN",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "¿Recuerdas la forma correcta?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // --- TARJETA DE PREGUNTA ---
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
                            onClick = { pronunciarTexto(preguntaActual.text) },
                            modifier = Modifier.background(NeonCyan.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = NeonCyan)
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = preguntaActual.text,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 28.sp
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // --- OPCIONES ---
                preguntaActual.options.forEach { opcion ->
                    val esSeleccionada = opcionSeleccionada == opcion
                    val colorBorde = when {
                        respuestaHaSidoComprobada && opcion == preguntaActual.correctAnswer -> EmeraldGreen
                        respuestaHaSidoComprobada && esSeleccionada && !esRespuestaCorrecta -> Color(0xFFEF4444)
                        esSeleccionada -> NeonCyan
                        else -> Color.White.copy(alpha = 0.1f)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable(enabled = !respuestaHaSidoComprobada) { opcionSeleccionada = opcion },
                        shape = RoundedCornerShape(20.dp),
                        color = if (esSeleccionada) Color.White.copy(alpha = 0.08f) else CardBg,
                        border = BorderStroke(2.dp, colorBorde)
                    ) {
                        Text(
                            text = opcion,
                            modifier = Modifier.padding(20.dp),
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
                Spacer(Modifier.height(120.dp))
            }

            // --- FEEDBACK PANEL ---
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                AnimatedVisibility(
                    visible = respuestaHaSidoComprobada,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (esRespuestaCorrecta) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                        border = BorderStroke(1.dp, if (esRespuestaCorrecta) EmeraldGreen.copy(alpha = 0.3f) else Color(0xFFEF4444).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(40.dp).background(if (esRespuestaCorrecta) EmeraldGreen else Color(0xFFEF4444), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(if (esRespuestaCorrecta) Icons.Rounded.Check else Icons.Rounded.PriorityHigh, null, tint = Color.White)
                                }
                                Spacer(Modifier.width(16.dp))
                                Text(
                                    text = if (esRespuestaCorrecta) "¡Sincronización Exitosa!" else "Error Persistente",
                                    color = if (esRespuestaCorrecta) EmeraldGreen else Color(0xFFEF4444),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                            
                            if (preguntaActual.explanation.isNotBlank()) {
                                Spacer(Modifier.height(16.dp))
                                Surface(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                                        Icon(Icons.Rounded.Lightbulb, null, tint = SunYellow, modifier = Modifier.size(20.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = preguntaActual.explanation,
                                            fontSize = 14.sp,
                                            lineHeight = 20.sp,
                                            color = TextPrimary.copy(alpha = 0.9f)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(24.dp))
                            LinguaButton(
                                text = "CONTINUAR",
                                onClick = {
                                    if (esRespuestaCorrecta) {
                                        authViewModel.eliminarErrorDePregunta(preguntaActual.id)
                                        erroresCorregidosEnSesion++
                                    }
                                    indicePreguntaActual++
                                    opcionSeleccionada = null
                                    respuestaHaSidoComprobada = false
                                },
                                containerColor = if (esRespuestaCorrecta) EmeraldGreen else Color(0xFFEF4444),
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }

                if (!respuestaHaSidoComprobada) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = SurfaceBg,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                    ) {
                        Box(Modifier.padding(24.dp)) {
                            LinguaButton(
                                text = "VERIFICAR",
                                onClick = {
                                    esRespuestaCorrecta = opcionSeleccionada == preguntaActual.correctAnswer
                                    respuestaHaSidoComprobada = true
                                },
                                enabled = opcionSeleccionada != null,
                                modifier = Modifier.fillMaxWidth().height(56.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaDeExitoRepaso(erroresCorregidos: Int, onClose: () -> Unit) {
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
                    .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
            )
            Icon(
                imageVector = Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(100.dp)
            )
        }
        
        Spacer(Modifier.height(40.dp))
        
        Text(
            text = "REPARACIÓN COMPLETADA",
            color = NeonCyan,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Has corregido $erroresCorregidos anomalías",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tu base de datos de conocimiento está ahora más estable.",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.weight(1f))
        
        LinguaButton(
            text = "VOLVER A LA BASE",
            onClick = onClose,
            containerColor = NeonCyan,
            contentColor = DeepNavy,
            modifier = Modifier.fillMaxWidth().height(64.dp)
        )
    }
}
