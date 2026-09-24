package com.example.calltranslator.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calltranslator.ui.components.BottomNavigationBar
import com.example.calltranslator.ui.screens.*
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@Composable
fun MainNavigationHost(viewModel: LinguaPhoneViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController, viewModel = viewModel)
        }
        
        // Main screens with bottom navigation
        composable(Screen.Phone.route) {
            MainScreenWithBottomBar(navController, viewModel) {
                PhoneScreen(navController = navController, viewModel = viewModel)
            }
        }
        composable(Screen.Contacts.route) {
            MainScreenWithBottomBar(navController, viewModel) {
                ContactsScreen(navController = navController, viewModel = viewModel)
            }
        }
        composable(Screen.Recents.route) {
            MainScreenWithBottomBar(navController, viewModel) {
                RecentsScreen(navController = navController, viewModel = viewModel)
            }
        }
        composable(Screen.Settings.route) {
            MainScreenWithBottomBar(navController, viewModel) {
                SettingsScreen(navController = navController, viewModel = viewModel)
            }
        }
        
        // Full screen screens (no bottom navigation)
        composable(Screen.Call.route) {
            CallScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.IncomingCall.route) {
            IncomingCallScreen(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun MainScreenWithBottomBar(
    navController: NavController,
    viewModel: LinguaPhoneViewModel,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Box(
            modifier = androidx.compose.ui.Modifier
                .padding(paddingValues)
        ) {
            content()
        }
    }
}
