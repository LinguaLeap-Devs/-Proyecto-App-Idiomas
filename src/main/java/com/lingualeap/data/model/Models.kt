package com.lingualeap.data.model

import java.util.*

// ─────────────────────────────────────────
//  MODELOS DE DATOS
//  Estas data class definen la estructura
//  de la información en toda la app.
//  Cambia los campos según necesites.
// ─────────────────────────────────────────


// ── USUARIO ──────────────────────────────
// Representa al usuario registrado/logueado

data class User(
    val id            : String = "",
    val name          : String = "",       // Nombre completo
    val email         : String = "",       // Correo electrónico
    val selectedLang  : Language? = null,  // Idioma que está aprendiendo
    val streakDays    : Int = 0,           // Días consecutivos de práctica
    val totalXp       : Int = 0,           // Puntos de experiencia acumulados
    val avatarInitials: String = ""        // Iniciales para el avatar (ej: "JD")
)


// ── IDIOMA ───────────────────────────────
// Representa un idioma disponible para aprender

data class Language(
    val code    : String,   // Código ISO: "en", "fr", "pt", "de", "ja"
    val name    : String,   // Nombre en español: "Inglés"
    val flag    : String,   // Emoji de bandera: "🇺🇸"
    val color   : Long,     // Color hex para la tarjeta (ej: 0xFF1565C0)
    val totalLessons: Int = 0  // Total de lecciones disponibles
)


// ── LECCIÓN ──────────────────────────────
// Representa una lección dentro de un idioma

data class Lesson(
    val id          : Int,
    val title       : String,     // Título de la lección
    val description : String,     // Descripción breve
    val emoji       : String,     // Emoji ilustrativo
    val level       : LessonLevel,// Nivel de dificultad
    val durationMin : Int,        // Duración estimada en minutos
    val xpReward    : Int,        // XP que otorga al completar
    val isLocked    : Boolean,    // Si está bloqueada (requiere completar anterior)
    val isCompleted : Boolean = false, // Si el usuario ya la completó
    val questions   : List<Question> = emptyList() // Lista de preguntas/ejercicios
)

// ── PREGUNTA ─────────────────────────────
// Representa un ejercicio individual dentro de una lección

data class Question(
    val id: Int,
    val text: String,             // Enunciado (Ej: "¿Cómo se dice 'Hola'?")
    val options: List<String>,    // Opciones de respuesta
    val correctAnswer: String,    // La respuesta correcta
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRANSLATE,
    LISTENING
}


// ── NIVEL DE LECCIÓN ─────────────────────
// Enum para los niveles de dificultad
// Agrega más niveles si los necesitas

enum class LessonLevel(val displayName: String) {
    BEGINNER    ("Principiante"),
    ELEMENTARY  ("Básico"),
    INTERMEDIATE("Intermedio"),
    ADVANCED    ("Avanzado")
}


// ── ESTADO DE AUTENTICACIÓN ───────────────
// Representa el estado del flujo de login/registro

sealed class AuthState {
    object Idle    : AuthState()            // Sin acción
    object Loading : AuthState()            // Cargando (llamada a API)
    data class Success(val user: User) : AuthState()  // Login/registro exitoso
    data class Error(val message: String) : AuthState() // Error con mensaje
}
