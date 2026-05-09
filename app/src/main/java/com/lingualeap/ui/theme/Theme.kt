package com.lingualeap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────
//  NUEVA IDENTIDAD VISUAL: AZUL ESPARTANO Y PLATA
// ─────────────────────────────────────────

object LinguaColors {
    val Primary        = Color(0xFF0077B6) // Azul profundo del logo
    val PrimaryDark    = Color(0xFF023E8A)
    val PrimaryLight   = Color(0xFFCAF0F8)
    val Accent         = Color(0xFF00B4D8) // Azul claro metálico
    val Success        = Color(0xFF4CAF50)
    val Error          = Color(0xFFEA2B2B)
    
    // Light Palette
    val Background     = Color(0xFFF8FAFC) // Gris muy claro (Plata suave)
    val Surface        = Color(0xFFFFFFFF)
    val TextPrimary    = Color(0xFF1E293B)
    val TextSecondary  = Color(0xFF64748B)
    
    // Dark Palette
    val BackgroundDark = Color(0xFF0F172A) // Azul noche profundo
    val SurfaceDark    = Color(0xFF1E293B) // Slate oscuro
    val TextPrimaryDark = Color(0xFFF1F5F9)
    val TextSecondaryDark = Color(0xFF94A3B8)
    
    val Border         = Color(0xFFE2E8F0) // Plata metálico
    
    // Colores de Gamificación
    val Fire           = Color(0xFFFF9600)
    val XP             = Color(0xFF0077B6)
    val Heart          = Color(0xFFFF4B4B)
}

object LinguaSpacing {
    val Tiny          = 4.dp
    val Small         = 8.dp
    val Medium        = 16.dp
    val Large         = 24.dp
    val Huge          = 32.dp
    val ScreenPadding = 24.dp
}

private val LightColorScheme = lightColorScheme(
    primary          = LinguaColors.Primary,
    onPrimary        = Color.White,
    primaryContainer = LinguaColors.PrimaryLight,
    secondary        = LinguaColors.Accent,
    onSecondary      = Color.White,
    background       = LinguaColors.Background,
    surface          = LinguaColors.Surface,
    onBackground     = LinguaColors.TextPrimary,
    onSurface        = LinguaColors.TextPrimary,
    onSurfaceVariant = LinguaColors.TextSecondary,
    error            = LinguaColors.Error,
    onError          = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary          = Color(0xFF90CAF9), // Un azul más claro para contraste en oscuro
    onPrimary        = Color(0xFF003354),
    primaryContainer = LinguaColors.PrimaryDark,
    secondary        = LinguaColors.Accent,
    onSecondary      = Color.Black,
    background       = LinguaColors.BackgroundDark,
    surface          = LinguaColors.SurfaceDark,
    onBackground     = LinguaColors.TextPrimaryDark,
    onSurface        = LinguaColors.TextPrimaryDark,
    onSurfaceVariant = LinguaColors.TextSecondaryDark,
    error            = Color(0xFFFFB4AB),
    onError          = Color(0xFF690005),
)

@Composable
fun LinguaLeapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = LinguaTypography
    ) {
        content()
    }
}
