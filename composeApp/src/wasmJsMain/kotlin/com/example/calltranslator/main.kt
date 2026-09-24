package com.example.calltranslator

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App(viewModel = LinguaPhoneViewModel())
    }
}
