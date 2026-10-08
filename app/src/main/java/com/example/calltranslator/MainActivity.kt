package com.example.calltranslator

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calltranslator.service.CallService
import com.example.calltranslator.ui.navigation.MainNavigationHost
import com.example.calltranslator.ui.theme.CallTranslatorTheme
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle permission results
    }

    private var pendingPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissions()

        // Start CallService to register PhoneAccount
        val serviceIntent = Intent(this, CallService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        // Handle incoming call intents or default dialer selection
        handleIntent(intent)

        setContent {
            val viewModel: LinguaPhoneViewModel = viewModel(
                factory = LinguaPhoneViewModelFactory(applicationContext)
            )
            App(viewModel = viewModel, pendingPhoneNumber = pendingPhoneNumber)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            when (it.action) {
                Intent.ACTION_DIAL, Intent.ACTION_CALL -> {
                    // Handle dial intent - extract number
                    val number = it.data?.schemeSpecificPart ?: ""
                    pendingPhoneNumber = number
                }
                TelecomManager.ACTION_CHANGE_DEFAULT_DIALER -> {
                    // Default dialer selection completed
                    // The app will be relaunched, no action needed here
                }
                else -> {
                    // Check for phone number in extras
                    val number = it.getStringExtra("number")
                    if (number != null) {
                        pendingPhoneNumber = number
                    }
                }
            }
        }
    }
    
    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECORD_AUDIO
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_NUMBERS)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val neededPermissions = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (neededPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(neededPermissions.toTypedArray())
        }
    }
}
