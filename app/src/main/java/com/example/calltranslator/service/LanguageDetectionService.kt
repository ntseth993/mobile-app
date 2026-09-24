package com.example.calltranslator.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LanguageDetectionService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://libretranslate.de/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val api = retrofit.create(TranslationApi::class.java)

    suspend fun detectLanguage(text: String): String = withContext(Dispatchers.IO) {
        try {
            val response = api.detectLanguage(text)
            if (response.isNotEmpty()) {
                response.first().language
            } else {
                "en" // Default to English if detection fails
            }
        } catch (e: Exception) {
            // Default to English if API fails
            "en"
        }
    }

    fun close() {
        // No resources to clean up
    }
}
