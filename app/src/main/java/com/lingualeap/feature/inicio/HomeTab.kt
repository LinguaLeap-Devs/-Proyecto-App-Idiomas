package com.lingualeap.feature.inicio

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.lingualeap.data.model.Lesson
import com.lingualeap.data.model.User

// ── COLOR PALETTE GALAXY ─────────────────────
private val DeepNavy      = Color(0xFF0D1B3E)
private val RoyalBlue     = Color(0xFF1A3A7A)
private val AccentBlue    = Color(0xFF2D7DD2)
private val NeonCyan      = Color(0xFF00D4FF)
private val SunYellow     = Color(0xFFFBBF24)
private val CoralOrange   = Color(0xFFF97316)
private val CardBg        = Color(0xFF1E2D4F)
private val SurfaceBg     = Color(0xFF0A1628)
private val TextPrimary   = Color(0xFFEFF6FF)
private val TextSecondary = Color(0xFF94A3B8)

enum class NodeType { LESSON, STORY, AUDIO, CHEST, REVIEW }

data class GalaxyNode(
    val id: Int,
    val title: String,
    val emoji: String,
    val type: NodeType,
    val isCompleted: Boolean = false,
    val isLocked: Boolean = true
)

@Composable
fun VistaInicio(
    usuario: User?, 
    lecciones: List<Lesson>, 
    onSettingsClick: () -> Unit,
    onStart: (Lesson) -> Unit
) {
    val currentXP = usuario?.totalXp ?: 0
    val targetXP = usuario?.dailyGoalXp ?: 50
    val progress = (currentXP.toFloat() / targetXP).coerceIn(0f, 1f)
    val streakDays = usuario?.streakDays ?: 0
    val userName = usuario?.name?.split(" ")?.firstOrNull() ?: "Explorador"
    
    // DEFINICIÓN DE LA ETAPA 1: ORDEN EN EL CAFÉ (8 NODOS)
    val etapa1Nodos = remember(usuario) {
        val completed = usuario?.completedLessons ?: emptyList()
        listOf(
            GalaxyNode(1, "¡Un café!", "☕", NodeType.LESSON, completed.contains(1), false),
            GalaxyNode(2, "Cuento: Café Pro", "📖", NodeType.STORY, completed.contains(2), !completed.contains(1)),
            GalaxyNode(3, "Mascotas", "🐶", NodeType.LESSON, completed.contains(3), !completed.contains(2)),
            GalaxyNode(4, "Cofre", "🎁", NodeType.CHEST, completed.contains(4), !completed.contains(3)),
            GalaxyNode(5, "Radio: Café?", "📻", NodeType.AUDIO, completed.contains(5), !completed.contains(4)),
            GalaxyNode(6, "Barista Máster", "☕", NodeType.LESSON, completed.contains(6), !completed.contains(5)),
            GalaxyNode(7, "Cuenta, por favor", "💳", NodeType.LESSON, completed.contains(7), !completed.contains(6)),
            GalaxyNode(8, "Repaso Final", "🏆", NodeType.REVIEW, completed.contains(8), !completed.contains(7))
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(SurfaceBg)
    ) {
        item {
            HeaderGalaxy(
                userName = userName,
                currentXP = currentXP,
                targetXP = targetXP,
                progress = progress,
                streakDays = streakDays,
                idioma = usuario?.selectedLang?.name ?: "Inglés",
                onSettingsClick = onSettingsClick
            )
        }

        item { Spacer(modifier = Modifier.height(26.dp)) }

        item {
            SectionHeaderGalaxy(
                title = "ETAPA 1: ORDEN EN EL CAFÉ",
                subtitle = "Domina el arte de pedir en la galaxia"
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        item {
            LearningPathGalaxy(
                nodos = etapa1Nodos,
                onNodeClick = { node ->
                    // Creamos la lección con 15 preguntas como solicitaste
                    val lessonObj = lecciones.find { it.id == node.id } ?: Lesson(
                        id = node.id, 
                        title = node.title, 
                        emoji = node.emoji,
                        xpReward = if (node.type == NodeType.REVIEW) 50 else 20
                    )
                    onStart(lessonObj)
                }
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }

        item {
            DailyChallengeCard()
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeaderGalaxy(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title,
            color = SunYellow,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun LearningPathGalaxy(nodos: List<GalaxyNode>, onNodeClick: (GalaxyNode) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        nodos.forEachIndexed { index, node ->
            val curveRight = (index % 4 < 2)
            val offsetX = when {
                index % 4 == 1 -> 55.dp
                index % 4 == 3 -> (-55).dp
                else -> 0.dp
            }

            LessonNodeGalaxy(node, offsetX, onNodeClick)

            if (index < nodos.lastIndex) {
                ConnectorGalaxy(node.isCompleted, curveRight)
            }
        }
    }
}

@Composable
fun LessonNodeGalaxy(node: GalaxyNode, offsetX: Dp, onNodeClick: (GalaxyNode) -> Unit) {
    val isCurrent = !node.isCompleted && !node.isLocked
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCurrent) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scale"
    )

    Column(
        modifier = Modifier.offset(x = offsetX).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isCurrent) {
                Box(
                    Modifier.size(95.dp).scale(scale)
                        .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.3f), Color.Transparent)), CircleShape)
                )
            }
            
            val nodeBg = when {
                node.isCompleted -> Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
                isCurrent -> Brush.linearGradient(listOf(AccentBlue, NeonCyan))
                node.type == NodeType.CHEST -> Brush.linearGradient(listOf(SunYellow, CoralOrange))
                node.type == NodeType.REVIEW -> Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)))
                else -> Brush.linearGradient(listOf(Color(0xFF334155), CardBg))
            }

            Surface(
                modifier = Modifier
                    .size(if (node.type == NodeType.REVIEW) 88.dp else 76.dp)
                    .shadow(if (!node.isLocked) 15.dp else 0.dp, CircleShape, spotColor = NeonCyan)
                    .clickable(enabled = !node.isLocked) { onNodeClick(node) },
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(3.dp, if (isCurrent) SunYellow else Color.White.copy(alpha = 0.1f))
            ) {
                Box(modifier = Modifier.fillMaxSize().background(nodeBg), contentAlignment = Alignment.Center) {
                    if (node.isLocked) {
                        Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.3f), modifier = Modifier.size(26.dp))
                    } else {
                        Text(node.emoji, fontSize = if (node.type == NodeType.REVIEW) 38.sp else 34.sp)
                    }
                    
                    if (node.isCompleted) {
                        Box(
                            Modifier.align(Alignment.BottomEnd).offset(x = (-2).dp, y = (-2).dp)
                                .size(24.dp).background(SunYellow, CircleShape).border(2.dp, SurfaceBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = DeepNavy, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
        Text(
            text = node.title.uppercase(),
            color = if (node.isLocked) TextSecondary else TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 8.dp),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun ConnectorGalaxy(completed: Boolean, curveRight: Boolean) {
    Canvas(Modifier.size(90.dp, 65.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, 0f)
            cubicTo(
                if (curveRight) size.width * 1.4f else -size.width * 0.4f, size.height * 0.2f,
                if (curveRight) size.width * 1.4f else -size.width * 0.4f, size.height * 0.8f,
                size.width / 2, size.height
            )
        }
        drawPath(
            path = path,
            color = if (completed) Color(0xFF10B981) else Color.White.copy(alpha = 0.15f),
            style = Stroke(
                width = 4.dp.toPx(),
                pathEffect = if (completed) null else PathEffect.dashPathEffect(floatArrayOf(15f, 10f))
            )
        )
    }
}

@Composable
fun HeaderGalaxy(
    userName: String,
    currentXP: Int,
    targetXP: Int,
    progress: Float,
    streakDays: Int,
    idioma: String,
    onSettingsClick: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = EaseOutCubic),
        label = "xp"
    )

    Box(
        modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(RoyalBlue, DeepNavy)))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            drawCircle(NeonCyan.copy(alpha = 0.08f), radius = 200.dp.toPx(), center = Offset(size.width * 0.9f, -20.dp.toPx()))
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 30.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        border = BorderStroke(2.dp, Brush.linearGradient(listOf(NeonCyan, AccentBlue)))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(userName.take(1).uppercase(), color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 22.sp)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("¡Hola, $userName! 👋", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Misión: Dominar $idioma", color = TextSecondary, fontSize = 13.sp)
                    }
                }
                IconButton(onClick = onSettingsClick, modifier = Modifier.background(Color.White.copy(alpha = 0.08f), CircleShape)) {
                    Icon(Icons.Outlined.Settings, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(28.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GalaxyChip("🔥 $streakDays DÍAS", CoralOrange)
                GalaxyChip(idioma.uppercase(), NeonCyan)
            }

            Spacer(Modifier.height(28.dp))

            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Text("ENERGÍA DIARIA", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    Text("$currentXP / $targetXP XP", color = SunYellow, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
                Spacer(Modifier.height(10.dp))
                Box(Modifier.fillMaxWidth().height(12.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.08f))) {
                    Box(Modifier.fillMaxWidth(animatedProgress).fillMaxHeight().background(Brush.horizontalGradient(listOf(AccentBlue, NeonCyan, SunYellow)), CircleShape))
                }
            }
        }
    }
}

@Composable
fun GalaxyChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(text, color = color, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
    }
}

@Composable
fun DailyChallengeCard() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))))
            .padding(22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("RETO DEL DÍA", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Black)
                Text("Cazador de Estrellas", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Text("Gana 50 XP extras hoy", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
            }
            Text("🎯", fontSize = 32.sp)
        }
    }
}

@Composable
fun QuickStatsRow(usuario: User?) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        StatBox("📚", "${usuario?.completedLessons?.size ?: 0}", "Lecciones", Modifier.weight(1f))
        StatBox("🏅", "${usuario?.desafiosCompletados?.size ?: 0}", "Logros", Modifier.weight(1f))
        StatBox("✨", "${usuario?.totalXp ?: 0}", "Total XP", Modifier.weight(1f))
    }
}

@Composable
fun StatBox(emoji: String, value: String, label: String, modifier: Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp), color = CardBg, border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 22.sp)
            Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(label.uppercase(), color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
