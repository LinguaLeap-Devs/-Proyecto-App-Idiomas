package com.lingualeap.data.model

object AppData {
    val availableLanguages =
            listOf(
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

    val listaDesafios =
            listOf(
                    Challenge(
                            id = "primer_paso",
                            titulo = "Primer Paso",
                            descripcion = "Completa tu primera lección",
                            recompensaXp = 50,
                            icono = "🌱"
                    ),
                    Challenge(
                            id = "racha_3",
                            titulo = "Constancia",
                            descripcion = "Mantén una racha de 3 días",
                            recompensaXp = 100,
                            icono = "🔥"
                    ),
                    Challenge(
                            id = "maestro_xp",
                            titulo = "Maestro de XP",
                            descripcion = "Acumula tus primeros 100 XP",
                            recompensaXp = 150,
                            icono = "⚡"
                    )
            )

    data class CategoryWord(
            val word: String = "",
            val translation: String = "",
            val phonetic: String = ""
    )

    val glossaryViajes =
            listOf(
                    CategoryWord("Passport", "Pasaporte", "/ˈpæspɔːrt/"),
                    CategoryWord("Airport", "Aeropuerto", "/ˈerpɔːrt/"),
                    CategoryWord("Hotel", "Hotel", "/hoʊˈtɛl/")
            )
    val glossaryComida =
            listOf(
                    CategoryWord("Bread", "Pan", "/brɛd/"),
                    CategoryWord("Water", "Agua", "/ˈwɔːtər/"),
                    CategoryWord("Apple", "Manzana", "/ˈæpəl/")
            )
    val glossaryCultura =
            listOf(
                    CategoryWord("Museum", "Museo", "/mjuːˈziːəm/"),
                    CategoryWord("Art", "Arte", "/ɑːrt/")
            )
    val glossaryNegocios =
            listOf(
                    CategoryWord("Meeting", "Reunión", "/ˈmiːtɪŋ/"),
                    CategoryWord("Office", "Oficina", "/ˈɒfɪs/")
            )
    val glossaryHogar =
            listOf(
                    CategoryWord("House", "Casa", "/haʊs/"),
                    CategoryWord("Kitchen", "Cocina", "/ˈkɪtʃɪn/")
            )
    val glossaryTransporte = listOf<CategoryWord>()
    val glossaryRopa = listOf<CategoryWord>()
    val glossaryFrases = listOf<CategoryWord>()
    val glossarySalud = listOf<CategoryWord>()
    val glossaryRestaurante = listOf<CategoryWord>()

    val listBadges =
            listOf(
                    Badge(
                            id = "early_bird",
                            name = "Madrugador",
                            description = "Practica antes de las 8 AM",
                            emoji = "🌅",
                            category = BadgeCategory.STREAK,
                            requirement = 1
                    )
            )
    val wordOfTheDay = CategoryWord("Knowledge", "Conocimiento", "/ˈnɒlɪdʒ/")

    val allWordsList =
            glossaryViajes + glossaryComida + glossaryCultura + glossaryNegocios + glossaryHogar

    fun findWordData(word: String): CategoryWord? {
        return allWordsList.find { it.word.equals(word, ignoreCase = true) }
    }

    fun getLessonsForLanguage(langCode: String): List<Lesson> {
        return when (langCode) {
            "en" ->
                    listOf(
                            // ETAPA 1: TÓPICO - AMISTAD
                            Lesson(
                                    id = 1,
                                    title = "¡Hola Amigo!",
                                    emoji = "👋",
                                    xpReward = 20,
                                    description = "Aprende a saludar y presentar a tus amigos.",
                                    questions =
                                            listOf(
                                                    Question(
                                                            101,
                                                            "¿Cómo presentarías a tu amigo?",
                                                            listOf(
                                                                    "This is my friend",
                                                                    "This is my dog",
                                                                    "I am a friend"
                                                            ),
                                                            "This is my friend"
                                                    ),
                                                    Question(
                                                            102,
                                                            "¿Cómo se dice 'Mejores amigos'?",
                                                            listOf(
                                                                    "Best friends",
                                                                    "Good brothers",
                                                                    "Happy people"
                                                            ),
                                                            "Best friends"
                                                    )
                                            )
                            ),
                            Lesson(
                                    id = 2,
                                    title = "Describiendo Amigos",
                                    emoji = "😊",
                                    xpReward = 25,
                                    description =
                                            "Aprende a describir la personalidad de tus amigos.",
                                    questions =
                                            listOf(
                                                    Question(
                                                            201,
                                                            "Traduce: 'Mi amigo es muy divertido'",
                                                            listOf(
                                                                    "My friend is very funny",
                                                                    "My friend is angry",
                                                                    "He is my friend"
                                                            ),
                                                            "My friend is very funny"
                                                    ),
                                                    Question(
                                                            202,
                                                            "Selecciona la palabra correcta para 'Leal':",
                                                            listOf("Loyal", "Tall", "Sad"),
                                                            "Loyal"
                                                    )
                                            )
                            ),
                            Lesson(
                                    id = 3,
                                    title = "Planes con Amigos",
                                    emoji = "🍿",
                                    xpReward = 20,
                                    description = "Vocabulario para invitar a tus amigos a salir.",
                                    questions =
                                            listOf(
                                                    Question(
                                                            301,
                                                            "¿Cómo invitas a un amigo al cine?",
                                                            listOf(
                                                                    "Let's go to the movies!",
                                                                    "I want to sleep",
                                                                    "Where is the bathroom?"
                                                            ),
                                                            "Let's go to the movies!"
                                                    ),
                                                    Question(
                                                            302,
                                                            "Completa: 'We are going to play ___' (videojuegos)",
                                                            listOf(
                                                                    "video games",
                                                                    "the guitar",
                                                                    "tennis"
                                                            ),
                                                            "video games"
                                                    )
                                            )
                            ),
                            Lesson(id = 4, title = "Cofre de Amistad", emoji = "🎁", xpReward = 0),

                            // ETAPA 2: TÓPICOS ESPECÍFICOS
                            Lesson(
                                    id = 5,
                                    title = "En el Restaurante",
                                    emoji = "🍽️",
                                    xpReward = 30,
                                    description = "Vocabulario para pedir comida.",
                                    questions =
                                            listOf(
                                                    Question(
                                                            501,
                                                            "¿Cómo se dice 'La cuenta'?",
                                                            listOf(
                                                                    "The bill",
                                                                    "The menu",
                                                                    "The table"
                                                            ),
                                                            "The bill"
                                                    ),
                                                    Question(
                                                            502,
                                                            "'Water, please' significa...",
                                                            listOf(
                                                                    "Agua, por favor",
                                                                    "Pan, por favor"
                                                            ),
                                                            "Agua, por favor"
                                                    )
                                            )
                            ),
                            Lesson(
                                    id = 6,
                                    title = "Viajes: El Avión",
                                    emoji = "✈️",
                                    xpReward = 30,
                                    questions =
                                            listOf(
                                                    Question(
                                                            601,
                                                            "¿Dónde está mi pasaporte?",
                                                            listOf(
                                                                    "Where is my passport?",
                                                                    "Where is my hotel?"
                                                            ),
                                                            "Where is my passport?"
                                                    )
                                            )
                            ),
                            Lesson(
                                    id = 7,
                                    title = "Hogar y Familia",
                                    emoji = "🏠",
                                    xpReward = 20,
                                    questions =
                                            listOf(
                                                    Question(
                                                            701,
                                                            "¿Cómo se dice 'Casa'?",
                                                            listOf("House", "Office", "School"),
                                                            "House"
                                                    )
                                            )
                            ),
                            Lesson(
                                    id = 8,
                                    title = "Desafío Final",
                                    emoji = "🏆",
                                    xpReward = 50,
                                    questions =
                                            listOf(
                                                    Question(
                                                            801,
                                                            "Traduce: 'I love learning with friends'",
                                                            listOf(
                                                                    "Me encanta aprender con amigos",
                                                                    "No me gusta estudiar"
                                                            ),
                                                            "Me encanta aprender con amigos"
                                                    )
                                            )
                            )
                    )
            else -> emptyList()
        }
    }
}
