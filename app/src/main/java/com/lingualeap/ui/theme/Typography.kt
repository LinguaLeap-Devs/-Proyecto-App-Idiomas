package com.lingualeap.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────
//  TIPOGRAFÍA DINÁMICA
//  Se han eliminado los colores fijos para que
//  el sistema use automáticamente onBackground/onSurface
//  según el modo (Claro/Oscuro).
// ─────────────────────────────────────────

val LinguaTypography = Typography(

    // Títulos grandes (pantalla splash, headers)
    displayLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Bold,
        fontSize    = 36.sp,
        lineHeight  = 44.sp
    ),

    // Títulos de sección
    headlineMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 24.sp,
        lineHeight  = 32.sp
    ),

    // Subtítulos / nombres de lección
    titleMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 16.sp,
        lineHeight  = 24.sp
    ),

    // Cuerpo de texto principal
    bodyLarge = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 16.sp,
        lineHeight  = 24.sp
    ),

    // Texto secundario / descripciones
    bodyMedium = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 20.sp
    ),

    // Labels pequeños (nivel, duración, badges)
    labelSmall = TextStyle(
        fontFamily  = FontFamily.Default,
        fontWeight  = FontWeight.Medium,
        fontSize    = 11.sp,
        lineHeight  = 16.sp
    )
)
