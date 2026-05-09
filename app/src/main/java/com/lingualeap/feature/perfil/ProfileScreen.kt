package com.lingualeap.feature.perfil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.lingualeap.data.model.Badge
import com.lingualeap.data.model.BadgeCategory
import com.lingualeap.data.model.User
import com.lingualeap.ui.components.UserAvatar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

@Composable
fun ProfileScreen(
    usuario: User?,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── HEADER GALAXY ──────────────────────────
        ProfileHeaderGalaxy(usuario, onNavigateToSettings)

        Spacer(Modifier.height(28.dp))

        // ── ESTADÍSTICAS ────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "ESTADÍSTICAS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    label = "Racha",
                    value = "${usuario?.streakDays ?: 0}",
                    emoji = "🔥",
                    color = CoralOrange,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    label = "XP Total",
                    value = "${usuario?.totalXp ?: 0}",
                    emoji = "⚡",
                    color = SunYellow,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    label = "Lecciones",
                    value = "${usuario?.completedLessons?.size ?: 0}",
                    emoji = "📚",
                    color = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── LOGROS / INSIGNIAS ──────────────────────
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = "MIS INSIGNIAS",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(14.dp))

            val insignias = listOf(
                Badge(
                    id = "racha_fuego",
                    name = "Racha de fuego",
                    description = "2 días seguidos",
                    emoji = "🔥",
                    category = BadgeCategory.STREAK,
                    requirement = 2,
                    isUnlocked = (usuario?.streakDays ?: 0) >= 2
                ),
                Badge(
                    id = "meta_superada",
                    name = "Meta superada",
                    description = "Completa tu meta diaria",
                    emoji = "🏆",
                    category = BadgeCategory.XP_DAILY,
                    requirement = 50,
                    isUnlocked = (usuario?.todayXp ?: 0) >= (usuario?.dailyGoalXp ?: 50)
                ),
                Badge(
                    id = "maestro_xp",
                    name = "Maestro Lecciones",
                    description = "Completa 10 lecciones",
                    emoji = "✨",
                    category = BadgeCategory.LESSONS,
                    requirement = 10,
                    isUnlocked = (usuario?.completedLessons?.size ?: 0) >= 10
                )
            )

            insignias.forEach { badge ->
                ProfileBadgeItem(badge)
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        // ── BOTÓN CERRAR SESIÓN ─────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun ProfileHeaderGalaxy(usuario: User?, onSettingsClick: () -> Unit) {
    val fechaMiembro = if ((usuario?.memberSince ?: 0L) > 0) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es"))
        "Miembro desde ${sdf.format(Date(usuario!!.memberSince))}"
    } else "Miembro estelar"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RoyalBlue, DeepNavy)))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            drawCircle(
                color = NeonCyan.copy(alpha = 0.08f),
                radius = 220.dp.toPx(),
                center = Offset(size.width * 0.1f, -40.dp.toPx())
            )
            drawCircle(
                color = AccentBlue.copy(alpha = 0.06f),
                radius = 150.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.9f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.Rounded.Settings, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }

            Box(contentAlignment = Alignment.Center) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
                )
                Surface(
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.1f),
                    border = BorderStroke(3.dp, Brush.linearGradient(listOf(NeonCyan, AccentBlue)))
                ) {
                    UserAvatar(initials = usuario?.avatarInitials ?: "?", photoUrl = usuario?.photoUrl ?: "", size = 90)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = usuario?.name ?: "Usuario",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = fechaMiembro,
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(usuario?.selectedLang?.flag ?: "🌎", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = (usuario?.selectedLang?.name ?: "Curso").uppercase(),
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileStatCard(label: String, value: String, emoji: String, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = CardBg,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(label.uppercase(), color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileBadgeItem(badge: Badge) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = if (badge.isUnlocked) CardBg else Color.White.copy(alpha = 0.03f),
        border = BorderStroke(1.dp, if (badge.isUnlocked) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.04f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (badge.isUnlocked) colorForCategory(badge.category).copy(alpha = 0.15f)
                        else Color.White.copy(alpha = 0.05f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badge.isUnlocked) badge.emoji else "🔒",
                    fontSize = 22.sp,
                    modifier = Modifier.alpha(if (badge.isUnlocked) 1f else 0.5f)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.name,
                    color = if (badge.isUnlocked) TextPrimary else TextSecondary.copy(alpha = 0.5f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = badge.description,
                    color = TextSecondary.copy(alpha = if (badge.isUnlocked) 0.8f else 0.4f),
                    fontSize = 12.sp
                )
            }

            if (badge.isUnlocked) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun colorForCategory(category: BadgeCategory): Color {
    return when(category) {
        BadgeCategory.STREAK -> CoralOrange
        BadgeCategory.XP_DAILY -> NeonCyan
        BadgeCategory.RANKING -> Color(0xFFA855F7)
        BadgeCategory.LESSONS -> AccentBlue
        BadgeCategory.PERFECT_SCORE -> SunYellow
    }
}
