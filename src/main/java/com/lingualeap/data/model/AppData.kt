package com.lingualeap.data.model

/**
 * ────────────────────────────────────────────────────────────────────────────
 * APP DATA - Base de datos estática
 * ────────────────────────────────────────────────────────────────────────────
 * He añadido más lecciones y niveles para que la experiencia sea más completa.
 */
object AppData {

    val availableLanguages: List<Language> = listOf(
        Language(code = "en", name = "Inglés", flag = "🇺🇸", color = 0xFF1565C0, totalLessons = 20),
        Language(code = "fr", name = "Francés", flag = "🇫🇷", color = 0xFFD32F2F, totalLessons = 18),
        Language(code = "pt", name = "Portugués", flag = "🇧🇷", color = 0xFF2E7D32, totalLessons = 16)
    )

    fun getLessonsForLanguage(langCode: String): List<Lesson> {
        return when (langCode) {
            "en" -> getEnglishLessons()
            else -> getEnglishLessons() // Por ahora usamos inglés para todos
        }
    }

    private fun getEnglishLessons(): List<Lesson> = listOf(
        // NIVEL 1: PRINCIPIANTE
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
        // NIVEL 2: BÁSICO
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
        // NIVEL 3: ELEMENTARY
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
        // NIVEL 4: INTERMEDIO
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
