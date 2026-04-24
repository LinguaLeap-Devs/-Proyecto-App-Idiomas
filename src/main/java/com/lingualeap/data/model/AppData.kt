package com.lingualeap.data.model

/**
 * ────────────────────────────────────────────────────────────────────────────
 * APP DATA - Base de datos estática mejorada (Honestidad Técnica)
 * ────────────────────────────────────────────────────────────────────────────
 */
object AppData {

    val availableLanguages: List<Language> = listOf(
        Language(code = "en", name = "Inglés", flag = "🇺🇸", color = 0xFF1565C0, totalLessons = 4),
        Language(code = "fr", name = "Francés", flag = "🇫🇷", color = 0xFFD32F2F, totalLessons = 0),
        Language(code = "pt", name = "Portugués", flag = "🇧🇷", color = 0xFF2E7D32, totalLessons = 0)
    )

    /**
     * Devuelve lecciones solo si existen para el idioma solicitado.
     * 🟢 MEJORA DE EXCELENCIA: No más fallbacks engañosos a inglés.
     */
    fun getLessonsForLanguage(langCode: String): List<Lesson> {
        return when (langCode) {
            "en" -> getEnglishLessons()
            else -> emptyList() // Retornamos lista vacía para idiomas sin contenido
        }
    }

    private fun getEnglishLessons(): List<Lesson> = listOf(
        Lesson(
            id          = 1,
            title       = "Saludos",
            description = "Hola, Adiós y Presentaciones",
            emoji       = "👋",
            level       = LessonLevel.BEGINNER,
            durationMin = 3,
            xpReward    = 10,
            isLocked    = false,
            isCompleted = false,
            questions   = listOf(
                Question(1, "¿Cómo se dice 'Hola'?", listOf("Hello", "Bye", "Water"), "Hello"),
                Question(2, "¿Cómo se dice 'Adiós'?", listOf("Hi", "Goodbye", "Bread"), "Goodbye"),
                Question(3, "Traduce 'Good morning':", listOf("Buenas noches", "Buenos días", "Hola"), "Buenos días")
            )
        ),
        Lesson(
            id          = 2,
            title       = "Números",
            description = "Contando del 1 al 10",
            emoji       = "🔢",
            level       = LessonLevel.BEGINNER,
            durationMin = 5,
            xpReward    = 15,
            isLocked    = false,
            questions   = listOf(
                Question(4, "¿Qué número es 'Seven'?", listOf("5", "7", "9"), "7"),
                Question(5, "¿Cómo se escribe 'Dos'?", listOf("One", "Three", "Two"), "Two")
            )
        ),
        Lesson(
            id          = 3,
            title       = "La Familia",
            description = "Padres, hermanos y mascotas",
            emoji       = "👨‍👩‍👧",
            level       = LessonLevel.ELEMENTARY,
            durationMin = 7,
            xpReward    = 20,
            isLocked    = false,
            questions   = listOf(
                Question(6, "¿Quién es 'Mother'?", listOf("Padre", "Madre", "Hijo"), "Madre"),
                Question(7, "¿Cómo se dice 'Hermano'?", listOf("Sister", "Brother", "Father"), "Brother")
            )
        ),
        Lesson(
            id          = 4,
            title       = "En el restaurante",
            description = "Pedir comida y bebidas",
            emoji       = "🍔",
            level       = LessonLevel.INTERMEDIATE,
            durationMin = 10,
            xpReward    = 30,
            isLocked    = false,
            questions   = listOf(
                Question(8, "¿Qué significa 'The bill'?", listOf("La cuenta", "El billete", "La comida"), "La cuenta"),
                Question(9, "¿Cómo pides 'Agua'?", listOf("Water", "Wine", "Juice"), "Water")
            )
        )
    )
}
