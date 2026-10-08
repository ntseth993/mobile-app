package com.example.calltranslator.viewmodel

import android.content.Context
import android.content.Intent
import com.example.calltranslator.model.UserModel
import com.example.calltranslator.service.SimCard
import com.example.calltranslator.service.TelecomCallManager
import com.example.calltranslator.service.CallLogService
import com.example.calltranslator.service.ContactsService
import com.example.calltranslator.service.SpeechRecognitionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calltranslator.model.TranscriptMessage
import com.example.calltranslator.service.Contact
import com.example.calltranslator.service.LanguageDetectionService
import com.example.calltranslator.service.TextToSpeechService
import com.example.calltranslator.service.TranslationService
import kotlinx.coroutines.*

enum class CallState { IDLE, CONNECTING, CONNECTED, WEAK_CONNECTION, RECONNECTING, DISCONNECTED }
enum class PipelineStatus { STANDBY, LISTENING, TRANSLATING, SPEAKING }

class LinguaPhoneViewModel(private val context: Context) : ViewModel() {

    // Services
    private val languageDetectionService = LanguageDetectionService()
    private val translationService = TranslationService()
    private val contactsService = ContactsService(context)
    private val callLogService = CallLogService(context)
    private val speechRecognitionService = SpeechRecognitionService(context)
    private val textToSpeechService = TextToSpeechService(context)
    private val telecomCallManager = TelecomCallManager(context)

    // Theme Mode Flow State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Auth Session Flow States
    private val _currentUser = MutableStateFlow<UserModel?>(
        UserModel("user_seth", "Seth", "seth@linguaphone.ai", "Kinyarwanda", "🇷🇼")
    )
    val currentUser: StateFlow<UserModel?> = _currentUser

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    // Static Seed Contacts List
    val contacts = listOf(
        UserModel("c1", "John Smith", "john@lingua.ai", "English", "🇬🇧", true),
        UserModel("c2", "Eric Munyaneza", "eric@lingua.ai", "Kinyarwanda", "🇷🇼", true),
        UserModel("c3", "Marie Dubois", "marie@lingua.ai", "French", "🇫🇷", false),
        UserModel("c4", "Sarah Kenya", "sarah@lingua.ai", "Swahili", "🇰🇪", true)
    )

    // Call Context States
    private val _callState = MutableStateFlow(CallState.IDLE)
    val callState: StateFlow<CallState> = _callState

    private val _remoteUser = MutableStateFlow<UserModel?>(null)
    val remoteUser: StateFlow<UserModel?> = _remoteUser

    private val _myLang = MutableStateFlow("Kinyarwanda")
    val myLang: StateFlow<String> = _myLang

    private val _remoteLang = MutableStateFlow("English")
    val remoteLang: StateFlow<String> = _remoteLang

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted

    private val _isSpeakerPhone = MutableStateFlow(true)
    val isSpeakerPhone: StateFlow<Boolean> = _isSpeakerPhone

    // Transcript Dialogue Feed Stream Flow State
    private val _transcript = MutableStateFlow<List<TranscriptMessage>>(emptyList())
    val transcript: StateFlow<List<TranscriptMessage>> = _transcript

    // Real-Time Audio Pipeline System States
    private val _pipelineStatus = MutableStateFlow(PipelineStatus.STANDBY)
    val pipelineStatus: StateFlow<PipelineStatus> = _pipelineStatus

    private val _audioWaveLevel = MutableStateFlow(0.0f)
    val audioWaveLevel: StateFlow<Float> = _audioWaveLevel

    // Settings Slider Engine Options Variables
    private val _speechSpeed = MutableStateFlow(1.0f)
    val speechSpeed: StateFlow<Float> = _speechSpeed

    private val _voiceVolume = MutableStateFlow(0.8f)
    val voiceVolume: StateFlow<Float> = _voiceVolume

    private val _activeVoiceModel = MutableStateFlow("Premium AI Voice Alpha")
    val activeVoiceModel: StateFlow<String> = _activeVoiceModel

    private var audioJob: Job? = null

    // Real conversational response lookup maps
    private val dictionary = mapOf(
        "muraho, amakuru yawe?" to mapOf("English" to "Hello, how are you?", "French" to "Bonjour, comment ça va?"),
        "hello, how are you?" to mapOf("Kinyarwanda" to "Muraho, amakuru yawe?", "French" to "Bonjour, comment ça va?"),
        "i am fine. where are you?" to mapOf("Kinyarwanda" to "Meze neza. Uri he?", "Swahili" to "Niko salama. Uko wapi?"),
        "meze neza. uri he?" to mapOf("English" to "I am fine. Where are you?", "Swahili" to "Niko salama. Uko wapi?")
    )

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun updateSpeechSpeed(speed: Float) {
        _speechSpeed.value = speed
    }

    fun updateVoiceVolume(vol: Float) {
        _voiceVolume.value = vol
    }

    fun cycleVoiceModel() {
        _activeVoiceModel.value = if (_activeVoiceModel.value.contains("Alpha")) {
            "Neural Voice Beta (HD)"
        } else {
            "Premium AI Voice Alpha"
        }
    }

