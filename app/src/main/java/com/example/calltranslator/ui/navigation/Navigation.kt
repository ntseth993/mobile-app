package com.example.calltranslator.ui.navigation

sealed class Screen(val route: String, val title: String = "") {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Phone : Screen("phone", "Phone")
    object Contacts : Screen("contacts", "Contacts")
    object Recents : Screen("recents", "Recents")
    object Settings : Screen("settings", "Settings")
    object Call : Screen("call", "Call")
    object IncomingCall : Screen("incoming_call", "Incoming Call")
}
