package com.example.calltranslator.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Home : Screen("home")
    object Call : Screen("call")
    object Settings : Screen("settings")
}
