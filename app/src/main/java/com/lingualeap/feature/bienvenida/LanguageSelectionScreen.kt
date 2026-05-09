package com.lingualeap.feature.bienvenida

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.theme.LinguaColors
import com.lingualeap.feature.autenticacion.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val usuario by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    
    val interfaceLanguages = listOf(
        "Español" to "🇪🇸",
        "English" to "🇺🇸",
        "Português" to "🇧🇷",
        "Français" to "🇫🇷"
    )
    
    var selectedLang by remember { mutableStateOf(usuario?.nativeLang ?: "Español") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Idioma de la App", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LinguaColors.Background)
            )
        },
        containerColor = LinguaColors.Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Selecciona el idioma en el que deseas ver la interfaz de la aplicación.",
                fontSize = 16.sp,
                color = LinguaColors.TextSecondary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(interfaceLanguages) { (name, flag) ->
                    LanguageInterfaceCard(
                        name = name,
                        flag = flag,
                        isSelected = selectedLang == name,
                        onClick = { selectedLang = name }
                    )
                }
            }

            LinguaButton(
                text = "GUARDAR CAMBIOS",
                onClick = {
                    authViewModel.actualizarIdiomaNativo(selectedLang)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}

@Composable
fun LanguageInterfaceCard(
    name: String,
    flag: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) LinguaColors.Primary.copy(alpha = 0.1f) else LinguaColors.Surface,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) LinguaColors.Primary else LinguaColors.Border.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(flag, fontSize = 24.sp)
            Spacer(Modifier.width(16.dp))
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) LinguaColors.Primary else LinguaColors.TextPrimary
            )
            Spacer(Modifier.weight(1f))
            RadioButton(
                selected = isSelected, 
                onClick = null, 
                colors = RadioButtonDefaults.colors(selectedColor = LinguaColors.Primary)
            )
        }
    }
}
