package com.example.calltranslator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.calltranslator.ui.navigation.MainNavigationHost
import com.example.calltranslator.ui.theme.CallTranslatorTheme
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@Composable
fun App(viewModel: LinguaPhoneViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    CallTranslatorTheme(darkTheme = isDarkMode) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainNavigationHost(viewModel = viewModel)
        }
    }
}
