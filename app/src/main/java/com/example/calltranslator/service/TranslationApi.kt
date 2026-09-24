package com.example.calltranslator.service

import retrofit2.http.GET
import retrofit2.http.Query

data class TranslationResponse(
    val translatedText: String
)

data class LanguageDetectionResponse(
    val confidence: Float,
    val language: String
)

interface TranslationApi {
    @GET("translate")
    suspend fun translate(
        @Query("q") text: String,
        @Query("source") source: String,
        @Query("target") target: String,
        @Query("api_key") apiKey: String = ""
    ): TranslationResponse
    
    @GET("detect")
    suspend fun detectLanguage(
        @Query("q") text: String,
        @Query("api_key") apiKey: String = ""
    ): List<LanguageDetectionResponse>
}
