package com.lingualeap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lingualeap.data.model.AppData
import com.lingualeap.ui.screens.*
import com.lingualeap.ui.theme.LinguaLeapTheme
import com.lingualeap.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinguaLeapTheme {
                LinguaLeapApp()
            }
        }
    }
}

/**
 * ────────────────────────────────────────────────────────────────────────────
 * RUTAS DE NAVEGACIÓN
 * ────────────────────────────────────────────────────────────────────────────
 * Definimos las pantallas de la aplicación.
 * Para la lección, usamos un parámetro {lessonId} para saber qué clase abrir.
 */
sealed class Screen(val route: String) {
    object Splash            : Screen("splash")
    object Login             : Screen("login")
    object Register          : Screen("register")
    object LanguageSelection : Screen("language_selection")
    object Quiz              : Screen("quiz")
    object Home              : Screen("home")
    object Lesson            : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: Int) = "lesson/$lessonId"
    }
}

@Composable
fun LinguaLeapApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToLogin    = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel            = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack       = { navController.popBackStack() },
                onLoginSuccess       = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel         = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateBack    = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.LanguageSelection.route)
                }
            )
        }

        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                viewModel          = authViewModel,
                onNavigateBack     = { navController.popBackStack() },
                onLanguageSelected = {
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                onQuizComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel     = authViewModel,
                onStartLesson = { lesson ->
                    // Navegamos a la pantalla de lección usando su ID
                    navController.navigate(Screen.Lesson.createRoute(lesson.id))
                },
                onLogout      = {
                    authViewModel.cerrarSesion()
                    navController.navigate(Screen.Splash.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // NUEVA RUTA: Pantalla de Lección
        composable(
            route = Screen.Lesson.route,
            arguments = listOf(navArgument("lessonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getInt("lessonId")
            val userState by authViewModel.usuarioActual.collectAsStateWithLifecycle()
            val userLanguage = userState?.selectedLang?.code ?: "en"
            
            // Buscamos los datos de la lección en nuestra "base de datos" estática
            val lesson = AppData.getLessonsForLanguage(userLanguage).find { it.id == lessonId }
            
            if (lesson != null) {
                LessonScreen(
                    lesson = lesson,
                    onLessonComplete = { _ ->
                        // Aquí podríamos actualizar el XP del usuario en el futuro
                        navController.popBackStack() 
                    },
                    onClose = { navController.popBackStack() }
                )
            }
        }
    }
}
