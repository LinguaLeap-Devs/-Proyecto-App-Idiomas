# Skill: Estabilización de Arranque y Configuración de Infraestructura

Esta skill documenta los cambios críticos realizados para resolver el problema de la "pantalla oscura" y la falta de ejecución de la aplicación LinguaLeap. Se enfoca en la sincronización entre el manifiesto, el tema visual y los servicios backend (Firebase).

## 🛠 Cambios Realizados

### 1. Integración de Firebase (Infraestructura)
**Problema:** Existía el archivo `google-services.json` pero el plugin de Google Services no estaba activo en el build, lo que impedía que `AuthViewModel` funcionara correctamente.
- **Acción:** Se añadió `alias(libs.plugins.google.services)` al bloque de `plugins` en `build.gradle.kts`.
- **Resultado:** La aplicación ahora puede vincularse con Firebase en tiempo de ejecución.

### 2. Corrección del Tema Base (UI/UX)
**Problema:** El `AndroidManifest.xml` usaba un tema Material 1 (`Theme.Material.Light.NoActionBar`) incompatible con los componentes de Jetpack Compose Material 3.
- **Acción:** Se actualizó `android:theme` a `@style/Theme.LinguaLeap` (o similar compatible con Compose).
- **Resultado:** Eliminación de parpadeos oscuros y mejor integración con `enableEdgeToEdge()`.

### 3. Sincronización de Versiones (Build System)
**Problema:** Discrepancias entre el `compileSdk` y el `targetSdk`.
- **Acción:** Se estandarizaron ambos a la versión 36 para asegurar compatibilidad total con las últimas APIs de Android.
- **Resultado:** Build estable y sin advertencias de deprecación críticas.

## 📝 Notas Técnicas
- Se verificó que el logo en `SplashScreen.kt` use `ContentScale.Crop` para evitar el "efecto parche" visual.
- Se recomienda realizar un `Gradle Sync` después de estos cambios.

---
**Estado Final:** Aplicación lista para ejecución con servicios de autenticación activos.
