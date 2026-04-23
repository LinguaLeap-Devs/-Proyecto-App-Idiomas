# 🗄️ [Skill: Almacenamiento] - Room Local Database

Esta Skill representa la capacidad de persistir datos de forma robusta y profesional en el dispositivo, garantizando que el progreso del usuario no se pierda al cerrar la aplicación.

### 🎯 Objetivo
Implementar una base de datos SQLite gestionada a través de la librería **Room**, siguiendo las mejores prácticas de Android para el manejo de persistencia offline.

### 🛠️ Componentes Técnicos:
1. **Entities:** Definición de tablas para `User` y `LessonStatus` para almacenar el XP acumulado y las lecciones desbloqueadas.
2. **DAOs (Data Access Objects):** Interfaces limpias para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar).
3. **Database Provider:** Configuración de un Singleton para la instancia de la base de datos.
4. **Repository Pattern:** Capa intermedia que decide si obtener datos de la base de datos o de la memoria.

### 📈 Valor del Negocio:
- **Offline First:** El usuario puede ver su progreso incluso sin conexión a internet.
- **Integridad de Datos:** Evita la pérdida de información durante cierres inesperados o actualizaciones de la app.
- **Experiencia Continua:** Al volver a abrir la app, el usuario retoma exactamente donde lo dejó.

---
**Estado:** *En proceso de implementación técnica.*
