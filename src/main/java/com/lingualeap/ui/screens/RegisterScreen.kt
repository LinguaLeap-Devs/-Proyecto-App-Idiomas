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
import com.lingualeap.viewmodel.AuthViewModel

/**
 * Pantalla de Registro - REVISIÓN CRÍTICA:
 * 1. Coherencia: Se alinea visualmente con LoginScreen usando el logo circular.
 * 2. UX: Se mejora la disposición de los campos para evitar fatiga visual.
 * 3. Feedback: Gestión de errores clara y amigable.
 */
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit // Este llevará ahora al Quiz
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
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

        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = LinguaColors.Primary
            )
        }

        // ── BRANDING ──────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(LinguaSpacing.Small))
            Text(
                text = "Crea tu cuenta",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = LinguaColors.TextPrimary
            )
            Text(
                text = "Únete a la comunidad de Glossa",
                fontSize = 14.sp,
                color = LinguaColors.TextSecondary
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        // ── CAMPOS DE ENTRADA ────────────────────
        LinguaTextField(
            value = name,
            onValueChange = { name = it; viewModel.clearError() },
            label = "Nombre completo",
            placeholder = "Tu nombre",
            errorMessage = if (errorMessage?.contains("nombre", true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
            value = email,
            onValueChange = { email = it; viewModel.clearError() },
            label = "Email",
            placeholder = "ejemplo@correo.com",
            keyboardType = KeyboardType.Email,
            errorMessage = if (errorMessage?.contains("correo", true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
            value = password,
            onValueChange = { password = it; viewModel.clearError() },
            label = "Contraseña",
            placeholder = "Mínimo 6 caracteres",
            isPassword = true,
            errorMessage = if (errorMessage?.contains("contraseña", true) == true) errorMessage else null
        )

        if (errorMessage != null && !errorMessage.contains("nombre", true) && 
            !errorMessage.contains("correo", true) && !errorMessage.contains("contraseña", true)) {
            Text(
                text = errorMessage,
                color = LinguaColors.Error,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = LinguaSpacing.Tiny)
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        // ── BOTÓN DE ACCIÓN ──────────────────────
        LinguaButton(
            text = "Crear cuenta",
            onClick = { viewModel.register(name.trim(), email.trim(), password) },
            isLoading = isLoading,
            enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        Text(
            text = "Al registrarte, aceptas nuestros Términos y Condiciones",
            fontSize = 11.sp,
            color = LinguaColors.TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LinguaSpacing.Large),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "¿Ya tienes cuenta? ", fontSize = 14.sp, color = LinguaColors.TextSecondary)
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Inicia sesión",
                    fontSize = 14.sp,
                    color = LinguaColors.Primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
