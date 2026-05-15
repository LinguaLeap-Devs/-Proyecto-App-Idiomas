# LinguaLeap - Documentación del Proyecto

## 1. Visión General
**LinguaLeap** es una aplicación móvil nativa para Android enfocada en el aprendizaje de idiomas a través de un sistema altamente gamificado, interactivo y potenciado por Inteligencia Artificial. La aplicación utiliza una temática "galáctica/espacial" (Galaxy Aesthetic) para hacer que la progresión del usuario se sienta como una exploración espacial.

## 2. Stack Tecnológico
*   **Lenguaje:** Kotlin
*   **Interfaz de Usuario:** Jetpack Compose (Material Design 3)
*   **Arquitectura:** MVVM (Model - View - ViewModel)
*   **Programación Asíncrona:** Kotlin Coroutines & StateFlow
*   **Backend & Base de Datos:** Firebase Cloud Firestore
*   **Autenticación:** Firebase Authentication
*   **Trabajos en Segundo Plano:** WorkManager (para notificaciones de rachas)
*   **Inteligencia Artificial:** Integración de IA para evaluación de escritura y explicación de errores en tiempo real.

## 3. Estructura del Proyecto (Paquetes)
La aplicación está modularizada por funcionalidades (features) bajo el paquete principal `com.lingualeap`:

*   **`data`**: Contiene la capa de datos.
    *   **`model`**: Clases de datos (`User`, `Lesson`, `Question`, `Challenge`, `Badge`, `Language`).
    *   **`repository`**: Clases que manejan la sincronización con Firebase (`DatabaseSyncManager`).
*   **`feature`**: Contiene las pantallas y la lógica de negocio dividida por módulos:
    *   **`autenticacion`**: Flujo de inicio de sesión, registro y `AuthViewModel`.
    *   **`bienvenida`**: Pantalla de Splash, Onboarding y selección de idioma.
    *   **`estudio`**: Pantallas de Glosario (categorías) y Flashcards para repasar vocabulario.
    *   **`iaconsultas`**: Contiene el `AIViewModel` que analiza la escritura del usuario y da feedback en caso de errores en las lecciones.
    *   **`inicio`**: Pantallas principales del Dashboard (Mapa de nodos estilo galaxia), Ranking Social (`RankingTab`) y sistema de ligas.
    *   **`lecciones`**: Lógica de los tests de nivel (`QuizScreen`), el desarrollo de las lecciones interactivas (`LessonScreen`) y el `LessonsViewModel`.
    *   **`perfil`**: Gestión de cuenta del usuario, estadísticas rápidas y pantalla de configuración (`SettingsScreen`).
*   **`tareas`**: Contiene los "Workers" como `NotificadorRachaWorker` para enviar recordatorios diarios al usuario.
*   **`ui`**: Elementos visuales reutilizables.
    *   **`components`**: Botones personalizados (`LinguaButton`), inputs (`LinguaTextField`), etc.
    *   **`theme`**: Colores de la paleta Galaxy (NeonCyan, DeepNavy, AccentBlue), tipografías y el `ThemeManager`.

## 4. Estructura de la Base de Datos (Firestore)
La aplicación está migrando a ser 100% dependiente de la nube. La estructura principal es:

1.  **Colección `users`**:
    *   Guarda el perfil, XP acumulado, racha diaria, insignias desbloqueadas, idioma seleccionado, y configuración de UI (dark mode, notificaciones).
2.  **Colección `languages` (ej. documento `en`)**:
    *   Guarda la información general del idioma.
    *   **Subcolección `levels`** (ej. `BEGINNER`).
        *   **Subcolección `subtopics`** (ej. `etapa_1`, `general`).
            *   **Subcolección `lessons`**: Aquí viven los documentos de las lecciones (id, title, xpReward) y su respectivo Array de preguntas (`questions`).
3.  **Colección `glossary`**:
    *   Guarda las listas de palabras divididas por categorías (viajes, comida, hogar, etc.) para la sección de Flashcards.

## 5. Características Principales (Features)
*   **Nivelación Inteligente:** Al iniciar, el usuario puede hacer un test escrito que es evaluado por una IA para determinar su nivel (A1, B1, etc.).
*   **Mapa de Exploración (Pathing):** Un recorrido visual de nodos (lecciones, audios, cuentos y cofres) que el usuario debe ir desbloqueando.
*   **Explicaciones de IA:** Si el usuario falla una pregunta, tiene un botón de "Analizar con IA" que consulta a la IA por qué se equivocó y cómo mejorar.
*   **Gamificación Completa:**
    *   **Ligas:** Bronce, Plata, Oro, Diamante.
    *   **Rachas Diarias (Streaks):** Mantienen al usuario comprometido.
    *   **Ranking:** Tabla de clasificación comparando XP entre amigos.
    *   **Text to Speech (TTS):** Los usuarios pueden escuchar la pronunciación de las preguntas.

## 6. Siguientes Pasos (Roadmap)
1.  **Desacoplar `AppData.kt`:** Eliminar por completo los datos fijos (mock data) del código para que el tamaño de la aplicación sea menor y dependa exclusivamente de Firebase.
2.  **Backoffice / Panel Admin:** Crear una herramienta web sencilla para que profesores o administradores puedan cargar nuevas lecciones y audios a Firebase sin tener que escribirlas en código.
3.  **Sistema Social:** Conectar las pantallas de `RankingTab` para que las solicitudes de amistad y la comparación de XP funcionen en tiempo real usando Firestore.
