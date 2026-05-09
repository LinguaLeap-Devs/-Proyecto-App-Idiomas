package com.lingualeap.feature.estudio

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Style
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
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.feature.autenticacion.AuthViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    authViewModel: AuthViewModel,
    onNavigateToPractice: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val usuarioActual by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val favoritos = usuarioActual?.favoriteWordsIds ?: emptyList()
    
    val context = LocalContext.current
    val tts = remember { TextToSpeech(context) { _ -> } }
    
    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                },
                actions = {
                    if (favoritos.isNotEmpty()) {
                        IconButton(onClick = onNavigateToPractice) {
                            Icon(Icons.Rounded.Style, contentDescription = "Practicar", tint = LinguaColors.Primary)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (favoritos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Favorite, null, tint = Color.LightGray, modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Aún no tienes palabras favoritas", color = Color.Gray)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Botón destacado de práctica
                Button(
                    onClick = onNavigateToPractice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LinguaColors.Primary)
                ) {
                    Icon(Icons.Rounded.Style, null)
                    Spacer(Modifier.width(12.dp))
                    Text("PRACTICAR ESTAS PALABRAS", fontWeight = FontWeight.Black)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(favoritos) { palabra ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { 
                                            tts.language = Locale.US
                                            tts.speak(palabra, TextToSpeech.QUEUE_FLUSH, null, null)
                                        },
                                        modifier = Modifier.background(LinguaColors.Primary.copy(alpha = 0.1f), CircleShape).size(40.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = LinguaColors.Primary, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Text(palabra, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                }
                                IconButton(onClick = { authViewModel.toggleFavoriteWord(palabra) }) {
                                    Icon(Icons.Rounded.Delete, null, tint = Color.Red.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
