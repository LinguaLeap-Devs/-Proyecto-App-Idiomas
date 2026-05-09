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
                val listaCompleta = mutableListOf<Lesson>()
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
                        
                        listaCompleta.addAll(lessonsSnapshot.toObjects(Lesson::class.java))
                    }
                }

                if (listaCompleta.isEmpty()) {
                    _lecciones.value = AppData.getLessonsForLanguage(langCode)
                } else {
                    _lecciones.value = listaCompleta.sortedBy { it.id }
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
}
