package com.example.calltranslator.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class TextToSpeechService(private val context: Context) {
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    fun initialize(onInitComplete: (Boolean) -> Unit) {
        textToSpeech = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            onInitComplete(isInitialized)
        }
    }

    fun speak(text: String, language: String = Locale.getDefault().language): Flow<Boolean> = callbackFlow {
        if (!isInitialized || textToSpeech == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val locale = when (language.lowercase()) {
            "kinyarwanda", "rw" -> Locale("rw")
            "french", "fr" -> Locale.FRENCH
            "english", "en" -> Locale.ENGLISH
            else -> Locale.getDefault()
        }

        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val listener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                trySend(true)
                close()
            }
            override fun onError(utteranceId: String?) {
                trySend(false)
                close()
            }
        }

        textToSpeech?.setOnUtteranceProgressListener(listener)
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance")

        awaitClose {
            textToSpeech?.stop()
        }
    }

    fun stop() {
        textToSpeech?.stop()
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isInitialized = false
    }
}
