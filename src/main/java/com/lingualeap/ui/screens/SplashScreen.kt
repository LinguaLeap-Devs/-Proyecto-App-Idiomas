package com.lingualeap.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.R
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.components.LinguaOutlineButton
import com.lingualeap.ui.theme.LinguaColors
import kotlinx.coroutines.delay

// ─────────────────────────────────────────
//  PANTALLA: SPLASH / BIENVENIDA (REDiseñada)
//
//  🔄 ACTUALIZACIÓN CRÍTICA DE DISEÑO:
//  Se ha rediseñado el área visual para eliminar el efecto "parche"
//  cuadrado, integrando el logo de forma circular y premium.
// ─────────────────────────────────────────

private const val APP_NAME   = "Glossa"
private const val APP_SLOGAN = "Aprende un idioma nuevo,\nuna lección a la vez."

@Composable
fun SplashScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin   : () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "logo_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF283593), // Indigo profundo para mejor contraste
                        Color(0xFF3F51B5),
                        Color(0xFF5C6BC0)
                    )
                )
            )
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        // ── LOGO CON CORRECCIÓN ESTÉTICA ──────────
        // 🔄 CAMBIO DOCUMENTADO:
        // Se aplica CircleShape y shadow para integrar el logo a la UI.
        // Esto elimina las esquinas negras de la imagen y le da profundidad.
        Box(
            modifier = Modifier
                .scale(logoScale)
                .size(170.dp)
                .shadow(elevation = 30.dp, shape = CircleShape)
                .border(2.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .background(Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de la aplicación",
                contentScale = ContentScale.Crop, // Forzamos que la imagen llene el círculo
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(40.dp))

        // ── NOMBRE DE LA APP ──────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn() + slideInVertically()
        ) {
            Text(
                text       = APP_NAME,
                fontSize   = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                letterSpacing = (-1.5).sp
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── SLOGAN ────────────────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(animationSpec = tween(delayMillis = 150)) + slideInVertically()
        ) {
            Text(
                text      = APP_SLOGAN,
                fontSize  = 16.sp,
                color     = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Spacer(Modifier.height(32.dp))

        // ── INDICADORES DE IDIOMAS ────────────────
        AnimatedVisibility(visible = visible, enter = fadeIn(tween(delayMillis = 300))) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("🇺🇸", "🇫🇷", "🇧🇷", "🇩🇪", "🇯🇵").forEach { flag ->
                    Text(text = flag, fontSize = 28.sp)
                }
            }
        }

        Spacer(Modifier.weight(1.2f))

        // ── BOTONES DE ACCIÓN ─────────────────────
        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(delayMillis = 400)) + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinguaButton(
                    text    = "Empezar gratis",
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )

                Spacer(Modifier.height(16.dp))

                androidx.compose.material3.TextButton(
                    onClick  = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = "YA TENGO CUENTA",
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
