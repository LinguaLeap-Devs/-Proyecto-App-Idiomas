package com.lingualeap.feature.bienvenida

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lingualeap.ui.theme.LinguaSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Términos y Condiciones", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = LinguaSpacing.ScreenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Última actualización: Noviembre 2024",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            TermSection("1. Uso de la Plataforma", "LinguaLeap otorga una licencia personal e intransferible para aprender idiomas mediante IA y gamificación.")
            TermSection("2. Privacidad de Datos", "Utilizamos Firebase para proteger tu información. Tu progreso se sincroniza en la nube para que no pierdas tus rachas.")
            TermSection("3. Tutoría por IA", "Nuestro Tutor IA utiliza modelos avanzados. Aunque es muy preciso, recomendamos contrastar conceptos complejos con fuentes oficiales.")
            TermSection("4. Propiedad Intelectual", "Los algoritmos de aprendizaje y el contenido de las lecciones están protegidos por derechos de autor.")
            
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun TermSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title, 
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = content, 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
