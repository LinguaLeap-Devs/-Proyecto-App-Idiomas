package com.lingualeap.feature.bienvenida

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingualeap.data.model.Language
import com.lingualeap.ui.components.LinguaButton
import com.lingualeap.ui.components.LinguaTextField
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.feature.lecciones.LessonsViewModel
import com.lingualeap.feature.iaconsultas.AIViewModel
import com.lingualeap.feature.lecciones.QuizScreen

enum class OnboardingStep { COUNTRY, REGION, LANGUAGE, QUIZ, LEVEL, PERSONAL_INFO }

@Composable
fun OnboardingScreen(
    authViewModel: AuthViewModel,
    lessonsViewModel: LessonsViewModel,
    aiViewModel: AIViewModel,
    onOnboardingComplete: () -> Unit
) {
    var currentStep by remember { mutableStateOf(OnboardingStep.COUNTRY) }
    
    // Data collected
    var selectedCountry by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("") }
    var nativeLang by remember { mutableStateOf("Español") }
    var selectedLearningLang by remember { mutableStateOf<Language?>(null) }
    var selectedLevel by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    val countries = listOf(
        "Peru" to "🇵🇪", "Mexico" to "🇲🇽", "Spain" to "🇪🇸",
        "Argentina" to "🇦🇷", "Colombia" to "🇨🇴", "USA" to "🇺🇸",
        "UK" to "🇬🇧", "China" to "🇨🇳", "Japan" to "🇯🇵"
    )
    
    val regionsPeru = listOf("Lima", "Arequipa", "Cusco", "Trujillo", "Piura", "Otros")
    val levels = listOf("Principiante (A1)", "Elemental (A2)", "Intermedio (B1)", "Intermedio Alto (B2)", "Avanzado (C1)")

    val learningLanguages by lessonsViewModel.idiomas.collectAsStateWithLifecycle()
    val cargandoIdiomas by lessonsViewModel.cargando.collectAsStateWithLifecycle()
    val cargandoOnboarding by authViewModel.cargandoOnboarding.collectAsStateWithLifecycle()
    val onboardingFinalizado by authViewModel.onboardingFinalizado.collectAsStateWithLifecycle()
    val usuarioActual by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val aiLevel by aiViewModel.nivelDetectado.collectAsStateWithLifecycle()

    LaunchedEffect(onboardingFinalizado) { if (onboardingFinalizado) onOnboardingComplete() }
    LaunchedEffect(Unit) { lessonsViewModel.cargarIdiomas() }
    
    LaunchedEffect(aiLevel) {
        aiLevel?.let { 
            selectedLevel = it
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundBrush)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
        ) {
            // Header: Botón atrás y progreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        currentStep = when (currentStep) {
                            OnboardingStep.REGION -> OnboardingStep.COUNTRY
                            OnboardingStep.LANGUAGE -> if (selectedCountry == "Peru") OnboardingStep.REGION else OnboardingStep.COUNTRY
                            OnboardingStep.QUIZ -> OnboardingStep.LANGUAGE
                            OnboardingStep.LEVEL -> OnboardingStep.QUIZ
                            OnboardingStep.PERSONAL_INFO -> OnboardingStep.LEVEL
                            else -> currentStep
                        }
                    },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape).size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                LinearProgressIndicator(
                    progress = { 
                        when(currentStep) {
                            OnboardingStep.COUNTRY -> 0.15f
                            OnboardingStep.REGION -> 0.30f
                            OnboardingStep.LANGUAGE -> 0.45f
                            OnboardingStep.QUIZ -> 0.60f
                            OnboardingStep.LEVEL -> 0.80f
                            OnboardingStep.PERSONAL_INFO -> 1f
                        }
                    },
                    modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            }

            // --- SECCIÓN DE SELECCIÓN ACTUAL ---
            AnimatedVisibility(
                visible = selectedCountry.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val flag = countries.find { it.first == selectedCountry }?.second ?: ""
                    SelectionSummaryChip(text = "$flag $selectedCountry")
                    
                    if (selectedRegion.isNotEmpty()) {
                        SelectionSummaryChip(text = selectedRegion)
                    }
                    
                    selectedLearningLang?.let {
                        SelectionSummaryChip(text = "${it.flag} ${it.name}")
                    }
                }
            }

            Spacer(Modifier.height(if (selectedCountry.isEmpty()) 60.dp else 40.dp))

            AnimatedContent(
                targetState = currentStep,
                modifier = Modifier.weight(1f),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = ""
            ) { step ->
                when (step) {
                    OnboardingStep.COUNTRY -> CountryStep(
                        selectedCountry = selectedCountry,
                        countries = countries,
                        onCountrySelected = { selectedCountry = it },
                        onNext = { currentStep = if (selectedCountry == "Peru") OnboardingStep.REGION else OnboardingStep.LANGUAGE }
                    )
                    OnboardingStep.REGION -> RegionStep(
                        selectedRegion = selectedRegion,
                        regions = regionsPeru,
                        onRegionSelected = { selectedRegion = it },
                        onNext = { currentStep = OnboardingStep.LANGUAGE }
                    )
                    OnboardingStep.LANGUAGE -> LanguageStep(
                        selectedLanguage = selectedLearningLang,
                        languages = learningLanguages,
                        isLoading = cargandoIdiomas,
                        onLanguageSelected = { selectedLearningLang = it },
                        onNext = { currentStep = OnboardingStep.QUIZ }
                    )
                    OnboardingStep.QUIZ -> QuizScreen(
                        aiViewModel = aiViewModel,
                        onQuizComplete = { currentStep = OnboardingStep.LEVEL }
                    )
                    OnboardingStep.LEVEL -> LevelStep(
                        selectedLevel = selectedLevel,
                        levels = levels,
                        onLevelSelected = { selectedLevel = it },
                        onNext = { currentStep = OnboardingStep.PERSONAL_INFO }
                    )
                    OnboardingStep.PERSONAL_INFO -> PersonalInfoStep(
                        birthDate = birthDate,
                        onBirthDateChange = { birthDate = it },
                        isLoading = cargandoOnboarding,
                        onComplete = {
                            selectedLearningLang?.let { lang ->
                                authViewModel.completarOnboarding(
                                    country = selectedCountry,
                                    region = if (selectedCountry == "Peru") selectedRegion else "",
                                    nativeLang = nativeLang,
                                    learningLanguage = lang,
                                    level = selectedLevel,
                                    name = usuarioActual?.name ?: "",
                                    birthDate = birthDate,
                                    gender = ""
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionSummaryChip(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun CountryStep(
    selectedCountry: String,
    countries: List<Pair<String, String>>,
    onCountrySelected: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mi País/Región", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = buildAnnotatedString {
                append("Nacionalidad legal, no residencia. ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Una vez seleccionada, no se puede modificar.") }
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp),
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(40.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(countries) { (name, flag) ->
                CountryChip(text = name, flag = flag, isSelected = selectedCountry == name, onClick = { onCountrySelected(name) })
            }
        }
        
        if (selectedCountry.isNotEmpty()) {
            LinguaButton(
                text = "CONTINUAR", 
                onClick = onNext, 
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(bottom = 10.dp)
            )
        }
    }
}

@Composable
fun CountryChip(text: String, flag: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                 else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        shadowElevation = if (isSelected) 4.dp else 1.dp,
        modifier = Modifier.height(50.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp), 
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.Start
        ) {
            Text(flag, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                text = text, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium, 
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun RegionStep(selectedRegion: String, regions: List<String>, onRegionSelected: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mi Región", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Selecciona tu ubicación actual.", 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, 
            modifier = Modifier.padding(top = 12.dp)
        )
        Spacer(Modifier.height(40.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), 
            horizontalArrangement = Arrangement.spacedBy(10.dp), 
            verticalArrangement = Arrangement.spacedBy(12.dp), 
            modifier = Modifier.weight(1f)
        ) {
            items(regions) { region ->
                val isSelected = selectedRegion == region
                Surface(
                    onClick = { onRegionSelected(region) },
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                             else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.height(50.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) { 
                        Text(
                            text = region, 
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        ) 
                    }
                }
            }
        }
        LinguaButton(
            text = "CONTINUAR", 
            onClick = onNext, 
            enabled = selectedRegion.isNotEmpty(), 
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}

@Composable
fun LanguageStep(selectedLanguage: Language?, languages: List<Language>, isLoading: Boolean, onLanguageSelected: (Language) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Quiero aprender", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(40.dp))
        if (isLoading && languages.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { 
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary) 
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), 
                horizontalArrangement = Arrangement.spacedBy(10.dp), 
                verticalArrangement = Arrangement.spacedBy(12.dp), 
                modifier = Modifier.weight(1f)
            ) {
                items(languages) { lang ->
                    CountryChip(
                        text = lang.name, 
                        flag = lang.flag, 
                        isSelected = selectedLanguage == lang, 
                        onClick = { onLanguageSelected(lang) }
                    )
                }
            }
        }
        LinguaButton(
            text = "CONTINUAR", 
            onClick = onNext, 
            enabled = selectedLanguage != null, 
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}

@Composable
fun LevelStep(selectedLevel: String, levels: List<String>, onLevelSelected: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Mi Nivel", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "La IA ha sugerido un nivel para ti, pero puedes ajustarlo si lo prefieres.", 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 12.dp)
        )
        Spacer(Modifier.height(40.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            levels.forEach { level ->
                val isSelected = selectedLevel == level
                Surface(
                    onClick = { onLevelSelected(level) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) 
                             else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Box(Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.CenterStart) { 
                        Text(
                            text = level, 
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        ) 
                    }
                }
            }
        }
        LinguaButton(
            text = "CONTINUAR", 
            onClick = onNext, 
            enabled = selectedLevel.isNotEmpty(), 
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}

@Composable
fun PersonalInfoStep(birthDate: String, onBirthDateChange: (String) -> Unit, isLoading: Boolean, onComplete: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Sobre mí", 
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(40.dp))
        Column(modifier = Modifier.weight(1f)) {
            LinguaTextField(
                value = birthDate, 
                onValueChange = onBirthDateChange, 
                label = "Fecha de nacimiento", 
                placeholder = "DD/MM/AAAA"
            )
        }
        LinguaButton(
            text = "FINALIZAR", 
            onClick = onComplete, 
            isLoading = isLoading, 
            enabled = birthDate.isNotBlank(), 
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )
    }
}
