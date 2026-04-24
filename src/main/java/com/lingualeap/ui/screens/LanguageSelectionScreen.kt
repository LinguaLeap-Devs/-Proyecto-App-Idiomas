package com.lingualeap.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lingualeap.data.model.AppData
import com.lingualeap.data.model.Language
import com.lingualeap.ui.components.LanguageCard
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.viewmodel.AuthViewModel

/**
 * PANTALLA: SELECCIÓN DE IDIOMA
 * 🟢 MEJORA DE EXCELENCIA: Sincronizada con los nuevos nombres de función en español del ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onLanguageSelected: () -> Unit
) {
    var selectedLang by remember { mutableStateOf<Language?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("¿Qué quieres aprender?", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Elige el idioma que quieres dominar hoy. Podrás cambiarlo más tarde.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(AppData.availableLanguages) { language ->
                    LanguageCard(
                        language = language,
                        isSelected = selectedLang == language,
                        onClick = { selectedLang = language }
                    )
                }
            }

            LinguaButton(
                text = "CONTINUAR",
                onClick = {
                    selectedLang?.let {
                        // 🛠️ CORRECCIÓN: Usando el nuevo nombre de función en español
                        viewModel.actualizarIdiomaUsuario(it)
                        onLanguageSelected()
                    }
                },
                enabled = selectedLang != null,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
    }
}
