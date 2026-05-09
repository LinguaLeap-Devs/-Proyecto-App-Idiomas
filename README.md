# LinguaLeap — App de Aprendizaje de Idiomas
**Kotlin + Jetpack Compose + MVVM**

---

## Estructura del proyecto

```
app/src/main/java/com/lingualeap/
│
├── MainActivity.kt              ← Punto de entrada + Navegación (rutas)
│
├── data/model/
│   ├── Models.kt                ← Clases de datos: User, Language, Lesson, AuthState
│   └── AppData.kt               ← Idiomas y lecciones disponibles (datos estáticos)
│
├── viewmodel/
│   └── AuthViewModel.kt         ← Lógica de login, registro, validaciones
│
└── ui/
    ├── theme/
    │   ├── Theme.kt             ← Colores de la app (LinguaColors)
    │   └── Typography.kt        ← Fuentes y tamaños de texto
    │
    ├── components/
    │   └── Components.kt        ← Componentes reutilizables (botones, inputs, tarjetas)
    │
    └── screens/
        ├── SplashScreen.kt      ← Pantalla de bienvenida
        ├── LoginScreen.kt       ← Inicio de sesión
        ├── RegisterScreen.kt    ← Registro + selector de idioma
        └── HomeScreen.kt        ← Home con lista de lecciones
```

---

## Cómo personalizar

### Cambiar colores
Edita `ui/theme/Theme.kt` → objeto `LinguaColors`
```kotlin
val Primary = Color(0xFF5C6BC0)  // Cambia este hex por cualquier color
```

### Cambiar el nombre de la app
Edita `ui/screens/SplashScreen.kt`:
```kotlin
private const val APP_NAME   = "LinguaLeap"   // ← Cambia aquí
private const val APP_SLOGAN = "Aprende un idioma..."
```

### Agregar un idioma
Edita `data/model/AppData.kt`, agrega un objeto en `availableLanguages`:
```kotlin
Language(code = "it", name = "Italiano", flag = "🇮🇹", color = 0xFF1B5E20, totalLessons = 14)
```

### Agregar una lección
En `AppData.kt` → función `getLessonsForLanguage()`:
```kotlin
Lesson(id = 6, title = "Animales", description = "...", emoji = "🐾",
       level = LessonLevel.ELEMENTARY, durationMin = 10, xpReward = 20, isLocked = true)
```

### Conectar a un backend real
En `AuthViewModel.kt` busca los comentarios `// TODO`:
```kotlin
// TODO: Aquí iría: val response = authRepository.login(email, password)
```
Reemplaza el `delay()` y el usuario demo con una llamada a Retrofit o Firebase.

### Agregar una pantalla nueva
1. Crea `ui/screens/NuevaPantalla.kt`
2. Agrega la ruta en `MainActivity.kt`:
```kotlin
object NuevaPantalla : Screen("nueva_pantalla")
```
3. Agrega el composable en `NavHost`:
```kotlin
composable(Screen.NuevaPantalla.route) {
    NuevaPantalla(onNavigateBack = { navController.popBackStack() })
}
```

---

## Próximos pasos sugeridos
- [ ] Pantalla de Lección con ejercicios (multiple choice, flashcards)
- [ ] Persistencia con Room Database
- [ ] Firebase Authentication
- [ ] Animaciones con Lottie
- [ ] Notificaciones de racha diaria
