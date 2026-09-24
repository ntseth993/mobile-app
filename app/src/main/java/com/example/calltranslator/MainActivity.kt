package com.example.calltranslator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calltranslator.ui.navigation.MainNavigationHost
import com.example.calltranslator.ui.theme.CallTranslatorTheme
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: LinguaPhoneViewModel = viewModel(
                factory = LinguaPhoneViewModelFactory(applicationContext)
            )
            App(viewModel = viewModel)
        }
    }
}
