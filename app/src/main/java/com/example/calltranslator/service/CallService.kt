package com.example.calltranslator.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import androidx.core.app.NotificationCompat
import com.example.calltranslator.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CallService : Service() {

    private val CHANNEL_ID = "CallTranslatorChannel"
    private val NOTIFICATION_ID = 1
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private var isPhoneAccountRegistered = false

        fun registerPhoneAccount(context: Context) {
            if (isPhoneAccountRegistered) return

            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val componentName = ComponentName(context, MyConnectionService::class.java)
            val phoneAccountHandle = PhoneAccountHandle(componentName, "CallTranslatorAccount")

            val phoneAccount = PhoneAccount.Builder(
                phoneAccountHandle,
                "TranslateCall"
            ).apply {
                setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
                setShortDescription("TranslateCall")
            }.build()

            telecomManager.registerPhoneAccount(phoneAccount)
            isPhoneAccountRegistered = true
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        registerPhoneAccount(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Call Translator Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Service for managing calls and translation"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Call Translator")
            .setContentText("Active call with translation")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}

class MyConnectionService : ConnectionService() {

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val connection = MyConnection(request.address.toString())
        connection.setInitialized()
        connection.setDialing()
        return connection
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val connection = MyConnection(request.address.toString())
        connection.setInitialized()
        connection.setRinging()
        return connection
    }
}

class MyConnection(private val phoneNumber: String) : Connection() {

    override fun onShowIncomingCallUi() {
        super.onShowIncomingCallUi()
    }

    override fun onCallAudioStateChanged(state: android.telecom.CallAudioState?) {
        super.onCallAudioStateChanged(state)
    }

    override fun onAnswer() {
        super.onAnswer()
        setActive()
    }

    override fun onAnswer(videoState: Int) {
        super.onAnswer(videoState)
        setActive()
    }

    override fun onDisconnect() {
        super.onDisconnect()
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onAbort() {
        super.onAbort()
        setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
        destroy()
    }

    override fun onReject() {
        super.onReject()
        setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
        destroy()
    }

    override fun onHold() {
        super.onHold()
        setOnHold()
    }

    override fun onUnhold() {
        super.onUnhold()
        setActive()
    }
}