    fun updateLanguages(mine: String, remote: String) {
        _myLang.value = mine
        _remoteLang.value = remote
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleSpeaker() {
        _isSpeakerPhone.value = !_isSpeakerPhone.value
    }

    fun login(email: String) {
        viewModelScope.launch {
            _currentUser.value = UserModel("user_seth", email.split("@")[0].uppercase(), email, "Kinyarwanda", "🇷🇼")
            _isAuthenticated.value = true
        }
    }

    fun signUp(name: String, email: String, lang: String) {
        viewModelScope.launch {
            _currentUser.value = UserModel("user_new", name, email, lang, if (lang == "English") "🇬🇧" else "🇷🇼")
            _isAuthenticated.value = true
        }
    }

    fun logout() {
        _isAuthenticated.value = false
        _currentUser.value = null
    }

    fun startCall(phoneNumber: String, simCard: SimCard? = null) {
        val success = telecomCallManager.placeCall(phoneNumber, simCard)
        if (success) {
            _callState.value = CallState.CONNECTING
            _remoteUser.value = UserModel(phoneNumber, phoneNumber, "", "English", "👤")
            _transcript.value = emptyList()
        }
    }

    fun startCall(contact: UserModel) {
        startCall(contact.uid)
    }

    fun getAvailableSims(): List<SimCard> {
        return telecomCallManager.getAvailableSims()
    }

    fun requestDefaultDialerRole(): Intent? {
        return telecomCallManager.requestDefaultDialerRole()
    }

    fun isDefaultDialer(): Boolean {
        return telecomCallManager.isDefaultDialer()
    }

    fun updateCallState(state: CallState) {
        _callState.value = state
    }

    fun toggleListening() {
        if (_pipelineStatus.value == PipelineStatus.LISTENING) {
            _pipelineStatus.value = PipelineStatus.STANDBY
            speechRecognitionService.stopSpeechRecognition()
            audioJob?.cancel()
            _audioWaveLevel.value = 0.0f
        } else {
            _pipelineStatus.value = PipelineStatus.LISTENING
            startWaveOscillations()
            
            viewModelScope.launch {
                speechRecognitionService.startSpeechRecognition(_myLang.value).collect { recognizedText ->
                    if (recognizedText.isNotBlank()) {
                        _pipelineStatus.value = PipelineStatus.TRANSLATING
                        speechRecognitionService.stopSpeechRecognition()
                        
                        val translation = translateText(recognizedText, _remoteLang.value, _myLang.value)
                        injectLocalSpeech(recognizedText, translation)
                        
                        _pipelineStatus.value = PipelineStatus.SPEAKING
                        textToSpeechService.speak(translation, _remoteLang.value).collect { success ->
                            _pipelineStatus.value = PipelineStatus.STANDBY
                            _audioWaveLevel.value = 0.0f
                            audioJob?.cancel()
                        }
                    }
                }
            }
        }
    }

    private fun startWaveOscillations() {
        audioJob?.cancel()
        audioJob = viewModelScope.launch {
            var tick = 0
            while (isActive) {
                _audioWaveLevel.value = if (tick % 4 == 0) 0.85f else if (tick % 2 == 0) 0.45f else 0.15f
                delay(120)
                tick++
            }
        }
    }

    private fun injectLocalSpeech(original: String, translated: String) {
        val now = LocalDateTime.now()
        val timeStr = String.format("%02d:%02d", now.hour, now.minute)
        val newList = _transcript.value.toMutableList().apply {
            add(TranscriptMessage("You", original, translated, timeStr, true))
        }
        _transcript.value = newList

        if (original.lowercase().trim() == "meze neza. uri he?") {
            viewModelScope.launch {
                delay(3000)
                injectRemoteMessage("I am fine. Where are you?", "Meze neza. Uri he?")
            }
        }
    }

    private fun injectRemoteMessage(original: String, translated: String) {
        val now = LocalDateTime.now()
        val timeStr = String.format("%02d:%02d", now.hour, now.minute)
        val newList = _transcript.value.toMutableList().apply {
            add(TranscriptMessage(_remoteUser.value?.name ?: "John", original, translated, timeStr, false))
        }
        _transcript.value = newList
    }

    fun clearTranscripts() {
        _transcript.value = emptyList()
    }

    fun endCall() {
        _callState.value = CallState.DISCONNECTED
        viewModelScope.launch {
            delay(400)
            _callState.value = CallState.IDLE
            _remoteUser.value = null
            _pipelineStatus.value = PipelineStatus.STANDBY
            audioJob?.cancel()
        }
    }

    // Language Detection
    suspend fun detectLanguage(text: String): String {
        return try {
            languageDetectionService.detectLanguage(text)
        } catch (e: Exception) {
            "en"
        }
    }

    // Translation
    suspend fun translateText(text: String, targetLanguage: String, sourceLanguage: String? = null): String {
        return try {
            translationService.translateText(text, targetLanguage, sourceLanguage)
        } catch (e: Exception) {
            text
        }
    }

    // Contacts
    fun loadContacts(): List<Contact> {
        return runBlocking {
            contactsService.getAllContacts()
        }
    }

    fun loadCallLogs(): List<com.example.calltranslator.service.CallLogEntry> {
        return runBlocking {
            callLogService.getRecentCalls()
        }
    }

    fun searchContacts(query: String): List<Contact> {
        return runBlocking {
            contactsService.searchContacts(query)
        }
    }

    suspend fun loadCallLog(limit: Int = 50): List<com.example.calltranslator.service.CallLogEntry> {
        return try {
            callLogService.getRecentCalls(limit)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchCallLog(query: String): List<com.example.calltranslator.service.CallLogEntry> {
        return try {
            callLogService.searchCallLog(query)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        languageDetectionService.close()
        translationService.close()
    }
}
