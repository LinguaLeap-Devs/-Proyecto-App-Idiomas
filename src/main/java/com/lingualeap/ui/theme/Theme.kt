package com.lingualeap.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────
//  COLORES — Cambia estos valores para
//  modificar toda la paleta de la app
// ─────────────────────────────────────────

object LinguaColors {

    // Color principal (botones, acentos, íconos activos)
    val Primary        = Color(0xFF5C6BC0)   // Indigo suave
    val PrimaryDark    = Color(0xFF3949AB)   // Indigo oscuro (hover/pressed)
    val PrimaryLight   = Color(0xFFE8EAF6)   // Fondo suave del primary

    // Color de acento secundario (logros, racha, XP)
    val Accent         = Color(0xFFFFB300)   // Ámbar dorado
    val AccentLight    = Color(0xFFFFF8E1)   // Fondo suave del acento

    // Estado de éxito (lección completada, respuesta correcta)
    val Success        = Color(0xFF43A047)
    val SuccessLight   = Color(0xFFE8F5E9)

    // Estado de error (respuesta incorrecta, validación)
    val Error          = Color(0xFFE53935)
    val ErrorLight     = Color(0xFFFFEBEE)

    // Fondos
    val Background     = Color(0xFFF5F5F7)   // Fondo general de la app
    val Surface        = Color(0xFFFFFFFF)   // Tarjetas y superficies
    val SurfaceDim     = Color(0xFFF0F0F5)   // Superficies ligeramente grises

    // Texto
    val TextPrimary    = Color(0xFF1A1A2E)   // Texto principal (casi negro azulado)
    val TextSecondary  = Color(0xFF6B6B8A)   // Texto secundario / subtítulos
    val TextHint       = Color(0xFFAAAAAA)   // Placeholders

    // Divisores y bordes
    val Border         = Color(0xFFE0E0E8)

    // Colores de los idiomas (para las tarjetas en el selector)
    val EnglishColor   = Color(0xFF1565C0)   // Azul USA
    val FrenchColor    = Color(0xFFD32F2F)   // Rojo Francia
    val PortugueseColor= Color(0xFF2E7D32)   // Verde Brasil
    val GermanColor    = Color(0xFF37474F)   // Gris carbón Alemania
    val JapaneseColor  = Color(0xFFC62828)   // Rojo Japón
}

// ─────────────────────────────────────────
//  ESPACIADOS (DIMENS) — Estándares de la UI
// ─────────────────────────────────────────

object LinguaSpacing {
    val None        = 0.dp
    val Tiny        = 4.dp
    val Small       = 8.dp
    val Medium      = 16.dp
    val Large       = 24.dp
    val ExtraLarge  = 32.dp
    val Huge        = 40.dp
    val ScreenPadding = 24.dp
}

// ─────────────────────────────────────────
//  COLOR SCHEME para Material3
// ─────────────────────────────────────────

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
    error            = LinguaColors.Error,
    onError          = Color.White,
)

// ─────────────────────────────────────────
//  TEMA PRINCIPAL DE LA APP
//  Envuelve toda la app en MainActivity
// ─────────────────────────────────────────

@Composable
fun LinguaLeapTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = LinguaTypography,
        content     = content
    )
}
