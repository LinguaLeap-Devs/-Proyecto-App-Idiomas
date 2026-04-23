package com.lingualeap.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.R
import com.lingualeap.data.model.AuthState
import com.lingualeap.ui.components.*
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.ui.theme.LinguaSpacing
import com.lingualeap.ui.theme.LinguaLeapTheme
import com.lingualeap.viewmodel.AuthViewModel
import androidx.compose.ui.tooling.preview.Preview

/**
 * Pantalla de inicio de sesión - CRÍTICA Y MEJORA:
 * 1. Integración de Marca: Se añade el logo circular de Glossa.
 * 2. Jerarquía Visual: Se ajustan los pesos tipográficos para mayor legibilidad.
 * 3. Diseño "Soft": Se alinea con el estilo amigable de la HomeScreen.
 */
@Composable
fun LoginScreen(
    viewModel           : AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateBack      : () -> Unit,
    onLoginSuccess      : () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    val errorMessage = (authState as? AuthState.Error)?.message
    val isLoading    = authState is AuthState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LinguaColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = LinguaSpacing.ScreenPadding)
    ) {
        Spacer(Modifier.height(LinguaSpacing.Medium))

        // Botón volver más integrado
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = LinguaColors.Primary
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Medium))

        // ── BRANDING (NUEVO) ──────────────────────
        // 🔄 CAMBIO: Se integra el logo para dar identidad a la pantalla
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(10.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(LinguaSpacing.Medium))
            Text(
                text = "Glossa",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LinguaColors.Primary,
                letterSpacing = (-1).sp
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        Text(
            text       = "¡Qué bueno\nverte de nuevo! 👋",
            fontSize   = 28.sp,
            fontWeight = FontWeight.Bold,
            color      = LinguaColors.TextPrimary,
            lineHeight = 36.sp
        )
        
        Spacer(Modifier.height(LinguaSpacing.Huge))

        // ── CAMPOS DE ENTRADA ────────────────────
        LinguaTextField(
            value         = email,
            onValueChange = {
                email = it
                viewModel.clearError()
            },
            label        = "Email",
            placeholder  = "ejemplo@correo.com",
            keyboardType = KeyboardType.Email,
            errorMessage = if (errorMessage?.contains("correo", ignoreCase = true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
            value         = password,
            onValueChange = {
                password = it
                viewModel.clearError()
            },
            label        = "Contraseña",
            placeholder  = "Tu clave secreta",
            isPassword   = true,
            errorMessage = if (errorMessage?.contains("contraseña", ignoreCase = true) == true) errorMessage else null
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { /* TODO */ }) {
                Text(
                    text     = "¿Olvidaste tu contraseña?",
                    fontSize = 13.sp,
                    color    = LinguaColors.Primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (errorMessage != null && !errorMessage.contains("correo", true) && !errorMessage.contains("contraseña", true)) {
            Text(
                text     = errorMessage,
                color    = LinguaColors.Error,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = LinguaSpacing.Tiny)
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        // ── BOTONES DE ACCIÓN ────────────────────
        LinguaButton(
            text      = "Entrar",
            onClick   = { viewModel.login(email.trim(), password) },
            isLoading = isLoading,
            enabled   = email.isNotBlank() && password.isNotBlank()
        )

        Spacer(Modifier.height(LinguaSpacing.Large))

        DividerWithText(text = "o usa tu cuenta social")

        Spacer(Modifier.height(LinguaSpacing.Medium))

        // 🔄 CAMBIO: Botón de Google más limpio (se quitó el emoji azul)
        LinguaOutlineButton(
            text    = "Continuar con Google",
            onClick = { /* TODO */ }
        )

        Spacer(Modifier.weight(1f)) // Empuja el registro hacia abajo

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LinguaSpacing.Large),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "¿Eres nuevo? ", fontSize = 14.sp, color = LinguaColors.TextSecondary)
            TextButton(
                onClick  = onNavigateToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Crea una cuenta", 
                    fontSize = 14.sp, 
                    color = LinguaColors.Primary, 
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LinguaLeapTheme {
        LoginScreen(
            viewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
            onNavigateToRegister = {},
            onNavigateBack = {},
            onLoginSuccess = {}
        )
    }
}
