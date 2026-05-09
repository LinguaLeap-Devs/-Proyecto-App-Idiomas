package com.lingualeap.feature.autenticacion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.R
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.theme.LinguaColors
import kotlinx.coroutines.delay

private const val APP_NAME   = "LinguaLeap"
private const val APP_SLOGAN = "Domina idiomas con la fuerza\nde un espartano."

@Suppress("DEPRECATION")
@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin   : () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val googleSignInOptions = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, googleSignInOptions) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                viewModel.loginConGoogle(idToken)
            }
        } catch (e: ApiException) {
            android.util.Log.e("SplashScreen", "Error Google Sign In", e)
            viewModel.mostrarErrorManual("Error de Google: ${e.statusCode}")
        }
    }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "logo_scale"
    )

    // Usamos los colores del esquema para el fondo si queremos que cambie, 
    // o mantenemos los de marca para consistencia. Aquí usaremos una mezcla 
    // que respete la identidad visual pero sea consciente del tema.
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            LinguaColors.PrimaryDark,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.weight(1f))

        // -- NUEVO LOGO ESPARTANO ----------
        Box(
            modifier = Modifier
                .scale(logoScale)
                .size(170.dp)
                .shadow(elevation = 30.dp, shape = CircleShape)
                .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_principal),
                contentDescription = "Logo LinguaLeap",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(40.dp))

        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn() + slideInVertically()
        ) {
            Text(
                text       = APP_NAME,
                style      = MaterialTheme.typography.displayLarge,
                fontSize   = 44.sp, // Ajuste manual para el impacto visual de la Splash
                fontWeight = FontWeight.Black,
                color      = Color.White,
                letterSpacing = (-1.5).sp
            )
        }

        Spacer(Modifier.height(8.dp))

        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(animationSpec = tween(delayMillis = 150)) + slideInVertically()
        ) {
            Text(
                text      = APP_SLOGAN,
                style     = MaterialTheme.typography.bodyLarge,
                color     = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Spacer(Modifier.height(32.dp))

        AnimatedVisibility(visible = visible, enter = fadeIn(tween(delayMillis = 300))) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("🇺🇸", "🇫🇷", "🇧🇷", "🇩🇪", "🇯🇵").forEach { flag ->
                    Text(text = flag, fontSize = 28.sp)
                }
            }
        }

        Spacer(Modifier.weight(1.2f))

        AnimatedVisibility(
            visible = visible,
            enter   = fadeIn(tween(delayMillis = 400)) + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinguaButton(
                    text    = "CREAR CUENTA",
                    onClick = onNavigateToRegister,
                    containerColor = Color.White, 
                    contentColor = LinguaColors.PrimaryDark, // Texto azul para que se vea
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                )

                Spacer(Modifier.height(16.dp))

                LinguaButton(
                    text    = "G  INICIAR CON GOOGLE",
                    onClick = { launcher.launch(googleSignInClient.signInIntent) },
                    containerColor = Color.White,
                    contentColor = Color.DarkGray,
                    modifier = Modifier.fillMaxWidth().height(58.dp)
                )

                Spacer(Modifier.height(16.dp))

                androidx.compose.material3.TextButton(
                    onClick  = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text       = "YA TENGO UNA CUENTA",
                        color      = Color.White,
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}
