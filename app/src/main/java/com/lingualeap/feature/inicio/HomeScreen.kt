package com.lingualeap.feature.inicio

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.Lesson
import com.lingualeap.feature.iaconsultas.*
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.feature.lecciones.LessonsViewModel
import com.lingualeap.feature.perfil.ProfileScreen
import androidx.compose.ui.text.font.FontWeight

// Colores del tema Galaxy para la navegación
private val SurfaceBg     = Color(0xFF0A1628)
private val CardBg        = Color(0xFF1E2D4F)
private val NeonCyan      = Color(0xFF00D4FF)
private val TextSecondary = Color(0xFF94A3B8)

data class MenuTab(val titulo: String, val icon: ImageVector, val id: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AuthViewModel,
    lessonsViewModel: LessonsViewModel,
    aiViewModel: AIViewModel,
    onStartLesson: (Lesson) -> Unit,
    onStartRepaso: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToViajes: () -> Unit,
    onNavigateToComida: () -> Unit,
    onNavigateToCultura: () -> Unit,
    onNavigateToNegocios: () -> Unit,
    onNavigateToHogar: () -> Unit,
    onNavigateToTransporte: () -> Unit,
    onNavigateToRopa: () -> Unit,
    onNavigateToFrases: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    val lecciones by lessonsViewModel.lecciones.collectAsStateWithLifecycle()
    var seccionActiva by remember { mutableIntStateOf(0) }
    var mostrarConsultasIA by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SurfaceBg, // Fondo oscuro Galaxy
        floatingActionButton = {
            if (seccionActiva == 0) {
                FloatingActionButton(
                    onClick = { mostrarConsultasIA = true },
                    containerColor = NeonCyan,
                    contentColor = SurfaceBg,
                    shape = CircleShape
                ) { Icon(Icons.Rounded.AutoAwesome, "Consultar IA") }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = CardBg,
                tonalElevation = 0.dp,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                val menuTabs = listOf(
                    MenuTab("Inicio", Icons.Rounded.Home, 0),
                    MenuTab("Repaso", Icons.Rounded.AutoStories, 1),
                    MenuTab("Ranking", Icons.Rounded.EmojiEvents, 2),
                    MenuTab("Perfil", Icons.Rounded.Person, 3)
                )
                menuTabs.forEach { tab ->
                    val selected = seccionActiva == tab.id
                    NavigationBarItem(
                        selected = selected,
                        onClick = { seccionActiva = tab.id },
                        icon = { Icon(tab.icon, contentDescription = tab.titulo) },
                        label = {
                            Text(
                                text = tab.titulo,
                                fontSize = 11.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = NeonCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (seccionActiva) {
                0 -> VistaInicio(usuarioActual, lecciones, onNavigateToSettings, onStartLesson)
                1 -> VistaRepaso(
                    usuario = usuarioActual,
                    onStart = onStartRepaso,
                    onFavorites = onNavigateToFavorites,
                    onViajes = onNavigateToViajes,
                    onComida = onNavigateToComida,
                    onCultura = onNavigateToCultura,
                    onNegocios = onNavigateToNegocios,
                    onHogar = onNavigateToHogar,
                    onTransporte = onNavigateToTransporte,
                    onRopa = onNavigateToRopa,
                    onFrases = onNavigateToFrases
                )
                2 -> VistaRanking(viewModel)
                3 -> ProfileScreen(usuarioActual, onNavigateToSettings, onLogout)
            }
        }

        if (mostrarConsultasIA) {
            ModalBottomSheet(
                onDismissRequest = { mostrarConsultasIA = false },
                sheetState = rememberModalBottomSheetState(),
                containerColor = CardBg,
                contentColor = Color.White
            ) { PanelConsultasBrevesIA(aiViewModel) }
        }
    }
}
