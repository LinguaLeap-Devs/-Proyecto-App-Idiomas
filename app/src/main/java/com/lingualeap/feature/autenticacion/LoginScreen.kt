package com.lingualeap.feature.autenticacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.R
import com.lingualeap.data.model.AuthState
import com.lingualeap.ui.components.*
import com.lingualeap.ui.theme.LinguaSpacing
import com.lingualeap.feature.autenticacion.AuthViewModel

@Composable
fun LoginScreen(
        viewModel: AuthViewModel,
        onNavigateToRegister: () -> Unit,
        onNavigateBack: () -> Unit,
        onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val authState by viewModel.estadoAuth.collectAsStateWithLifecycle()
    val isLoading = authState is AuthState.Loading

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    val errorMessage = (authState as? AuthState.Error)?.message

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = LinguaSpacing.ScreenPadding)
    ) {
        Spacer(Modifier.height(LinguaSpacing.Medium))

        IconButton(onClick = onNavigateBack) {
            Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription =
                            stringResource(id = R.string.login_back_content_description),
                    tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Small))

        // CABECERA REFINADA
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            Text(
                    text =
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                    append(stringResource(id = R.string.login_welcome))
                                }
                                append(stringResource(id = R.string.login_welcome_back))
                            },
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 44.sp,
                    letterSpacing = (-1.5).sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                    text = stringResource(id = R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Huge))

        LinguaTextField(
                value = email,
                onValueChange = {
                    email = it
                    viewModel.limpiarError()
                },
                label = stringResource(id = R.string.login_email_label),
                placeholder = stringResource(id = R.string.login_email_placeholder),
                keyboardType = KeyboardType.Email,
                errorMessage =
                        if (errorMessage?.contains("correo", true) == true) errorMessage else null
        )

        Spacer(Modifier.height(LinguaSpacing.Medium))

        LinguaTextField(
                value = password,
                onValueChange = {
                    password = it
                    viewModel.limpiarError()
                },
                label = stringResource(id = R.string.login_password_label),
                placeholder = stringResource(id = R.string.login_password_placeholder),
                isPassword = true,
                errorMessage =
                        if (errorMessage?.contains("contraseña", true) == true) errorMessage
                        else null
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = { /* TODO */}) {
                Text(
                        text = stringResource(id = R.string.login_forgot_password),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                )
            }
        }

        if (errorMessage != null &&
                        !errorMessage.contains("correo", true) &&
                        !errorMessage.contains("contraseña", true)
        ) {
            Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = LinguaSpacing.Tiny)
            )
        }

        Spacer(Modifier.height(LinguaSpacing.Large))

        LinguaButton(
                text = stringResource(id = R.string.login_button_enter),
                onClick = { viewModel.login(email.trim(), password) },
                isLoading = isLoading && email.isNotBlank(),
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        )

        if (isLoading && email.isBlank()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(Modifier.weight(1f))

        // FOOTER TIPO CÁPSULA "ALIÑADO"
        Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
        ) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                            Modifier.background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                            RoundedCornerShape(50.dp)
                                    )
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                        text = stringResource(id = R.string.login_new_user_question),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                        onClick = onNavigateToRegister,
                        contentPadding = PaddingValues(start = 8.dp)
                ) {
                    Text(
                            text = stringResource(id = R.string.login_create_account),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
