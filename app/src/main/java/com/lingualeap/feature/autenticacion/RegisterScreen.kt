package com.lingualeap.feature.autenticacion

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
import androidx.compose.ui.res.stringResource
import com.lingualeap.R
import com.lingualeap.data.model.AuthState
import com.lingualeap.ui.components.*
import com.lingualeap.ui.theme.LinguaSpacing
import com.lingualeap.feature.autenticacion.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.estadoAuth.collectAsStateWithLifecycle()

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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = LinguaSpacing.ScreenPadding)
    ) {
        Spacer(Modifier.height(LinguaSpacing.Medium))

        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.login_back_content_description),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_principal),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(LinguaSpacing.Medium))
            Text(
                text = stringResource(id = R.string.register_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(id = R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        LinguaTextField(
            value = name,
            onValueChange = { name = it; viewModel.limpiarError() },
            label = stringResource(id = R.string.register_name_label),
            placeholder = stringResource(id = R.string.register_name_placeholder),
            errorMessage = if (errorMessage?.contains("nombre", true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
            value = email,
            onValueChange = { email = it; viewModel.limpiarError() },
            label = stringResource(id = R.string.login_email_label),
            placeholder = stringResource(id = R.string.login_email_placeholder),
            keyboardType = KeyboardType.Email,
            errorMessage = if (errorMessage?.contains("correo", true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
            value = password,
            onValueChange = { password = it; viewModel.limpiarError() },
            label = stringResource(id = R.string.login_password_label),
            placeholder = stringResource(id = R.string.register_password_placeholder),
            isPassword = true,
            errorMessage = if (errorMessage?.contains("contraseña", true) == true) errorMessage else null
        )

        if (errorMessage != null && !errorMessage.contains("nombre", true) && 
            !errorMessage.contains("correo", true) && !errorMessage.contains("contraseña", true)) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = LinguaSpacing.Tiny)
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        LinguaButton(
            text = stringResource(id = R.string.register_button_create),
            onClick = { viewModel.registrar(name.trim(), email.trim(), password) },
            isLoading = isLoading,
            enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        Text(
            text = stringResource(id = R.string.register_terms_agreement),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            Text(
                text = stringResource(id = R.string.register_has_account_question), 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.register_login_action),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
