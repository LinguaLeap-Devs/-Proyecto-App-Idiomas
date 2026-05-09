package com.lingualeap.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.data.model.Badge
import com.lingualeap.data.model.Language
import com.lingualeap.data.model.Lesson
import com.lingualeap.data.model.LessonProgress
import com.lingualeap.data.model.LessonStatus
import com.lingualeap.ui.theme.LinguaColors

@Composable
fun LinguaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = placeholder?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }
            },
            isError = errorMessage != null,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun LinguaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    // Definimos un color de sombra basado en el color del botón o del tema
    val shadowColor = if (containerColor == MaterialTheme.colorScheme.primary) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
    } else {
        containerColor.copy(alpha = 0.7f)
    }

    Box(modifier = modifier) {
        // Shadow/3D effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 4.dp)
                .background(shadowColor, RoundedCornerShape(16.dp))
        )
        
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            enabled = enabled && !isLoading,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun LinguaOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun DividerWithText(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f), 
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 
            thickness = 2.dp
        )
        Text(
            text = text.uppercase(),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f), 
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), 
            thickness = 2.dp
        )
    }
}

@Composable
fun UserAvatar(initials: String, photoUrl: String = "", size: Int = 40) {
    if (photoUrl.isNotBlank()) {
        coil.compose.AsyncImage(
            model = photoUrl,
            contentDescription = "Avatar",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    } else {
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun LessonNode(
    lesson: Lesson,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCurrent: Boolean = false
) {
    val mainColor = when {
        lesson.isLocked -> MaterialTheme.colorScheme.surfaceVariant
        lesson.isCompleted -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }
    
    val shadowColor = when {
        lesson.isLocked -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        lesson.isCompleted -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isCurrent) {
            LessonPopup(title = "Cuento: ${lesson.title}", onStart = onClick)
            Spacer(Modifier.height(12.dp))
        }

        Box(contentAlignment = Alignment.Center) {
            // Shadow
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 6.dp)
                    .background(shadowColor, CircleShape)
            )
            
            // Main Circle
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clickable(enabled = !lesson.isLocked, onClick = onClick),
                shape = CircleShape,
                color = mainColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (lesson.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.onSurfaceVariant, 
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Text(
                            text = lesson.emoji,
                            fontSize = 40.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonPopup(title: String, onStart: () -> Unit) {
    val triangleShape = GenericShape { size, _ ->
        moveTo(size.width / 2f, size.height)
        lineTo(size.width / 2f - 10.dp.value, 0f)
        lineTo(size.width / 2f + 10.dp.value, 0f)
        close()
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 240.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                LinguaButton(
                    text = "EMPEZAR +20 EXP",
                    onClick = onStart,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .size(width = 20.dp, height = 10.dp)
                .background(MaterialTheme.colorScheme.primary, triangleShape)
        )
    }
}

@Composable
fun LanguageCard(
    language: Language,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = language.flag,
                fontSize = 40.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = language.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SpecialtyCard(title: String, routes: List<String>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Terminal, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onPrimary, 
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title, 
                    color = MaterialTheme.colorScheme.onPrimary, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.height(12.dp))
            routes.forEach { route ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f), CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = route, 
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f), 
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, iconColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = value, 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black, 
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label, 
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── PILL BADGE ────────────────────────────
@Composable
fun PillBadge(
    text: String,
    backgroundColor: Color = LinguaColors.Success,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ── LESSON PROGRESS CARD ─────────────────
@Composable
fun LessonProgressCard(
    progress: LessonProgress,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val porcentaje = if (progress.totalWords > 0)
        (progress.completedWords.toFloat() / progress.totalWords * 100).toInt()
    else 0

    val statusColor = when (progress.status) {
        LessonStatus.COMPLETED -> LinguaColors.Success
        LessonStatus.IN_PROGRESS -> LinguaColors.Primary
        LessonStatus.PENDING -> LinguaColors.Fire
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icono emoji
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(progress.lessonEmoji.ifBlank { "📚" }, fontSize = 22.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = progress.lessonTitle,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${progress.totalWords} palabras · ${progress.status.displayName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Barra de progreso + porcentaje
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(porcentaje / 100f)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "$porcentaje%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

// ── BADGE CARD (INSIGNIA) ────────────────
@Composable
fun BadgeCard(
    badge: Badge,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = if (badge.isUnlocked)
            MaterialTheme.colorScheme.surface
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(
            1.5.dp,
            if (badge.isUnlocked) MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (badge.isUnlocked) badge.emoji else "🔒",
                fontSize = 28.sp
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = if (badge.isUnlocked) badge.description else "Bloqueado",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (badge.isUnlocked) 0.8f else 0.4f
                    )
                )
            }
        }
    }
}

// ── PODIUM VIEW (TOP 3 RANKING) ──────────
data class PodiumEntry(
    val name: String,
    val initials: String,
    val photoUrl: String = "",
    val xp: Int,
    val position: Int
)

@Composable
fun PodiumView(
    top3: List<PodiumEntry>,
    modifier: Modifier = Modifier
) {
    if (top3.size < 3) return

    val first = top3[0]
    val second = top3[1]
    val third = top3[2]

    val podiumColors = listOf(
        Color(0xFFFFD700), // Oro
        Color(0xFFC0C0C0), // Plata
        Color(0xFFCD7F32)  // Bronce
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // #2 - Plata (izquierda)
        PodiumColumn(second, podiumColors[1], barHeight = 90, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        // #1 - Oro (centro, más alto)
        PodiumColumn(first, podiumColors[0], barHeight = 120, showCrown = true, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        // #3 - Bronce (derecha)
        PodiumColumn(third, podiumColors[2], barHeight = 70, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PodiumColumn(
    entry: PodiumEntry,
    color: Color,
    barHeight: Int,
    showCrown: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Corona para #1
        if (showCrown) {
            Text("👑", fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
        }

        // Avatar
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(3.dp, color, CircleShape)
            ) {
                UserAvatar(
                    initials = entry.initials,
                    photoUrl = entry.photoUrl,
                    size = 50
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = entry.name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = "${entry.xp} XP",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )

        Spacer(Modifier.height(6.dp))

        // Barra del podio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight.dp)
                .background(
                    color = color.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.position.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

// ── PROFILE GRADIENT HEADER ──────────────
@Composable
fun ProfileGradientHeader(
    nombre: String,
    iniciales: String,
    photoUrl: String,
    subtitulo: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(LinguaColors.Primary, LinguaColors.PrimaryDark)
                )
            )
            .padding(top = 40.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar con borde blanco
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                UserAvatar(initials = iniciales, photoUrl = photoUrl, size = 90)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
