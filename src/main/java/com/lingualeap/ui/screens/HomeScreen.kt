package com.lingualeap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.AppData
import com.lingualeap.data.model.Lesson
import com.lingualeap.data.model.LessonLevel
import com.lingualeap.data.model.User
import com.lingualeap.ui.components.LessonNode
import com.lingualeap.ui.components.UserAvatar
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.viewmodel.AuthViewModel

/**
 * PANTALLA PRINCIPAL (HomeScreen)
 * Esta es la pantalla central de la aplicación después de iniciar sesión.
 * Funciona como un contenedor que cambia entre 4 secciones: Inicio, Repaso, Progreso y Perfil.
 */
@Composable
fun HomeScreen(
    viewModel    : AuthViewModel,      // El "cerebro" que maneja los datos del usuario
    onStartLesson: (Lesson) -> Unit,   // Acción que ocurre al tocar una lección (navegar a la clase)
    onLogout     : () -> Unit          // Acción para cerrar sesión y volver al inicio
) {
    // Obtenemos los datos del usuario logueado en tiempo real
    val usuarioActual by viewModel.currentUser.collectAsStateWithLifecycle()
    
    // Estado para saber qué pestaña de la barra inferior está seleccionada (0 a 3)
    var seccionActiva by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Color(0xFFF8FAFC), // Color de fondo gris muy claro (estilo moderno)
        bottomBar = {
            // Barra de navegación inferior
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                val menuTabs = listOf(
                    MenuTab("Inicio", Icons.Rounded.Home, 0),
                    MenuTab("Repaso", Icons.Rounded.AutoStories, 1),
                    MenuTab("Progreso", Icons.Rounded.BarChart, 2),
                    MenuTab("Perfil", Icons.Rounded.Person, 3)
                )
                menuTabs.forEach { tab ->
                    NavigationBarItem(
                        selected = seccionActiva == tab.id,
                        onClick = { seccionActiva = tab.id },
                        icon = { Icon(tab.icon, contentDescription = tab.titulo) },
                        label = { Text(tab.titulo, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LinguaColors.Primary,
                            indicatorColor = LinguaColors.Primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        // Contenedor principal que cambia el contenido según la pestaña seleccionada
        Column(modifier = Modifier.padding(padding)) {
            when (seccionActiva) {
                0 -> VistaInicio(usuarioActual, onStartLesson)    // El mapa de lecciones
                1 -> VistaRepaso()                                // Biblioteca y vocabulario
                2 -> VistaProgreso(usuarioActual?.totalXp ?: 0)   // Gráficas de estudio
                3 -> VistaPerfil(usuarioActual, onLogout)         // Datos del usuario y medallas
            }
        }
    }
}

/**
 * SECCIÓN 0: INICIO (Mapa de Lecciones)
 * Muestra la ruta de aprendizaje con nodos (círculos) para cada lección.
 */
@Composable
private fun VistaInicio(usuario: User?, onStart: (Lesson) -> Unit) {
    // Filtramos las lecciones según el idioma que el usuario eligió (por defecto inglés "en")
    val lecciones = remember(usuario?.selectedLang) { 
        AppData.getLessonsForLanguage(usuario?.selectedLang?.code ?: "en") 
    }
    
    LazyColumn(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            // Cabecera con nombre y barra de meta diaria
            HeaderConMetaDiaria(
                nombre = usuario?.name ?: "",
                iniciales = usuario?.avatarInitials ?: "?",
                xpActual = (usuario?.totalXp ?: 0) % 50, // XP calculado dinámicamente
                metaXP = 50,   // Meta diaria fija de 50 XP
                bandera = usuario?.selectedLang?.flag ?: "🌍"
            )
            Spacer(Modifier.height(24.dp))
        }
        
        // Agrupamos las lecciones por nivel (Principiante, Elemental, etc.)
        val gruposPorNivel = lecciones.groupBy { it.level }
        
        gruposPorNivel.forEach { (nivel, listaDeLecciones) ->
            // Título del nivel (ejemplo: BEGINNER)
            item { 
                Text(
                    text = nivel.displayName.uppercase(), 
                    modifier = Modifier.padding(vertical = 16.dp), 
                    style = MaterialTheme.typography.labelLarge, 
                    color = Color.Gray, 
                    letterSpacing = 2.sp
                ) 
            }
            
            // Dibujamos cada lección con un efecto de "zigzag" (bias) para que parezca un camino
            itemsIndexed(listaDeLecciones) { index, leccion ->
                val desplazamientoX = when (index % 4) { 0 -> 0f; 1 -> -0.4f; 2 -> 0f; 3 -> 0.4f; else -> 0f }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LessonNode(
                        lesson = leccion, 
                        onClick = { onStart(leccion) }, 
                        modifier = Modifier.offset(x = (desplazamientoX * 160).dp).padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

/**
 * SECCIÓN 1: REPASO (Biblioteca)
 * Aquí el usuario puede ver palabras aprendidas y categorías especiales.
 */
@Composable
private fun VistaRepaso() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), 
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { 
            Spacer(Modifier.height(24.dp))
            Text("Tu Biblioteca", fontSize = 28.sp, fontWeight = FontWeight.Black) 
        }
        
        // Tarjeta de la "Palabra del día"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(24.dp), 
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(Color(0xFFFFF7ED), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Lightbulb, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Palabra del día", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Adventure", fontSize = 26.sp, fontWeight = FontWeight.Black, color = LinguaColors.Primary)
                    Text("Una experiencia emocionante o audaz.", fontSize = 15.sp, color = Color.DarkGray)
                }
            }
        }

        // Fila horizontal de categorías de vocabulario
        item {
            Text("Categorías", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            val categorias = listOf("Comida" to "🍕", "Viajes" to "✈️", "Salud" to "🏥", "Trabajo" to "💼")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categorias) { (nombre, emoji) ->
                    Column(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(12.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(emoji, fontSize = 24.sp)
                        Text(nombre, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * SECCIÓN 2: PROGRESO (Estadísticas)
 * Muestra visualmente cuánto ha estudiado el usuario en la semana.
 */
@Composable
private fun VistaProgreso(xpTotalAcumulada: Int) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        item {
            Text("Tu Progreso", fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(24.dp))
            
            // Gráfica de barras sencilla (Actividad Semanal)
            Text("Actividad Semanal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.Bottom
            ) {
                val valoresdebarras = listOf(0.4f, 0.8f, 0.2f, 0.9f, 0.5f, 0.1f, 0.3f) // Representa porcentaje de meta cumplida
                val diasSemana = listOf("L", "M", "X", "J", "V", "S", "D")
                
                diasSemana.forEachIndexed { index, dia ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // La barra de color
                        Box(
                            modifier = Modifier
                                .width(28.dp)
                                .fillMaxHeight(valoresdebarras[index])
                                .clip(RoundedCornerShape(6.dp))
                                .background(if(valoresdebarras[index] > 0.6f) LinguaColors.Primary else Color(0xFFE2E8F0))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(dia, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Tarjeta de XP Total
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Bolt, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column { 
                        Text("Total acumulado", fontSize = 13.sp, color = Color.Gray)
                        Text("$xpTotalAcumulada XP", fontSize = 22.sp, fontWeight = FontWeight.Bold) 
                    }
                }
            }
        }
    }
}

/**
 * SECCIÓN 3: PERFIL (Usuario y Logros)
 * Muestra la información personal, medallas obtenidas y opción de salir.
 */
@Composable
private fun VistaPerfil(usuario: User?, onLogout: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp), 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(32.dp))
            UserAvatar(initials = usuario?.avatarInitials ?: "?", size = 100)
            Spacer(Modifier.height(16.dp))
            Text(text = usuario?.name ?: "Usuario", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text(text = usuario?.email ?: "", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(32.dp))
        }
        
        // Cuadro de estadísticas: Racha, XP Idioma actual
        item {
            Card(
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(24.dp), 
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    PerfilStatItem("🔥", "${usuario?.streakDays ?: 0}", "Días")
                    PerfilStatItem("⚡", "${usuario?.totalXp ?: 0}", "XP")
                    PerfilStatItem(usuario?.selectedLang?.flag ?: "🌍", usuario?.selectedLang?.name ?: "—", "Idioma")
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // Sección de medallas (Logros)
        item {
            Row(modifier = Modifier.fillMaxWidth()) { Text("Mis Medallas", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val misMedallas = listOf("🌱", "🔥", "🎓", "🏆")
                misMedallas.forEach { emojiMedalla ->
                    Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White), 
                        contentAlignment = Alignment.Center
                    ) {
                        Text(emojiMedalla, fontSize = 24.sp)
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        // Botón de Cerrar Sesión
        item {
            Button(
                onClick = onLogout, 
                modifier = Modifier.fillMaxWidth().height(56.dp), 
                shape = RoundedCornerShape(16.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red)
            ) {
                Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                Spacer(Modifier.width(12.dp))
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── COMPONENTES INTERNOS DE APOYO (Solo se usan en este archivo) ─────────

/**
 * El encabezado azul/blanco que muestra el progreso del día.
 */
@Composable
private fun HeaderConMetaDiaria(nombre: String, iniciales: String, xpActual: Int, metaXP: Int, bandera: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserAvatar(initials = iniciales, size = 52)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = "¡Hola, ${nombre.split(" ").first()}!", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Meta: $xpActual / $metaXP XP", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(Modifier.weight(1f))
            Text(bandera, fontSize = 28.sp)
        }
        Spacer(Modifier.height(16.dp))
        // Barra de progreso física
        LinearProgressIndicator(
            progress = { xpActual.toFloat() / metaXP.toFloat() }, 
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape), 
            color = LinguaColors.Primary, 
            trackColor = Color(0xFFF1F5F9)
        )
    }
}

/**
 * Un item pequeño para las estadísticas del perfil (Icono + Valor + Texto).
 */
@Composable
private fun PerfilStatItem(emoji: String, valor: String, etiqueta: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Text(text = valor, fontSize = 18.sp, fontWeight = FontWeight.Black)
        Text(text = etiqueta, fontSize = 12.sp, color = Color.Gray)
    }
}

/**
 * Clase de datos para definir los elementos del menú inferior.
 */
private data class MenuTab(val titulo: String, val icon: ImageVector, val id: Int)
