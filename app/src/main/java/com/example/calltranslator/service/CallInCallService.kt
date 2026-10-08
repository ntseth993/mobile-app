package com.example.calltranslator.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.Call.Details
import android.telecom.InCallService
import android.telecom.VideoProfile
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@RequiresApi(Build.VERSION_CODES.M)
class CallInCallService : InCallService() {

    companion object {
        private var instance: CallInCallService? = null
        private val _activeCall = MutableStateFlow<Call?>(null)
        val activeCall: StateFlow<Call?> = _activeCall

        private val _callState = MutableStateFlow<CallState>(CallState.IDLE)
        val callState: StateFlow<CallState> = _callState

        private val _callNumber = MutableStateFlow("")
        val callNumber: StateFlow<String> = _callNumber

        fun getInstance(): CallInCallService? = instance
    }

    enum class CallState { IDLE, DIALING, RINGING, ACTIVE, DISCONNECTED }

    private val callCallback = object : Call.Callback() {
        override fun onStateChanged(call: Call, state: Int) {
            when (state) {
                Call.STATE_RINGING -> {
                    _callState.value = CallState.RINGING
                }
                Call.STATE_DIALING -> {
                    _callState.value = CallState.DIALING
                }
                Call.STATE_ACTIVE -> {
                    _callState.value = CallState.ACTIVE
                }
                Call.STATE_DISCONNECTED -> {
                    _callState.value = CallState.DISCONNECTED
                    _activeCall.value = null
                }
            }
        }
    }

    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        instance = this
        _activeCall.value = call
        _callNumber.value = call.details.handle.schemeSpecificPart
        call.registerCallback(callCallback)

        // Start call UI activity
        val intent = Intent(this, com.example.calltranslator.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("action", if (call.details.callDirection == Details.DIRECTION_INCOMING) "incoming" else "outgoing")
            putExtra("number", call.details.handle.schemeSpecificPart)
        }
        startActivity(intent)
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        call.unregisterCallback(callCallback)
        if (_activeCall.value == call) {
            _activeCall.value = null
            _callState.value = CallState.IDLE
            _callNumber.value = ""
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _activeCall.value?.unregisterCallback(callCallback)
        _activeCall.value = null
        _callState.value = CallState.IDLE
    }
}
