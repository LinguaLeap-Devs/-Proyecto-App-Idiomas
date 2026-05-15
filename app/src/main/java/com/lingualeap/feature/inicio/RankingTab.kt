package com.lingualeap.feature.inicio

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.User
import com.lingualeap.feature.autenticacion.AuthViewModel

// ─── Colors Galaxy ────────────────────────────────────────────────────────────
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
private val BronzeColor   = Color(0xFFCD7F32)
private val SilverColor   = Color(0xFF94A3B8)

enum class RankingFilter { WEEK, MONTH, ALL_TIME }
enum class RankingScope { GLOBAL, FRIENDS }

@Composable
fun VistaRanking(viewModel: AuthViewModel) {
    val usersRanking by viewModel.rankingUsuarios.collectAsStateWithLifecycle()
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(RankingFilter.WEEK) }
    var selectedScope by remember { mutableStateOf(RankingScope.GLOBAL) }
    var showSearchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.cargarRanking() }

    val displayedRanking = remember(usersRanking, selectedScope, usuarioActual) {
        if (selectedScope == RankingScope.GLOBAL) {
            usersRanking
        } else {
            usersRanking.filter { user ->
                user.id == usuarioActual?.id || usuarioActual?.friendsIds?.contains(user.id) == true
            }
        }
    }

    val top3 = displayedRanking.take(3)
    val maxXp = displayedRanking.firstOrNull()?.totalXp ?: 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Header + Tabs
        RankingHeader(
            selectedTab = selectedFilter,
            onTabSelected = { selectedFilter = it },
            selectedScope = selectedScope,
            onScopeSelected = { selectedScope = it }
        )

        // Podium Section
        if (top3.isNotEmpty()) {
            PodiumSection(top3 = top3, currentUserId = usuarioActual?.id)
        } else {
            Box(Modifier.height(220.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NeonCyan)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Full leaderboard
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text          = "CLASIFICACIÓN COMPLETA",
                color         = TextSecondary,
                fontSize      = 11.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))

            displayedRanking.forEachIndexed { index, user ->
                LeaderboardRow(
                    rank = index + 1,
                    user = user,
                    maxXp = maxXp,
                    isMe = user.id == usuarioActual?.id
                )
                Spacer(Modifier.height(8.dp))
            }

            // Placeholder si hay pocos usuarios
            if (displayedRanking.size < 4) {
                LeaderboardPlaceholderRow(
                    rank = displayedRanking.size + 1,
                    onInviteClick = { showSearchDialog = true }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Motivational banner
            if (usuarioActual != null) {
                val myRank = displayedRanking.indexOfFirst { it.id == usuarioActual?.id } + 1
                if (myRank > 0) {
                    MotivationalBanner(rank = myRank)
                }
            }

        Spacer(Modifier.height(30.dp))
        }
    }

    if (showSearchDialog) {
        SearchFriendDialog(
            viewModel = viewModel,
            onDismiss = { showSearchDialog = false }
        )
    }
}

