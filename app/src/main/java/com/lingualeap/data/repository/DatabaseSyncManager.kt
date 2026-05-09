package com.lingualeap.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.lingualeap.data.model.AppData
import com.lingualeap.data.model.LessonLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

class DatabaseSyncManager(private val context: Context) {

    // Versión 80: Forzamos la inyección de las LECCIONES de la Home con sus PREGUNTAS
    private val VERSION_ACTUAL_DATOS = 80 

    fun verificarYSincronizar(coroutineScope: CoroutineScope) {
        val prefs = context.getSharedPreferences("lingualeap_prefs", Context.MODE_PRIVATE)
        val versionSincronizada = prefs.getInt("version_db_firestore", 0)

        Log.i("DatabaseSyncManager", ">>> ESTADO SYNC: Local=$versionSincronizada | Requerida=$VERSION_ACTUAL_DATOS")

        if (versionSincronizada < VERSION_ACTUAL_DATOS) {
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    mostrarToast("Inyectando nodos de la Home y preguntas...")
                    Log.i("DatabaseSyncManager", ">>> INICIANDO CARGA DE RUTA DE APRENDIZAJE (v$VERSION_ACTUAL_DATOS) <<<")
                    
                    sincronizarTodoFirestore()
                    
                    prefs.edit().putInt("version_db_firestore", VERSION_ACTUAL_DATOS).apply()
                    
                    Log.i("DatabaseSyncManager", ">>> ¡ÉXITO! HOME Y PREGUNTAS SINCRONIZADOS EN FIREBASE <<<")
                    mostrarToast("✅ Firebase actualizado correctamente")
                } catch (e: Exception) {
                    Log.e("DatabaseSyncManager", "!!! ERROR EN SINCRONIZACIÓN: ${e.message}", e)
                    mostrarToast("❌ Error al inyectar datos")
                }
            }
        }
    }

    private suspend fun mostrarToast(mensaje: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun sincronizarTodoFirestore() {
        val db = FirebaseFirestore.getInstance()

        // 1. Configuración, Desafíos e Insignias
        db.collection("config").document("word_of_the_day").set(AppData.wordOfTheDay).await()
        for (desafio in AppData.listaDesafios) {
            db.collection("challenges").document(desafio.id).set(desafio).await()
        }
        for (insignia in AppData.listBadges) {
            db.collection("badges").document(insignia.id).set(insignia).await()
        }

        // 2. Glosarios
        val glosarios = mapOf(
            "viajes" to AppData.glossaryViajes,
            "comida" to AppData.glossaryComida,
            "cultura" to AppData.glossaryCultura,
            "negocios" to AppData.glossaryNegocios,
            "hogar" to AppData.glossaryHogar
        )
        for ((catId, lista) in glosarios) {
            val catRef = db.collection("glossary").document(catId)
            catRef.set(mapOf("id" to catId, "title" to catId.replaceFirstChar { it.uppercase() })).await()
            for (wordItem in lista) {
                if (wordItem.word.isNotEmpty()) {
                    catRef.collection("words").document(wordItem.word).set(wordItem).await()
                }
            }
        }

        // 3. Idiomas y Lecciones (Nodos de la Home con Preguntas)
        for (idioma in AppData.availableLanguages) {
            db.collection("languages").document(idioma.code).set(idioma).await()

            for (nivelEnum in LessonLevel.entries) {
                val levelDocRef = db.collection("languages").document(idioma.code)
                    .collection("levels").document(nivelEnum.name)

                levelDocRef.set(mapOf("name" to nivelEnum.displayName)).await()

                val lecciones = AppData.getLessonsForLanguage(idioma.code).filter { it.level == nivelEnum }
                for (leccion in lecciones) {
                    val subtopicId = leccion.subtopicId.ifBlank { "etapa_1" }
                    val subtopicRef = levelDocRef.collection("subtopics").document(subtopicId)
                    subtopicRef.set(mapOf("title" to "ORDEN EN EL CAFÉ")).await()
                    
                    // A. Guardamos el documento de la lección
                    val lessonDocRef = subtopicRef.collection("lessons").document(leccion.id.toString())
                    lessonDocRef.set(leccion).await()

                    // B. Inyectamos las PREGUNTAS como sub-colección
                    for (pregunta in leccion.questions) {
                        lessonDocRef.collection("questions")
                            .document(pregunta.id.toString())
                            .set(pregunta)
                            .await()
                    }
                    
                    Log.d("DatabaseSyncManager", "Inyectado nodo: ${leccion.title} con ${leccion.questions.size} preguntas.")
                }
            }
        }
    }
}
