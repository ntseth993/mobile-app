package com.example.calltranslator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calltranslator.model.TranscriptMessage
import com.example.calltranslator.model.UserModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class CallState { IDLE, CONNECTING, CONNECTED, WEAK_CONNECTION, RECONNECTING, DISCONNECTED }
enum class PipelineStatus { STANDBY, LISTENING, TRANSLATING, SPEAKING }

class LinguaPhoneViewModel : ViewModel() {

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

    fun startCall(contact: UserModel) {
        _remoteUser.value = contact
        _callState.value = CallState.CONNECTING
        _transcript.value = emptyList()

        viewModelScope.launch {
            delay(2000)
            _callState.value = CallState.CONNECTED
            injectRemoteMessage("Hello, how are you?", "Muraho, amakuru yawe?")
        }
    }

    fun toggleListening() {
        if (_pipelineStatus.value == PipelineStatus.LISTENING) {
            _pipelineStatus.value = PipelineStatus.STANDBY
            audioJob?.cancel()
            _audioWaveLevel.value = 0.0f
            
            // Process phrase simulation automatically
            viewModelScope.launch {
                val phrase = "Muraho, amakuru yawe?"
                _pipelineStatus.value = PipelineStatus.TRANSLATING
                delay(900)
                
                val translation = dictionary[phrase.lowercase()]?.get(_remoteLang.value) ?: "[Translated to ${_remoteLang.value}]: $phrase"
                injectLocalSpeech(phrase, translation)
                
                _pipelineStatus.value = PipelineStatus.SPEAKING
                startWaveOscillations()
                delay((translation.split(" ").size * 350 / _speechSpeed.value).toLong().coerceIn(1000, 3000))
                
                _pipelineStatus.value = PipelineStatus.STANDBY
                _audioWaveLevel.value = 0.0f
                audioJob?.cancel()
            }
        } else {
            _pipelineStatus.value = PipelineStatus.LISTENING
            startWaveOscillations()
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
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timeStr = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
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
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val timeStr = "${now.hour.toString().padStart(2, '0')}:${now.minute.toString().padStart(2, '0')}"
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
}
