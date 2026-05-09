package com.lingualeap.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsManager private constructor(context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
            }
        }
    }

    fun speakSmart(text: String) {
        if (!isInitialized) return
        val segments = text.split(Regex("[\'\"“”]")).filter { it.isNotBlank() }
        segments.forEachIndexed { index, segment ->
            val lower = segment.lowercase()
            val isSpanish = lower.contains(Regex("[¿áéíóúñ]")) || lower.contains("como") || lower.contains("dice")
            tts?.language = if (isSpanish) Locale("es", "ES") else Locale.US
            val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts?.speak(segment, queueMode, null, null)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isInitialized = false
    }

    companion object {
        @Volatile
        private var instance: TtsManager? = null

        fun getInstance(context: Context): TtsManager {
            return instance ?: synchronized(this) {
                instance ?: TtsManager(context).also { instance = it }
            }
        }
    }
}
