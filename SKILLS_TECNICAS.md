# 🚀 LinguaLeap - Skills Técnicas (MVP)

Este documento detalla las capacidades técnicas clave implementadas en el proyecto, utilizando una nomenclatura de "Skills" para facilitar su identificación en demostraciones técnicas.

---

### 🎙️ [Skill: Multimedia] - Text-to-Speech (TTS) Nativo
- **Descripción:** Integración del motor nativo de Android (`android.speech.tts`) para la pronunciación de frases en tiempo real.
- **Implementación:** Localizada en `LessonScreen.kt`. Se activa automáticamente al cargar una nueva pregunta.
- **Impacto:** Eleva la app de un simple cuestionario a una herramienta de aprendizaje auditivo real.

### 🎨 [Skill: UX/UI] - Feedback Dinámico y Animaciones
- **Descripción:** Sistema de retroalimentación visual inmediata con estados críticos (Correcto/Incorrecto).
- **Implementación:** Uso de `AnimatedVisibility` y transiciones de color en `LessonScreen.kt` y `Components.kt`.
- **Impacto:** Mejora la experiencia del usuario (UX) mediante refuerzo positivo y correcciones claras.

### 🏗️ [Skill: Arquitectura] - MVVM con Estado Reactivo
- **Descripción:** Gestión centralizada del estado de la aplicación y flujo de datos unidireccional.
- **Implementación:** `AuthViewModel.kt` utiliza `StateFlow` y `MutableStateFlow` para sincronizar XP y datos de usuario en tiempo real entre pantallas.
- **Impacto:** Garantiza la integridad de los datos y una "Sola Fuente de Verdad" (SSOT).

### 🗺️ [Skill: UI] - Algoritmos de Layout Personalizados
- **Descripción:** Renderizado dinámico de rutas de aprendizaje en formato "Zigzag".
- **Implementación:** Cálculo matemático basado en índices dentro de `LazyColumn` en `HomeScreen.kt`.
- **Impacto:** Crea una interfaz visualmente atractiva y lúdica (estilo gamificación) sin depender de librerías externas pesadas.

---
**Nomenclatura utilizada:** `[Skill: Categoría] - Nombre`.
Fáciles de aprender, fáciles de entender, prácticos para producción.
