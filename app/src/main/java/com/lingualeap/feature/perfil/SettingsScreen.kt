package com.lingualeap.feature.perfil

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.R
import com.lingualeap.feature.autenticacion.AuthViewModel
import kotlin.math.roundToInt

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
private val ErrorRed      = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val usuario by viewModel.usuarioActual.collectAsStateWithLifecycle()
    
    // Dialog States
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    
    var nuevoNombre by remember(usuario?.name) { mutableStateOf(usuario?.name ?: "") }
    var nuevaPassword by remember { mutableStateOf("") }

    // Logic for Dialogs (Reusing existing logic)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = CardBg,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Cerrar Sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que quieres abandonar la base?") },
            confirmButton = {
                TextButton(onClick = { 
                    showLogoutDialog = false
                    viewModel.cerrarSesion()
                    onLogoutSuccess() 
                }) { Text("Cerrar Sesión", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar", color = NeonCyan) }
            }
        )
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            containerColor = CardBg,
            title = { Text("Editar Perfil", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = nuevoNombre,
                    onValueChange = { nuevoNombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary,
                        cursorColor = NeonCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nuevoNombre.isNotBlank()) {
                        viewModel.actualizarNombre(nuevoNombre)
                        showEditProfileDialog = false
                    }
                }) { Text("Guardar", color = NeonCyan) }
            }
        )
    }

    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = { showChangePasswordDialog = false },
            containerColor = CardBg,
            title = { Text("Cambiar Contraseña", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = nuevaPassword,
                    onValueChange = { nuevaPassword = it },
                    label = { Text("Nueva Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedLabelColor = NeonCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (nuevaPassword.length >= 6) {
                        viewModel.cambiarContrasena(nuevaPassword)
                        showChangePasswordDialog = false
                        nuevaPassword = ""
                    }
                }) { Text("Actualizar", color = NeonCyan) }
            }
        )
    }

    if (showGoalDialog) {
        val goals = listOf(20, 50, 100, 150)
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            containerColor = CardBg,
            title = { Text("Meta Diaria de XP", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    goals.forEach { goal ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                viewModel.actualizarMetaDiaria(goal)
                                showGoalDialog = false
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (usuario?.dailyGoalXp == goal),
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = NeonCyan)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("$goal XP", color = TextPrimary)
                            if (goal == 50) {
                                Spacer(Modifier.weight(1f))
                                Text("RECOMENDADO", color = SunYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showGoalDialog = false }) { Text("Cerrar", color = NeonCyan) }
            }
        )
    }

    Scaffold(
        containerColor = SurfaceBg,
        topBar = {
            SettingsTopBar(onBack = onNavigateBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // App Brand Section
            AppBrandSection()

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Spacer(Modifier.height(22.dp))

                // CUENTA
                SettingsSectionLabel("Cuenta", NeonCyan)
                Spacer(Modifier.height(10.dp))
                SettingsCard {
                    AccountRow(emoji = "👤", title = "Usuario", subtitle = usuario?.name ?: "Nombre no definido") {
                        showEditProfileDialog = true
                    }
                    SettingsDivider()
                    AccountRow(emoji = "✉️", title = "Email", subtitle = usuario?.email ?: "Email no definido")
                    SettingsDivider()
                    AccountRow(emoji = "🔐", title = "Cambiar Contraseña", subtitle = null, tint = SunYellow) {
                        showChangePasswordDialog = true
                    }
                }

                Spacer(Modifier.height(22.dp))

                // NOTIFICACIONES
                SettingsSectionLabel("Notificaciones", NeonCyan)
                Spacer(Modifier.height(10.dp))
                SettingsCard {
                    ToggleRow("🔔", "Notificaciones Fuera de la App", NeonCyan.copy(alpha=0.12f), usuario?.notificationsEnabled ?: true) { 
                        viewModel.toggleNotificaciones(it) 
                    }
                    SettingsDivider()
                    ToggleRow("🔥", "Notificaciones de Rachas", CoralOrange.copy(0.15f), usuario?.streakNotificationsEnabled ?: true) {
                        viewModel.toggleStreakNotifications(it)
                    }
                    SettingsDivider()
                    ToggleRow("🔊", "Sonidos en la App", AccentBlue.copy(0.15f), usuario?.soundsEnabled ?: true) {
                        viewModel.toggleSounds(it)
                    }
                    SettingsDivider()
                    ToggleRow("📧", "Notificaciones de Gmail", Color(0xFF22C55E).copy(0.12f), usuario?.emailNotificationsEnabled ?: false) {
                        viewModel.toggleEmailNotifications(it)
                    }
                }

                Spacer(Modifier.height(22.dp))

                // CONFIGURACIÓN DE CHAT
                SettingsSectionLabel("Configuración de IA", NeonCyan)
                Spacer(Modifier.height(10.dp))
                SettingsCard {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Tamaño del texto", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${(usuario?.chatTextSize ?: 16f).roundToInt()} sp", color = TextSecondary, fontSize = 11.sp)
                        }
                        Slider(
                            value = usuario?.chatTextSize ?: 16f,
                            onValueChange = { viewModel.updateChatTextSize(it) },
                            valueRange = 12f..24f,
                            steps = 5,
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.08f)
                            )
                        )
                    }
                    SettingsDivider()
                    ToggleRow("💡", "Ideas de Chat IA", NeonCyan.copy(0.12f), usuario?.showAiChatIdeas ?: true) {
                        viewModel.toggleShowAiChatIdeas(it)
                    }
                }

                Spacer(Modifier.height(22.dp))

                // APRENDIZAJE
                SettingsSectionLabel("Aprendizaje y Estilo", NeonCyan)
                Spacer(Modifier.height(10.dp))
                SettingsCard {
                    AccountRow(emoji = "🌐", title = "Idioma a aprender", subtitle = usuario?.selectedLang?.name ?: "No seleccionado", tint = SunYellow) {
                        onNavigateToLanguage()
                    }
                    SettingsDivider()
                    AccountRow(emoji = "🎯", title = "Metas de Aprendizaje", subtitle = "${usuario?.dailyGoalXp ?: 50} XP diarios") {
                        showGoalDialog = true
                    }
                    SettingsDivider()
                    ToggleRow("🌙", "Modo Oscuro", Color(0xFF8B5CF6).copy(0.15f), usuario?.darkModeEnabled ?: false) {
                        viewModel.toggleDarkMode(it)
                    }
                }

                Spacer(Modifier.height(22.dp))

                // SOPORTE
                SettingsSectionLabel("Soporte", TextSecondary)
                Spacer(Modifier.height(10.dp))
                SettingsCard {
                    AccountRow(emoji = "❓", title = "Ayuda / FAQ", subtitle = null)
                    SettingsDivider()
                    AccountRow(emoji = "⚖️", title = "Términos y Condiciones", subtitle = null) {
                        onNavigateToTerms()
                    }
                }

                Spacer(Modifier.height(32.dp))

                // ZONA DE PELIGRO
                SettingsSectionLabel("Zona de Peligro", ErrorRed)
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { showLogoutDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = CardBg,
                    border = BorderStroke(1.dp, ErrorRed.copy(alpha = 0.2f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(ErrorRed.copy(alpha = 0.15f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Logout, null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Cerrar Sesión", color = ErrorRed, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Icon(Icons.Outlined.ChevronRight, null, tint = ErrorRed.copy(0.5f), modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(RoyalBlue, DeepNavy))).padding(horizontal = 18.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.1f), CircleShape).clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver", tint = TextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text("Ajustes", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun AppBrandSection() {
    Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(DeepNavy, SurfaceBg)))) {
        Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            drawCircle(NeonCyan.copy(alpha=0.07f), 130.dp.toPx(), Offset(size.width*0.9f, -20.dp.toPx()))
        }
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(20.dp),
                color = CardBg,
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(NeonCyan, AccentBlue))),
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_principal),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(14.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Text("LinguaLeap", color = NeonCyan, fontSize = 20.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Text("VERSIÓN 1.0.0", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsSectionLabel(text: String, color: Color) {
    Text(text.uppercase(), color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.2.sp)
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = CardBg,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.06f)),
        content = { Column(content = content) }
    )
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
}

@Composable
fun AccountRow(emoji: String, title: String, subtitle: String?, tint: Color = AccentBlue, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(enabled = onClick != null) { onClick?.invoke() }.padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(tint.copy(alpha = 0.15f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        if (onClick != null) Icon(Icons.Outlined.ChevronRight, null, tint = TextSecondary.copy(0.4f), modifier = Modifier.size(18.dp))
    }
}

@Composable
fun ToggleRow(emoji: String, title: String, iconBg: Color, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).background(iconBg, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = Color.White.copy(0.6f),
                uncheckedTrackColor = Color.White.copy(0.1f),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
