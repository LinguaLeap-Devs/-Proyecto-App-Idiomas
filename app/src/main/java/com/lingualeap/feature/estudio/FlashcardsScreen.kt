package com.lingualeap.feature.estudio

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.AppData
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.feature.lecciones.LessonsViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    title: String,
    categoryId: String,
    lessonsViewModel: LessonsViewModel,
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val words by lessonsViewModel.palabrasGlosario.collectAsStateWithLifecycle()
    val isLoading by lessonsViewModel.cargando.collectAsStateWithLifecycle()
    val usuario by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    
    var currentIndex by remember { mutableIntStateOf(0) }
    var rotated by remember { mutableStateOf(false) }
    var showQuiz by remember { mutableStateOf(false) }
    var quizStep by remember { mutableIntStateOf(0) }
    var quizScore by remember { mutableIntStateOf(0) }
    var quizFinished by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context) { _ -> } }

    LaunchedEffect(categoryId) {
        if (categoryId == "favorites") {
            val favs = usuario?.favoriteWordsIds ?: emptyList()
            lessonsViewModel.cargarPalabrasGlosario("favorites", favs)
        } else {
            lessonsViewModel.cargarPalabrasGlosario(categoryId)
        }
    }

    val rotation by animateFloatAsState(
        targetValue = if (rotated) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "cardRotation"
    )

    val quizWords = remember(words) { if(words.isNotEmpty()) words.shuffled().take(minOf(5, words.size)) else emptyList() }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            if (words.isNotEmpty() && spokenText.equals(words[currentIndex].word, ignoreCase = true)) {
                Toast.makeText(context, "¡Pronunciación Perfecta! 🌟", Toast.LENGTH_SHORT).show()
                rotated = true 
            } else {
                Toast.makeText(context, "Dijiste: \"$spokenText\". Intenta de nuevo.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startListening() {
        if (words.isEmpty()) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Pronuncia: ${words[currentIndex].word}")
        }
        try { speechLauncher.launch(intent) } catch (e: Exception) {
            Toast.makeText(context, "Voz no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showQuiz) "Examen: $title" else "Flashcards: $title", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (words.isEmpty()) {
                Text("No hay palabras en esta categoría", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else if (!showQuiz) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "${currentIndex + 1} de ${words.size}", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .graphicsLayer { rotationY = rotation; cameraDistance = 8 * density }
                            .clickable { rotated = !rotated },
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation <= 90f) {
                            Card(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Inglés", fontSize = 14.sp, color = LinguaColors.Primary, fontWeight = FontWeight.Black)
                                    Spacer(Modifier.height(16.dp))
                                    Text(words[currentIndex].word, fontSize = 36.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                                    Text(words[currentIndex].phonetic, fontSize = 18.sp, color = Color.Gray)
                                    
                                    Spacer(Modifier.height(32.dp))
                                    
                                    Row {
                                        IconButton(
                                            onClick = { tts.language = Locale.US; tts.speak(words[currentIndex].word, TextToSpeech.QUEUE_FLUSH, null, null) },
                                            modifier = Modifier.background(LinguaColors.Primary.copy(alpha = 0.1f), CircleShape).size(56.dp)
                                        ) { Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = LinguaColors.Primary, modifier = Modifier.size(32.dp)) }
                                        
                                        Spacer(Modifier.width(16.dp))

                                        IconButton(
                                            onClick = { startListening() },
                                            modifier = Modifier.background(Color(0xFFF1F5F9), CircleShape).size(56.dp)
                                        ) { Icon(Icons.Rounded.Mic, null, tint = Color.Gray, modifier = Modifier.size(32.dp)) }
                                    }

                                    Spacer(Modifier.height(40.dp))
                                    Text("Toca para ver la respuesta", fontSize = 12.sp, color = Color.LightGray)
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier.fillMaxSize().graphicsLayer { rotationY = 180f },
                                shape = RoundedCornerShape(32.dp),
                                colors = CardDefaults.cardColors(containerColor = LinguaColors.Primary),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Español", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), fontWeight = FontWeight.Black)
                                    Spacer(Modifier.height(16.dp))
                                    Text(words[currentIndex].translation, fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(
                            onClick = { if (currentIndex > 0) { currentIndex--; rotated = false } },
                            enabled = currentIndex > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color.Black),
                            modifier = Modifier.weight(1f).height(56.dp).padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) { Text("Anterior") }

                        Button(
                            onClick = {
                                if (currentIndex < words.size - 1) { currentIndex++; rotated = false }
                                else { showQuiz = true }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LinguaColors.Primary),
                            modifier = Modifier.weight(1f).height(56.dp).padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(if (currentIndex < words.size - 1) "Siguiente" else "¡Hacer Examen!")
                        }
                    }
                }
            } else if (!quizFinished) {
                val currentQuizWord = quizWords[quizStep]
                val options = remember(quizStep) {
                    (words.filter { it.word != currentQuizWord.word }.shuffled().take(minOf(2, words.size - 1)) + currentQuizWord).shuffled()
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(progress = { (quizStep + 1).toFloat() / quizWords.size.toFloat() }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), color = LinguaColors.Primary, trackColor = Color(0xFFF1F5F9))
                    Spacer(Modifier.height(48.dp))
                    Text("¿Cómo se traduce?", fontSize = 16.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text(currentQuizWord.word, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    Spacer(Modifier.height(40.dp))

                    options.forEach { option ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                                if (option.translation == currentQuizWord.translation) quizScore++
                                if (quizStep < quizWords.size - 1) quizStep++
                                else quizFinished = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(2.dp, Color(0xFFE2E8F0)),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) { Text(option.translation, modifier = Modifier.padding(20.dp), fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center) }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFFFFD700), modifier = Modifier.size(100.dp))
                    Spacer(Modifier.height(24.dp))
                    Text("¡Examen Completado!", fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Has acertado $quizScore de ${quizWords.size} palabras.", fontSize = 18.sp, color = Color.Gray, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(48.dp))
                    Button(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) { Text("VOLVER AL GLOSARIO", fontWeight = FontWeight.Black) }
                }
            }
        }
    }
}
