# 🚀 LinguaLeap - Skills Técnicas (MVP)

Este documento detalla las capacidades técnicas clave implementadas en el proyecto, utilizando una nomenclatura de "Skills" para facilitar su identificación en demostraciones técnicas y auditorías de código.

---

### 🎙️ [Skill: Multimedia] - Text-to-Speech (TTS) Nativo
- **Descripción:** Integración del motor nativo de Android (`android.speech.tts`) para la pronunciación de frases en tiempo real.
- **Implementación:** Localizada en `LessonScreen.kt`. Se activa de forma automática al cargar una nueva pregunta para mejorar la inmersión lingüística.
- **Impacto:** Transforma la aplicación de un cuestionario estático a una herramienta de aprendizaje auditivo interactiva.

### 🎨 [Skill: UX/UI] - Feedback Dinámico y Animaciones
- **Descripción:** Sistema de retroalimentación visual inmediata con estados críticos (Correcto/Incorrecto) y transiciones fluidas.
- **Implementación:** Uso de `AnimatedVisibility` y estados de color reactivos en `LessonScreen.kt` y `Components.kt`.
- **Impacto:** Incrementa la retención del usuario mediante un sistema de refuerzo positivo y correcciones visuales claras.

### 🏗️ [Skill: Arquitectura] - MVVM con Estado Reactivo
- **Descripción:** Gestión centralizada del estado de la aplicación mediante un flujo de datos unidireccional y reactivo.
- **Implementación:** `AuthViewModel.kt` utiliza `StateFlow` y `MutableStateFlow` para garantizar que el progreso (XP) y los datos de usuario estén sincronizados en tiempo real en toda la app.
- **Impacto:** Asegura la integridad de los datos y facilita el mantenimiento del código bajo el principio de "Sola Fuente de Verdad" (SSOT).

### 🗺️ [Skill: UI] - Algoritmos de Layout Personalizados
- **Descripción:** Renderizado dinámico de rutas de aprendizaje siguiendo un patrón visual no lineal.
- **Implementación:** Cálculo matemático basado en índices dentro de `LazyColumn` en `HomeScreen.kt` para posicionar los nodos de lección en formato zigzag.
- **Impacto:** Proporciona una interfaz de usuario gamificada y visualmente atractiva sin depender de librerías externas adicionales.

---
**Nomenclatura utilizada:** `[Skill: Categoría] - Nombre`.
Documentación técnica orientada a la excelencia y escalabilidad del proyecto.
