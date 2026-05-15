package com.lingualeap.feature.lecciones

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.lingualeap.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LessonsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()

    init {
        viewModelScope.launch {
            inyectarDatosFirebase()
        }
    }

    private val _lecciones = MutableStateFlow<List<Lesson>>(emptyList())
    val lecciones: StateFlow<List<Lesson>> = _lecciones

    private val _idiomas = MutableStateFlow<List<Language>>(AppData.availableLanguages)
    val idiomas: StateFlow<List<Language>> = _idiomas

    private val _palabrasGlosario = MutableStateFlow<List<AppData.CategoryWord>>(emptyList())
    val palabrasGlosario: StateFlow<List<AppData.CategoryWord>> = _palabrasGlosario

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    /**
     * Carga las palabras de una categoría específica del glosario desde Firestore.
     * Si la categoría es "favorites", utiliza la lista de IDs proporcionada.
     */
    fun cargarPalabrasGlosario(categoriaId: String, favoritosIds: List<String> = emptyList()) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                if (categoriaId == "favorites") {
                    // Cargamos favoritos: podemos buscarlos en AppData o en una colección global si existiera.
                    // Por ahora usamos AppData.findWordData para reconstruir la lista desde los IDs.
                    _palabrasGlosario.value = favoritosIds.mapNotNull { AppData.findWordData(it) }
                } else {
                    val snapshot = db.collection("glossary")
                        .document(categoriaId)
                        .collection("words")
                        .get()
                        .await()
                    
                    val lista = snapshot.toObjects(AppData.CategoryWord::class.java)
                    if (lista.isNotEmpty()) {
                        _palabrasGlosario.value = lista
                    } else {
                        // Fallback a AppData si Firebase está vacío
                        _palabrasGlosario.value = when(categoriaId) {
                            "viajes" -> AppData.glossaryViajes
                            "comida" -> AppData.glossaryComida
                            "cultura" -> AppData.glossaryCultura
                            "negocios" -> AppData.glossaryNegocios
                            "hogar" -> AppData.glossaryHogar
                            "transporte" -> AppData.glossaryTransporte
                            "ropa" -> AppData.glossaryRopa
                            "frases" -> AppData.glossaryFrases
                            else -> emptyList()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LessonsViewModel", "Error al cargar glosario $categoriaId", e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarLeccionesDesdeFirebase(langCode: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val mapCompleto = mutableMapOf<Int, Lesson>()
                val langRef = db.collection("languages").document(langCode)
                
                val levelsSnapshot = langRef.collection("levels").get().await()
                
                for (levelDoc in levelsSnapshot.documents) {
                    val levelId = levelDoc.id
                    val subtopicsSnapshot = langRef.collection("levels").document(levelId)
                        .collection("subtopics").get().await()
                    
                    for (subtopicDoc in subtopicsSnapshot.documents) {
                        val subtopicId = subtopicDoc.id
                        val lessonsSnapshot = langRef.collection("levels").document(levelId)
                            .collection("subtopics").document(subtopicId)
                            .collection("lessons").get().await()
                        
                        for (lesson in lessonsSnapshot.toObjects(Lesson::class.java)) {
                            val existing = mapCompleto[lesson.id]
                            // Si hay duplicados en Firebase, nos quedamos con la que tenga más preguntas
                            if (existing == null || lesson.questions.size > existing.questions.size) {
                                mapCompleto[lesson.id] = lesson
                            }
                        }
                    }
                }

                if (mapCompleto.isEmpty()) {
                    _lecciones.value = AppData.getLessonsForLanguage(langCode)
                } else {
                    _lecciones.value = mapCompleto.values.sortedBy { it.id }
                }
            } catch (e: Exception) {
                Log.e("LessonsViewModel", "Error al cargar jerarquía para $langCode", e)
                _lecciones.value = AppData.getLessonsForLanguage(langCode)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarIdiomas() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val snapshot = db.collection("languages").get().await()
                val lista = snapshot.toObjects(Language::class.java)
                if (lista.isNotEmpty()) {
                    _idiomas.value = lista
                } else {
                    _idiomas.value = AppData.availableLanguages
                }
            } catch (e: Exception) {
                Log.e("LessonsViewModel", "Error al cargar idiomas", e)
                _idiomas.value = AppData.availableLanguages
            } finally {
                _cargando.value = false
            }
        }
    }

    private suspend fun inyectarDatosFirebase() {
        try {
            val langCode = "en"
            val levelId = "BEGINNER" // Usando tu estructura existente
            val subtopicId = "general" // Como pediste, inyectamos en 'general'
            
            val langRef = db.collection("languages").document(langCode)
            
            // Nivel (usando merge para no borrar tus otros datos)
            langRef.collection("levels").document(levelId).set(
                mapOf("id" to levelId, "name" to "Principiante (A1)"),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
            
            // Subtopic (usando merge)
            langRef.collection("levels").document(levelId)
                .collection("subtopics").document(subtopicId).set(
                    mapOf("id" to subtopicId, "title" to "Etapa 1"),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            
            val genericQuestions = listOf(
                Question(1, "¿Cómo se dice 'Hola'?", listOf("Hello", "Bye", "Please"), "Hello"),
                Question(2, "¿Cómo se dice 'Adiós'?", listOf("Thanks", "Goodbye", "Water"), "Goodbye"),
                Question(3, "¿Cómo se dice 'Por favor'?", listOf("Coffee", "Please", "Friend"), "Please"),
                Question(4, "¿Cómo se dice 'Gracias'?", listOf("Thank you", "House", "Car"), "Thank you"),
                Question(5, "¿Cómo se dice 'Agua'?", listOf("Food", "Water", "Hello"), "Water"),
                Question(6, "¿Cómo se dice 'Café'?", listOf("Coffee", "Bye", "Please"), "Coffee"),
                Question(7, "¿Cómo se dice 'Amigo'?", listOf("Thanks", "Goodbye", "Friend"), "Friend"),
                Question(8, "¿Cómo se dice 'Casa'?", listOf("Coffee", "House", "Friend"), "House"),
                Question(9, "¿Cómo se dice 'Coche'?", listOf("Car", "House", "Water"), "Car"),
                Question(10, "¿Cómo se dice 'Comida'?", listOf("Food", "Water", "Hello"), "Food")
            )
            
            // Lecciones
            val lessons = AppData.getLessonsForLanguage(langCode).map { lesson ->
                val newQuestions = genericQuestions.map { q -> 
                    q.copy(id = lesson.id * 100 + q.id) 
                }
                lesson.copy(questions = newQuestions)
            }
            
            for (lesson in lessons) {
                langRef.collection("levels").document(levelId)
                    .collection("subtopics").document(subtopicId)
                    .collection("lessons").document(lesson.id.toString())
                    .set(lesson).await()
            }
            
            Log.d("LessonsViewModel", "Datos inyectados correctamente a Firebase")
            cargarLeccionesDesdeFirebase("en")
        } catch(e: Exception) {
            Log.e("LessonsViewModel", "Error inyectando datos", e)
        }
    }
}
