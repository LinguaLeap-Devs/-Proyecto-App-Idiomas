package com.lingualeap.data.model

object AppData {
    val availableLanguages = listOf(
        Language("en", "Inglés", "🇺🇸", 0xFF42A5F5),
        Language("fr", "Francés", "🇫🇷", 0xFFEF5350),
        Language("pt", "Portugués", "🇧🇷", 0xFF66BB6A),
        Language("de", "Alemán", "🇩🇪", 0xFFFFB300),
        Language("it", "Italiano", "🇮🇹", 0xFFAB47BC),
        Language("ru", "Ruso", "🇷🇺", 0xFF26A69A),
        Language("ja", "Japonés", "🇯🇵", 0xFF78909C),
        Language("ko", "Coreano", "🇰🇷", 0xFF5C6BC0),
        Language("zh", "Chino", "🇨🇳", 0xFFFF7043)
    )

    val listaDesafios = listOf(
        Challenge(id = "primer_paso", titulo = "Primer Paso", descripcion = "Completa tu primera lección", recompensaXp = 50, icono = "🌱"),
        Challenge(id = "racha_3", titulo = "Constancia", descripcion = "Mantén una racha de 3 días", recompensaXp = 100, icono = "🔥"),
        Challenge(id = "maestro_xp", titulo = "Maestro de XP", descripcion = "Acumula tus primeros 100 XP", recompensaXp = 150, icono = "⚡")
    )

    data class CategoryWord(val word: String = "", val translation: String = "", val phonetic: String = "")

    val glossaryViajes = listOf(CategoryWord("Passport", "Pasaporte", "/ˈpæspɔːrt/"), CategoryWord("Airport", "Aeropuerto", "/ˈerpɔːrt/"), CategoryWord("Hotel", "Hotel", "/hoʊˈtɛl/"))
    val glossaryComida = listOf(CategoryWord("Bread", "Pan", "/brɛd/"), CategoryWord("Water", "Agua", "/ˈwɔːtər/"), CategoryWord("Apple", "Manzana", "/ˈæpəl/"))
    val glossaryCultura = listOf(CategoryWord("Museum", "Museo", "/mjuːˈziːəm/"), CategoryWord("Art", "Arte", "/ɑːrt/"))
    val glossaryNegocios = listOf(CategoryWord("Meeting", "Reunión", "/ˈmiːtɪŋ/"), CategoryWord("Office", "Oficina", "/ˈɒfɪs/"))
    val glossaryHogar = listOf(CategoryWord("House", "Casa", "/haʊs/"), CategoryWord("Kitchen", "Cocina", "/ˈkɪtʃɪn/"))
    val glossaryTransporte = listOf<CategoryWord>()
    val glossaryRopa = listOf<CategoryWord>()
    val glossaryFrases = listOf<CategoryWord>()
    val glossarySalud = listOf<CategoryWord>()
    val glossaryRestaurante = listOf<CategoryWord>()

    val listBadges = listOf(
        Badge(id = "early_bird", name = "Madrugador", description = "Practica antes de las 8 AM", emoji = "🌅", category = BadgeCategory.STREAK, requirement = 1)
    )
    val wordOfTheDay = CategoryWord("Knowledge", "Conocimiento", "/ˈnɒlɪdʒ/")

    val allWordsList = glossaryViajes + glossaryComida + glossaryCultura + glossaryNegocios + glossaryHogar
    
    fun findWordData(word: String): CategoryWord? {
        return allWordsList.find { it.word.equals(word, ignoreCase = true) }
    }

    fun getLessonsForLanguage(langCode: String): List<Lesson> {
        return when (langCode) {
            "en" -> listOf(
                // ETAPA 1: LOS 8 NODOS DE LA HOME
                Lesson(id = 1, title = "¡Un café!", emoji = "☕", xpReward = 20,
                    questions = listOf(Question(101, "¿Cómo se dice Café?", listOf("Coffee", "Tea", "Milk"), "Coffee"))),
                
                Lesson(id = 2, title = "Cuento: Café Pro", emoji = "📖", xpReward = 25,
                    questions = listOf(Question(201, "The coffee is...", listOf("Hot", "Cold"), "Hot"))),
                
                Lesson(id = 3, title = "Mascotas", emoji = "🐶", xpReward = 20,
                    questions = listOf(Question(301, "¿Cómo se dice Perro?", listOf("Dog", "Cat", "Bird"), "Dog"))),
                
                Lesson(id = 4, title = "Cofre", emoji = "🎁", xpReward = 0), // Nodo de recompensa
                
                Lesson(id = 5, title = "Radio: Café?", emoji = "📻", xpReward = 30,
                    questions = listOf(Question(501, "Listen and select", listOf("Coffee", "Sugar"), "Coffee"))),
                
                Lesson(id = 6, title = "Barista Máster", emoji = "☕", xpReward = 30,
                    questions = listOf(Question(601, "Hard question", listOf("Option A", "Option B"), "Option A"))),
                
                Lesson(id = 7, title = "Cuenta, por favor", emoji = "💳", xpReward = 20,
                    questions = listOf(Question(701, "¿Cómo se dice Cuenta?", listOf("Bill", "Money"), "Bill"))),
                
                Lesson(id = 8, title = "Repaso Final", emoji = "🏆", xpReward = 50,
                    questions = listOf(Question(801, "Review Question", listOf("Correct", "Wrong"), "Correct")))
            )
            else -> emptyList()
        }
    }
}
