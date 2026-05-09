package com.lingualeap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lingualeap.data.ThemeManager
import com.lingualeap.feature.autenticacion.*
import com.lingualeap.feature.bienvenida.*
import com.lingualeap.feature.inicio.*
import com.lingualeap.feature.lecciones.*
import com.lingualeap.feature.perfil.*
import com.lingualeap.feature.estudio.*
import com.lingualeap.ui.theme.LinguaLeapTheme
import com.lingualeap.feature.iaconsultas.AIViewModel
import com.lingualeap.feature.autenticacion.AuthViewModel
import com.lingualeap.feature.lecciones.LessonsViewModel
import com.lingualeap.tareas.NotificadorRachaWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themeManager = ThemeManager(applicationContext)

        // Sincronización de base de datos
        val syncManager = com.lingualeap.data.repository.DatabaseSyncManager(applicationContext)
        syncManager.verificarYSincronizar(lifecycleScope)
        
        configurarRecordatorioRacha()

        setContent {
            val darkModePreference by themeManager.darkModeFlow.collectAsState(initial = null)
            val useDarkTheme = darkModePreference ?: isSystemInDarkTheme()

            LinguaLeapTheme(darkTheme = useDarkTheme) { LinguaLeapApp() }
        }
    }

    private fun configurarRecordatorioRacha() {
        try {
            val workRequest = PeriodicWorkRequestBuilder<NotificadorRachaWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "notificador_racha",
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
        } catch (e: Exception) {
            // Evitar que un error en WorkManager cierre la app
            e.printStackTrace()
        }
    }
}

// Estructura de navegación dinámica
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Repaso : Screen("repaso")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object Terms : Screen("terms")
    object LanguageSelection : Screen("language_selection")
    object Glossary : Screen("glossary/{categoryId}") {
        fun createRoute(categoryId: String) = "glossary/$categoryId"
    }
    object Flashcards : Screen("flashcards/{categoryId}") {
        fun createRoute(categoryId: String) = "flashcards/$categoryId"
    }
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Int) = "lesson/$lessonId"
    }
}

@Composable
fun LinguaLeapApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val lessonsViewModel: LessonsViewModel = viewModel()
    val aiViewModel: AIViewModel = viewModel()
    val usuario by authViewModel.usuarioActual.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Lógica de Redirección Automática
    LaunchedEffect(usuario, currentRoute) {
        val enPantallaDeAuth = currentRoute == Screen.Splash.route || 
                               currentRoute == Screen.Login.route || 
                               currentRoute == Screen.Register.route
        
        usuario?.let { user ->
            if (enPantallaDeAuth) {
                if (user.selectedLang != null) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }
        }
    }

    LaunchedEffect(usuario?.selectedLang) {
        usuario?.selectedLang?.code?.let { lessonsViewModel.cargarLeccionesDesdeFirebase(it) }
    }

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = {}
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {}
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                authViewModel = authViewModel,
                lessonsViewModel = lessonsViewModel,
                aiViewModel = aiViewModel,
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = authViewModel,
                lessonsViewModel = lessonsViewModel,
                aiViewModel = aiViewModel,
                onStartLesson = { lesson -> navController.navigate(Screen.Lesson.createRoute(lesson.id)) },
                onStartRepaso = { navController.navigate(Screen.Repaso.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToViajes = { navController.navigate(Screen.Glossary.createRoute("viajes")) },
                onNavigateToComida = { navController.navigate(Screen.Glossary.createRoute("comida")) },
                onNavigateToCultura = { navController.navigate(Screen.Glossary.createRoute("cultura")) },
                onNavigateToNegocios = { navController.navigate(Screen.Glossary.createRoute("negocios")) },
                onNavigateToHogar = { navController.navigate(Screen.Glossary.createRoute("hogar")) },
                onNavigateToTransporte = { navController.navigate(Screen.Glossary.createRoute("transporte")) },
                onNavigateToRopa = { navController.navigate(Screen.Glossary.createRoute("ropa")) },
                onNavigateToFrases = { navController.navigate(Screen.Glossary.createRoute("frases")) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTerms = { navController.navigate(Screen.Terms.route) },
                onNavigateToLanguage = { navController.navigate(Screen.LanguageSelection.route) },
                onLogoutSuccess = {
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Terms.route) { TermsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            Screen.Glossary.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString("categoryId") ?: ""
            GlossaryScreen(
                catId.replaceFirstChar { it.uppercase() },
                catId,
                lessonsViewModel,
                authViewModel,
                { navController.navigate(Screen.Flashcards.createRoute(catId)) },
                { navController.popBackStack() }
            )
        }
        composable(
            Screen.Flashcards.route,
            arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString("categoryId") ?: ""
            FlashcardsScreen(
                catId.replaceFirstChar { it.uppercase() },
                catId,
                lessonsViewModel,
                authViewModel,
                { navController.popBackStack() }
            )
        }
        composable(Screen.Repaso.route) {
            RepasoScreen(
                authViewModel = authViewModel,
                lessonsViewModel = lessonsViewModel,
                onClose = { navController.popBackStack() }
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                authViewModel = authViewModel,
                onNavigateToPractice = { navController.navigate(Screen.Flashcards.createRoute("favorites")) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId")
            val lecciones by lessonsViewModel.lecciones.collectAsStateWithLifecycle()
            lecciones.find { it.id == lessonId }?.let { lesson ->
                LessonScreen(
                    lesson = lesson,
                    aiViewModel = aiViewModel,
                    onLessonComplete = { xp ->
                        authViewModel.marcarLeccionCompletada(lesson.id, xp)
                        navController.popBackStack()
                    },
                    onQuestionFailed = { qId -> authViewModel.registrarErrorEnPregunta(qId) },
                    onClose = { navController.popBackStack() }
                )
            }
        }
    }
}
