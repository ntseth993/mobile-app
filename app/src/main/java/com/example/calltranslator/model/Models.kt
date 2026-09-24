package com.example.calltranslator.model

data class UserModel(
    val uid: String,
    val name: String,
    val email: String,
    val preferredLanguage: String,
    val avatarUrl: String,
    val isOnline: Boolean = true
)

data class TranscriptMessage(
    val senderName: String,
    val originalText: String,
    val translatedText: String,
    val timestamp: String,
    val isMe: Boolean
)
