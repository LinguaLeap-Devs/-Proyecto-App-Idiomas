package com.lingualeap.feature.inicio

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.lingualeap.data.model.User

// ── COLOR PALETTE GALAXY ─────────────────────
private val DeepNavy      = Color(0xFF0D1B3E)
private val RoyalBlue     = Color(0xFF1A3A7A)
private val AccentBlue    = Color(0xFF2D7DD2)
private val NeonCyan      = Color(0xFF00D4FF)
private val SunYellow     = Color(0xFFFBBF24)
private val CardBg        = Color(0xFF1E2D4F)
private val SurfaceBg     = Color(0xFF0A1628)
private val TextPrimary   = Color(0xFFEFF6FF)
private val TextSecondary = Color(0xFF94A3B8)

@Composable
fun VistaRepaso(
    usuario: User? = null,
    onStart: () -> Unit,
    onFavorites: () -> Unit,
    onViajes: () -> Unit,
    onComida: () -> Unit,
    onCultura: () -> Unit,
    onNegocios: () -> Unit,
    onHogar: () -> Unit,
    onTransporte: () -> Unit,
    onRopa: () -> Unit,
    onFrases: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── HEADER ──
        RepasoHeader(
            errorCount = usuario?.wrongQuestionsIds?.size ?: 0,
            lessonCount = usuario?.completedLessons?.size ?: 0
        )

        Spacer(Modifier.height(26.dp))

        // ── ACCIONES RÁPIDAS ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionLabel("TU PROGRESO")
            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    emoji = "❌",
                    title = "Repaso de Errores",
                    subtitle = "Corrige tus fallos",
                    badge = "${usuario?.wrongQuestionsIds?.size ?: 0} pendientes",
                    gradientColors = listOf(Color(0xFF1E3A8A), AccentBlue),
                    onClick = onStart,
                    modifier = Modifier.weight(1f)
                )
                ActionCard(
                    emoji = "⭐",
                    title = "Mis Favoritos",
                    subtitle = "Tus guardados",
                    badge = "${usuario?.favoriteWordsIds?.size ?: 0} palabras",
                    gradientColors = listOf(Color(0xFF1E3A4A), Color(0xFF0E7490)),
                    onClick = onFavorites,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── CATEGORÍAS ──
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            SectionLabel("TEMAS ESPECÍFICOS")
            Spacer(Modifier.height(14.dp))

            val categorias = listOf(
                Triple("Viajes", "✈️", onViajes),
                Triple("Comida", "🍕", onComida),
                Triple("Cultura", "🏛️", onCultura),
                Triple("Negocios", "💼", onNegocios),
                Triple("Hogar", "🏠", onHogar),
                Triple("Transporte", "🚌", onTransporte),
                Triple("Ropa", "👕", onRopa),
                Triple("Frases", "💬", onFrases)
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                categorias.chunked(2).forEach { fila ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        fila.forEach { (titulo, emoji, click) ->
                            MiniCategoryCard(titulo, emoji, click, Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun RepasoHeader(errorCount: Int, lessonCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RoyalBlue, DeepNavy)))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            drawCircle(
                color = NeonCyan.copy(alpha = 0.08f),
                radius = 170.dp.toPx(),
                center = Offset(size.width * 0.9f, -10.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 30.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Repaso 📖", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Refuerza tu conocimiento", color = TextSecondary, fontSize = 13.sp)
                }
                Surface(
                    color = NeonCyan.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                ) {
                    Text(
                        "INGLÉS",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniStatBox(value = "$errorCount", label = "ERRORES", modifier = Modifier.weight(1f))
                MiniStatBox(value = "$lessonCount", label = "LECCIONES", modifier = Modifier.weight(1f))
                MiniStatBox(value = "85%", label = "PRECISIÓN", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MiniStatBox(value: String, label: String, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(label, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(gradientColors))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) { Text(emoji, fontSize = 20.sp) }
            }

            Spacer(Modifier.height(14.dp))
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            
            Spacer(Modifier.height(10.dp))
            Surface(color = Color.White.copy(alpha = 0.2f), shape = CircleShape) {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier.align(Alignment.BottomEnd).size(18.dp)
        )
    }
}

@Composable
fun MiniCategoryCard(titulo: String, emoji: String, onClick: () -> Unit, modifier: Modifier) {
    Surface(
        modifier = modifier.height(90.dp).clickable(onClick = onClick),
        color = CardBg,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 26.sp)
            Spacer(Modifier.height(6.dp))
            Text(titulo, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.2.sp
    )
}
