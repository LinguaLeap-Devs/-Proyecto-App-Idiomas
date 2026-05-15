package com.lingualeap.data.model

// ── USUARIO ──────────────────────────────
data class User(
    val id            : String = "",
    val name          : String = "",
    val email         : String = "",
    val selectedLang  : Language? = null,
    val streakDays    : Int = 0,
    val totalXp       : Int = 0,
    val dailyGoalXp   : Int = 50, // Meta diaria de XP
    val avatarInitials: String = "",
    val photoUrl      : String = "",
    val role          : String = "user",
    val country       : String = "",
    val region        : String = "",
    val nativeLang    : String = "",
    val learningLevel : String = "",
    val birthDate     : String = "",
    val gender        : String = "",
    
    // Notificaciones
    val notificationsEnabled: Boolean = true,
    val streakNotificationsEnabled: Boolean = true,
    val soundsEnabled: Boolean = true,
    val emailNotificationsEnabled: Boolean = false,
    
    // Configuración App
    val darkModeEnabled: Boolean = false,
    val showTranslations: Boolean = true,
    
    // Chat IA
    val chatTextSize: Float = 16f,
    val showAiChatIdeas: Boolean = true,

    val completedLessons: List<Int> = listOf(),
    val wrongQuestionsIds: List<Int> = listOf(),
    val favoriteWordsIds: List<String> = listOf(),
    val fechaUltimaLeccion: Long = 0,
    val desafiosCompletados: List<String> = listOf(),
    val activityHistory: List<Long> = listOf(), // Almacena los timestamps de días practicados

    // Gamificación y Ranking
    val memberSince: Long = 0,
    val todayXp: Int = 0,
    val currentLeague: String = "BRONZE",
    val weeklyXp: Int = 0,
    val unlockedBadges: List<String> = listOf(),
    val friendsIds: List<String> = listOf() // IDs de amigos para el ranking social
)

// ── DESAFÍOS (CHALLENGES) ────────────────
data class Challenge(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val recompensaXp: Int = 0,
    val icono: String = ""
)

// ── ESTRUCTURA JERÁRQUICA ────────────────
data class LevelData(
    val id: String = "",
    val name: String = "",
    val subtopics: List<SubtopicData> = listOf()
)

data class SubtopicData(
    val id: String = "",
    val title: String = "",
    val lessons: List<Lesson> = listOf()
)

// ── IDIOMA ───────────────────────────────
data class Language(
    val code    : String = "",
    val name    : String = "",
    val flag    : String = "",
    val color   : Long = 0,
    val totalLessons: Int = 0
)

// ── LECCIÓN ──────────────────────────────
data class Lesson(
    val id          : Int = 0,
    val title       : String = "",
    val description : String = "",
    val emoji       : String = "",
    val imageUrl    : String = "",
    val level       : LessonLevel = LessonLevel.BEGINNER,
    val subtopicId  : String = "",
    val durationMin : Int = 0,
    val xpReward    : Int = 0,
    val isLocked    : Boolean = false,
    val isCompleted : Boolean = false,
    val questions: List<Question> = listOf()
)

// ── PREGUNTA ─────────────────────────────
data class Question(
    val id: Int = 0,
    val text: String = "",
    val options: List<String> = listOf(),
    val correctAnswer: String = "",
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val explanation: String = ""
)

enum class QuestionType { MULTIPLE_CHOICE, TRANSLATE, LISTENING, FILL_BLANK, SPEAKING }

@Suppress("unused")
enum class LessonLevel(val displayName: String) {
    BEGINNER          ("Principiante (A1)"),
    ELEMENTARY        ("Elemental (A2)"),
    INTERMEDIATE      ("Intermedio (B1)"),
    UPPER_INTERMEDIATE("Intermedio Alto (B2)"),
    ADVANCED          ("Avanzado (C1)")
}

sealed class AuthState {
    object Idle    : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}

// ── PROGRESO POR LECCIÓN ─────────────────
data class LessonProgress(
    val lessonId: Int = 0,
    val lessonTitle: String = "",
    val lessonEmoji: String = "",
    val totalWords: Int = 0,
    val completedWords: Int = 0,
    val status: LessonStatus = LessonStatus.PENDING,
    val lastPracticed: Long = 0
)

enum class LessonStatus(val displayName: String) {
    COMPLETED("Completado"),
    IN_PROGRESS("En progreso"),
    PENDING("Pendiente")
}

// ── SISTEMA DE LIGAS ─────────────────────
enum class League(val displayName: String, val colorHex: Long) {
    BRONZE("Liga Bronce", 0xFFCD7F32),
    SILVER("Liga Plata", 0xFFC0C0C0),
    GOLD("Liga Oro", 0xFFFFD700),
    DIAMOND("Liga Diamante", 0xFF00BCD4),
    CHAMPION("Liga Campeón", 0xFF9C27B0)
}

// ── INSIGNIAS ────────────────────────────
data class Badge(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val emoji: String = "",
    val category: BadgeCategory = BadgeCategory.STREAK,
    val requirement: Int = 0,
    val isUnlocked: Boolean = false
)

enum class BadgeCategory {
    STREAK, XP_DAILY, RANKING, LESSONS, PERFECT_SCORE
}
