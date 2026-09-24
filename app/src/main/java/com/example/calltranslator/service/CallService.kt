package com.example.calltranslator.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.telecom.Connection
import android.telecom.ConnectionService
import android.telecom.TelecomManager
import androidx.core.app.NotificationCompat
import com.example.calltranslator.MainActivity

class CallService : Service() {
    
    private val CHANNEL_ID = "CallTranslatorChannel"
    private val NOTIFICATION_ID = 1
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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
        connectionManagerPhoneAccount: android.telecom.PhoneAccountHandle,
        request: android.telecom.ConnectionRequest
    ): Connection? {
        val connection = MyConnection()
        connection.setInitialized()
        return connection
    }
    
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: android.telecom.PhoneAccountHandle,
        request: android.telecom.ConnectionRequest
    ): Connection? {
        val connection = MyConnection()
        connection.setInitialized()
        return connection
    }
}

class MyConnection : Connection() {
    override fun onCallAudioStateChanged(state: android.telecom.CallAudioState?) {
        super.onCallAudioStateChanged(state)
    }
    
    override fun onAnswer() {
        setActive()
    }
    
    override fun onDisconnect() {
        destroy()
    }
}
