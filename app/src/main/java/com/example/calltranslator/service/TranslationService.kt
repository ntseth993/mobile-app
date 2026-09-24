package com.example.calltranslator.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TranslationService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://libretranslate.de/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(TranslationApi::class.java)

    suspend fun translateText(text: String, targetLanguage: String, sourceLanguage: String? = null): String = 
        withContext(Dispatchers.IO) {
            try {
                val source = sourceLanguage ?: "auto"
                val response = api.translate(text, source, targetLanguage)
                response.translatedText
            } catch (e: Exception) {
                // Fallback to original text if API fails
                text
            }
        }

    suspend fun getSupportedLanguages(): List<String> = withContext(Dispatchers.IO) {
        // Common language codes
        listOf("en", "fr", "es", "de", "rw", "sw", "ar", "zh", "ja", "ko", "ru", "pt", "it", "nl", "pl", "tr", "hi")
    }

    fun close() {
        // No resources to clean up
    }
}