@Composable
fun RankingHeader(
    selectedTab: RankingFilter,
    onTabSelected: (RankingFilter) -> Unit,
    selectedScope: RankingScope,
    onScopeSelected: (RankingScope) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(RoyalBlue, DeepNavy)))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
            drawCircle(
                color  = SunYellow.copy(alpha = 0.10f),
                radius = 150.dp.toPx(),
                center = Offset(size.width * 0.85f, -30.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 24.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text("🏆 Ranking", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Semana actual", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 3.dp))
                }

                Box(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(listOf(BronzeColor.copy(alpha = 0.3f), Color(0xFF92400E).copy(alpha = 0.4f))),
                            RoundedCornerShape(20.dp)
                        )
                        .border(1.dp, BronzeColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text("🥉 Liga Bronce", color = BronzeColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // -- Pestañas Global / Amigos --
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(3.dp)
            ) {
                listOf(
                    RankingScope.GLOBAL to "🌎 Global",
                    RankingScope.FRIENDS to "👥 Mis Amigos"
                ).forEach { (scope, label) ->
                    val isSelected = selectedScope == scope
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SunYellow.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { onScopeSelected(scope) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (isSelected) SunYellow else TextSecondary,
                            fontSize   = 14.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // -- Filtros de Tiempo --
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(3.dp)
            ) {
                listOf(
                    RankingFilter.WEEK     to "Esta semana",
                    RankingFilter.MONTH    to "Este mes",
                    RankingFilter.ALL_TIME to "Histórico"
                ).forEach { (tab, label) ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonCyan.copy(alpha = 0.18f) else Color.Transparent)
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text       = label,
                            color      = if (isSelected) NeonCyan else TextSecondary,
                            fontSize   = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumSection(top3: List<User>, currentUserId: String?) {
    val crownOffset by rememberInfiniteTransition(label = "crown")
        .animateFloat(
            initialValue  = 0f,
            targetValue   = -6f,
            animationSpec = infiniteRepeatable(tween(1800, easing = EaseInOutSine), RepeatMode.Reverse),
            label         = "crown_y"
        )

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.Bottom
    ) {
        // 2nd place
        if (top3.size >= 2) {
            PodiumColumn(
                user         = top3[1],
                rank         = 2,
                blockHeight  = 64.dp,
                blockColor   = Brush.verticalGradient(listOf(SilverColor, Color(0xFF64748B))),
                avatarSize   = 52.dp,
                avatarFontSz = 17.sp,
                isMe         = top3[1].id == currentUserId,
                modifier     = Modifier.weight(1f)
            )
        }

        // 1st place
        if (top3.isNotEmpty()) {
            PodiumColumn(
                user         = top3[0],
                rank         = 1,
                blockHeight  = 88.dp,
                blockColor   = Brush.verticalGradient(listOf(SunYellow, Color(0xFFD97706))),
                avatarSize   = 64.dp,
                avatarFontSz = 20.sp,
                showCrown    = true,
                crownOffset  = crownOffset,
                isMe         = top3[0].id == currentUserId,
                modifier     = Modifier.weight(1f)
            )
        }

        // 3rd place
        if (top3.size >= 3) {
            PodiumColumn(
                user         = top3[2],
                rank         = 3,
                blockHeight  = 50.dp,
                blockColor   = Brush.verticalGradient(listOf(BronzeColor, Color(0xFF92400E))),
                avatarSize   = 48.dp,
                avatarFontSz = 15.sp,
                isMe         = top3[2].id == currentUserId,
                modifier     = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun PodiumColumn(
    user: User,
    rank: Int,
    blockHeight: Dp,
    blockColor: Brush,
    avatarSize: Dp,
    avatarFontSz: TextUnit,
    modifier: Modifier = Modifier,
    showCrown: Boolean = false,
    crownOffset: Float = 0f,
    isMe: Boolean = false
) {
    val borderColor = when(rank) {
        1 -> SunYellow
        2 -> SilverColor
        else -> BronzeColor
    }

    Column(
        modifier              = modifier,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        if (showCrown) {
            Text(text = "👑", fontSize = 22.sp, modifier = Modifier.offset(y = crownOffset.dp))
            Spacer(Modifier.height(4.dp))
        } else {
            Spacer(Modifier.height(30.dp))
        }

        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.size(avatarSize)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF94A3B8))), CircleShape)
                    .border(width = if (rank == 1) 3.dp else 2.dp, color = borderColor, shape = CircleShape)
                    .shadow(if (isMe) 12.dp else 0.dp, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.avatarInitials, color = Color.White, fontSize = avatarFontSz, fontWeight = FontWeight.ExtraBold)
            }
            if (isMe) {
                Box(
                    modifier = Modifier
                        .background(SunYellow, RoundedCornerShape(6.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("TÚ", color = DeepNavy, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(Modifier.height(7.dp))
        Text(
            user.name.split(" ").firstOrNull() ?: "",
            color      = if (rank == 1) SunYellow else TextPrimary,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
        Text("${user.totalXp} XP", color = if (rank == 1) SunYellow else TextSecondary, fontSize = 11.sp)

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(blockHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(blockColor),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", color = Color.White.copy(alpha = 0.9f), fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun LeaderboardRow(
    rank: Int,
    user: User,
    maxXp: Int,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue   = if (maxXp > 0) user.totalXp.toFloat() / maxXp else 0f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label         = "lb_progress"
    )

    val containerBg = if (isMe) NeonCyan.copy(alpha = 0.08f) else CardBg
    val containerBorder = if (isMe) NeonCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(containerBg)
            .border(1.dp, containerBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = "$rank",
            color      = if (rank == 1) SunYellow else TextSecondary,
            fontSize   = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier   = Modifier.width(22.dp)
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier.size(36.dp).background(Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF94A3B8))), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(user.avatarInitials, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(user.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (isMe) {
                    Spacer(Modifier.width(6.dp))
                    Text("· Tú", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(5.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(3.dp)).background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(RoundedCornerShape(3.dp))
                        .background(Brush.horizontalGradient(if (isMe) listOf(SunYellow, CoralOrange) else listOf(AccentBlue, NeonCyan)))
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Text("${user.totalXp} XP", color = if (rank == 1) SunYellow else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun LeaderboardPlaceholderRow(
    rank: Int, 
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg.copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(14.dp))
            .clickable {
                onInviteClick()
            }
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$rank", color = TextSecondary.copy(alpha = 0.5f), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(22.dp))
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.size(36.dp).background(Color(0xFF334155), CircleShape), contentAlignment = Alignment.Center) {
            Text("?", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Text("Invita a un amigo", color = TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("— XP", color = Color(0xFF475569), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MotivationalBanner(rank: Int, modifier: Modifier = Modifier) {
    val message = when(rank) {
        1 -> "¡Vas en primer lugar! 🎯" to "Mantén tu racha para conservar el #1 esta semana."
        in 2..3 -> "¡Estás en el podio! 🏆" to "¡Sigue así para alcanzar el primer puesto!"
        else -> "¡Sigue practicando! ⚡" to "Estás en el puesto #$rank. ¡Tú puedes subir más!"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(SunYellow.copy(alpha = 0.12f), CoralOrange.copy(alpha = 0.12f))))
            .border(1.dp, SunYellow.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(if (rank <= 3) "🎯" else "⚡", fontSize = 26.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(message.first, color = SunYellow, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            Text(message.second, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
        }
    }
}

@Composable
fun SearchFriendDialog(
    viewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val searchResults by viewModel.resultadosBusqueda.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    val usuarioActual by viewModel.usuarioActual.collectAsStateWithLifecycle()

    androidx.compose.ui.window.Dialog(onDismissRequest = {
        viewModel.limpiarBusqueda()
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceBg)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text("Buscar amigos", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.buscarUsuarios(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Escribe un nombre...", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = NeonCyan
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                    Text("No se encontraron usuarios", color = TextSecondary, fontSize = 14.sp)
                }

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(searchResults.size) { index ->
                        val user = searchResults[index]
                        val isFriend = usuarioActual?.friendsIds?.contains(user.id) == true
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardBg)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(40.dp).background(Color(0xFF475569), CircleShape), contentAlignment = Alignment.Center) {
                                Text(user.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                                Text("${user.totalXp} XP", color = NeonCyan, fontSize = 12.sp)
                            }
                            
                            if (isFriend) {
                                Text("Añadido", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = { viewModel.agregarAmigo(user.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Añadir", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                TextButton(
                    onClick = { 
                        viewModel.limpiarBusqueda()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cerrar", color = NeonCyan)
                }
            }
        }
    }
}
