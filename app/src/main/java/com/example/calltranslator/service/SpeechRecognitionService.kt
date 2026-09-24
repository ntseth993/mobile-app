package com.example.calltranslator.service

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

class SpeechRecognitionService(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null

    fun startSpeechRecognition(language: String = Locale.getDefault().language): Flow<String> = callbackFlow {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            
            override fun onPartialResults(partialResults: android.os.Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(matches[0])
                }
            }
            
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(matches[0])
                }
                close()
            }
            
            override fun onError(error: Int) {
                close()
            }
            
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)
        speechRecognizer?.startListening(intent)

        awaitClose {
            speechRecognizer?.destroy()
        }
    }

    fun stopSpeechRecognition() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
