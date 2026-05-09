package com.lingualeap.feature.estudio

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.AppData
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.feature.lecciones.LessonsViewModel
import kotlinx.coroutines.delay
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(
    title: String,
    categoryId: String,
    lessonsViewModel: LessonsViewModel,
    authViewModel: AuthViewModel,
    onNavigateToFlashcards: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val usuarioActual by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val words by lessonsViewModel.palabrasGlosario.collectAsStateWithLifecycle()
    val isLoading by lessonsViewModel.cargando.collectAsStateWithLifecycle()
    val favoritos = usuarioActual?.favoriteWordsIds ?: emptyList()
    
    var searchQuery by remember { mutableStateOf("") }
    var isAutoPlaying by remember { mutableStateOf(false) }
    var currentAutoIndex by remember { mutableIntStateOf(-1) }
    val listState = rememberLazyListState()

    // Cargar palabras al iniciar
    LaunchedEffect(categoryId) {
        lessonsViewModel.cargarPalabrasGlosario(categoryId)
    }

    // FILTRO DE BÚSQUEDA
    val filteredWords = remember(searchQuery, words) {
        if (searchQuery.isBlank()) words
        else words.filter { 
            it.word.contains(searchQuery, ignoreCase = true) || 
            it.translation.contains(searchQuery, ignoreCase = true) 
        }
    }

    // MOTOR TTS
    val tts = remember { TextToSpeech(context) { _ -> } }
    
    // RECONOCIMIENTO DE VOZ
    var targetWordForSpeech by remember { mutableStateOf("") }
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            if (spokenText.equals(targetWordForSpeech, ignoreCase = true)) {
                Toast.makeText(context, "¡Excelente pronunciación! ✨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Entendí: \"$spokenText\". ¡Inténtalo de nuevo!", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun startListening(target: String) {
        targetWordForSpeech = target
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Pronuncia: $target")
        }
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Reconocimiento de voz no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    // LÓGICA DE AUTO-REPRODUCCIÓN
    LaunchedEffect(isAutoPlaying) {
        if (isAutoPlaying) {
            filteredWords.forEachIndexed { index, item ->
                if (!isAutoPlaying) return@forEachIndexed
                currentAutoIndex = index
                listState.animateScrollToItem(index)
                
                tts.language = Locale.US
                tts.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, null)
                delay(2000)
                
                tts.language = Locale.forLanguageTag("es-ES")
                tts.speak(item.translation, TextToSpeech.QUEUE_FLUSH, null, null)
                delay(3000)
            }
            isAutoPlaying = false
            currentAutoIndex = -1
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
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Black) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isAutoPlaying = !isAutoPlaying }) {
                            Icon(
                                imageVector = if (isAutoPlaying) Icons.Rounded.StopCircle else Icons.Rounded.PlayCircle,
                                contentDescription = "Auto Play",
                                tint = if (isAutoPlaying) Color.Red else LinguaColors.Primary
                            )
                        }
                        IconButton(onClick = onNavigateToFlashcards) {
                            Icon(Icons.Rounded.Style, contentDescription = "Modo Flashcards", tint = LinguaColors.Primary)
                        }
                    }
                )
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar palabra o traducción...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F5F9),
                        unfocusedContainerColor = Color(0xFFF1F5F9),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    ) { padding ->
        if (filteredWords.isEmpty() && !isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay resultados", color = Color.Gray)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                itemsIndexed(filteredWords) { index, item ->
                    val isFavorite = favoritos.contains(item.word)
                    val isBeingPlayed = index == currentAutoIndex

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isBeingPlayed) LinguaColors.Primary.copy(alpha = 0.1f) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isBeingPlayed) 4.dp else 2.dp),
                        border = if (isBeingPlayed) BorderStroke(2.dp, LinguaColors.Primary) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { 
                                    tts.language = Locale.US
                                    tts.speak(item.word, TextToSpeech.QUEUE_FLUSH, null, null)
                                },
                                modifier = Modifier.background(LinguaColors.Primary.copy(alpha = 0.1f), CircleShape).size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = LinguaColors.Primary, modifier = Modifier.size(20.dp))
                            }
                            
                            Spacer(Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.word, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(item.translation, fontSize = 14.sp, color = Color.Gray)
                                Text(item.phonetic, fontSize = 12.sp, color = LinguaColors.Primary, fontWeight = FontWeight.Medium)
                            }

                            IconButton(onClick = { startListening(item.word) }) {
                                Icon(Icons.Rounded.Mic, "Practicar", tint = Color.Gray)
                            }

                            IconButton(onClick = { authViewModel.toggleFavoriteWord(item.word) }) {
                                Icon(
                                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = null,
                                    tint = if (isFavorite) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
