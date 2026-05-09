package com.lingualeap.feature.iaconsultas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lingualeap.BuildConfig
import com.lingualeap.data.model.Question
import com.lingualeap.data.model.QuestionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIViewModel : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _respuestaIA = MutableStateFlow<String>("")
    val respuestaIA: StateFlow<String> = _respuestaIA.asStateFlow()

    private val _nivelDetectado = MutableStateFlow<String?>(null)
    val nivelDetectado: StateFlow<String?> = _nivelDetectado.asStateFlow()

    private val _explicacionError = MutableStateFlow<String?>(null)
    val explicacionError: StateFlow<String?> = _explicacionError.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _preguntasGeneradas = MutableStateFlow<List<Question>>(emptyList())
    val preguntasGeneradas: StateFlow<List<Question>> = _preguntasGeneradas.asStateFlow()

    private val MAX_REINTENTOS = 3
    private val DELAY_INICIAL = 2000L // 2 segundos

    fun evaluarNivel(textoUsuario: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            var reintentos = 0
            var exito = false

            while (reintentos < MAX_REINTENTOS && !exito) {
                try {
                    val prompt = """
                        Actúa como un profesor de idiomas experto. 
                        Analiza la siguiente respuesta de un estudiante: "$textoUsuario"
                        Determina su nivel (A1, A2, B1, B2, C1) y responde solo con el nombre del nivel.
                    """.trimIndent()

                    val result = generativeModel.generateContent(prompt)
                    val nivel = result.text?.trim() ?: "Principiante (A1)"
                    val nivelFinal = cuandoNivelSeaValido(nivel)
                    _nivelDetectado.value = nivelFinal
                    _respuestaIA.value = "Tu nivel estimado es $nivelFinal."
                    exito = true
                } catch (e: Exception) {
                    Log.e("AIViewModel", "Error en evaluarNivel: ${e.message}", e)
                    reintentos++
                    if (esErrorDeCuota(e) && reintentos < MAX_REINTENTOS) {
                        delay(DELAY_INICIAL * reintentos)
                    } else {
                        _nivelDetectado.value = "Principiante (A1)"
                        _respuestaIA.value = obtenerMensajeErrorAmigable(e)
                        break
                    }
                }
            }
            _estaCargando.value = false
        }
    }

    fun explicarError(pregunta: String, respuestaUsuario: String, respuestaCorrecta: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            _explicacionError.value = null
            var reintentos = 0
            var exito = false

            while (reintentos < MAX_REINTENTOS && !exito) {
                try {
                    val prompt = """
                        Explica brevemente por qué "$respuestaUsuario" está mal para "$pregunta". 
                        La correcta es "$respuestaCorrecta". Máximo 2 líneas.
                    """.trimIndent()

                    val result = generativeModel.generateContent(prompt)
                    _explicacionError.value = result.text?.trim()
                    exito = true
                } catch (e: Exception) {
                    Log.e("AIViewModel", "Error en explicarError: ${e.message}", e)
                    reintentos++
                    if (esErrorDeCuota(e) && reintentos < MAX_REINTENTOS) {
                        delay(DELAY_INICIAL * reintentos)
                    } else {
                        _explicacionError.value = "Explicación no disponible. La correcta es: $respuestaCorrecta"
                        break
                    }
                }
            }
            _estaCargando.value = false
        }
    }

    fun enviarConsulta(consulta: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            var reintentos = 0
            var exito = false

            while (reintentos < MAX_REINTENTOS && !exito) {
                try {
                    val prompt = """
                        Actúa como un profesor de idiomas experto y amable. 
                        Responde a la siguiente duda del estudiante: "$consulta"
                        Sé conciso pero claro. Máximo 4 líneas.
                    """.trimIndent()

                    val result = generativeModel.generateContent(prompt)
                    val texto = result.text?.trim()
                    if (texto.isNullOrBlank()) {
                        _respuestaIA.value = "La IA no pudo generar una respuesta segura para esta consulta."
                    } else {
                        _respuestaIA.value = texto
                    }
                    exito = true
                } catch (e: Exception) {
                    Log.e("AIViewModel", "Error en enviarConsulta: ${e.message}", e)
                    reintentos++
                    if (esErrorDeCuota(e) && reintentos < MAX_REINTENTOS) {
                        delay(DELAY_INICIAL * reintentos)
                    } else {
                        _respuestaIA.value = obtenerMensajeErrorAmigable(e)
                        break
                    }
                }
            }
            _estaCargando.value = false
        }
    }

    fun generarPreguntasDeRepaso(nivel: String, temasFallados: String) {
        viewModelScope.launch {
            _estaCargando.value = true
            var reintentos = 0
            var exito = false

            while (reintentos < MAX_REINTENTOS && !exito) {
                try {
                    val prompt = """
                        Actúa como un profesor de idiomas. El estudiante tiene nivel "$nivel" y recientemente falló en estos temas: "$temasFallados".
                        Genera 3 preguntas de práctica nuevas para ayudarle a mejorar.
                        DEBES responder ÚNICAMENTE con un arreglo JSON válido, sin texto adicional, sin bloques de código (```json).
                        Usa este formato exacto:
                        [
                          {
                            "id": 1,
                            "type": "MULTIPLE_CHOICE",
                            "text": "Pregunta en español",
                            "options": ["Opcion1", "Opcion2", "Opcion3"],
                            "correctAnswer": "Opcion correcta en ingles",
                            "explanation": "Explicación breve de por qué es la correcta"
                          },
                          {
                            "id": 2,
                            "type": "FILL_BLANK",
                            "text": "Frase en inglés con ___ para completar",
                            "options": [],
                            "correctAnswer": "palabra correcta",
                            "explanation": "Explicación breve"
                          }
                        ]
                        Asegúrate de que 'type' solo sea uno de estos: MULTIPLE_CHOICE, TRANSLATE, FILL_BLANK, SPEAKING.
                    """.trimIndent()

                    val result = generativeModel.generateContent(prompt)
                    val jsonResponse = result.text?.replace("```json", "")?.replace("```", "")?.trim()
                    
                    if (!jsonResponse.isNullOrBlank()) {
                        val gson = Gson()
                        val listType = object : TypeToken<List<Question>>() {}.type
                        val preguntas: List<Question> = gson.fromJson(jsonResponse, listType)
                        _preguntasGeneradas.value = preguntas
                        exito = true
                    } else {
                        throw Exception("Respuesta vacía de Gemini")
                    }
                } catch (e: Exception) {
                    Log.e("AIViewModel", "Error en generarPreguntasDeRepaso: ${e.message}", e)
                    reintentos++
                    if (esErrorDeCuota(e) && reintentos < MAX_REINTENTOS) {
                        delay(DELAY_INICIAL * reintentos)
                    } else {
                        // PLAN B: Fallback (cargar de DB local, por ahora lista vacía)
                        _preguntasGeneradas.value = emptyList()
                        break
                    }
                }
            }
            _estaCargando.value = false
        }
    }

    private fun esErrorDeCuota(e: Exception): Boolean {
        val msg = e.message?.lowercase() ?: ""
        return msg.contains("429") || msg.contains("exhausted") || msg.contains("limit")
    }

    private fun obtenerMensajeErrorAmigable(e: Exception): String {
        val msg = e.message?.lowercase() ?: ""
        return when {
            msg.contains("api key") || msg.contains("invalid") -> 
                "Error de llave: Verifica que GEMINI_API_KEY sea correcta."
            msg.contains("location not supported") || msg.contains("user location") -> 
                "Lo siento, Gemini no está disponible en tu región actual."
            msg.contains("network") || msg.contains("unable to resolve host") -> 
                "Error de conexión: Revisa tu internet."
            else -> "Error de IA: ${e.message}"
        }
    }

    private fun cuandoNivelSeaValido(texto: String): String {
        val nivelesValidos = listOf("Principiante (A1)", "Elemental (A2)", "Intermedio (B1)", "Intermedio Alto (B2)", "Avanzado (C1)")
        return nivelesValidos.find { texto.contains(it, ignoreCase = true) } ?: "Principiante (A1)"
    }

    fun limpiarExplicacion() { _explicacionError.value = null }
}
