package com.example.calltranslator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.ui.theme.AppGradients
import com.example.calltranslator.ui.theme.DarkSurface
import com.example.calltranslator.ui.theme.PrimaryNeon
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("seth@linguaphone.ai") }
    var password by remember { mutableStateOf("password123") }
    var name by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Skip auth for now and go directly to Phone screen
        navController.navigate(Screen.Phone.route) {
            popUpTo(Screen.Auth.route) { inclusive = true }
        }
    }

    Scaffold { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryNeon.copy(alpha = 0.1f), CircleShape)
            ) {
                Text("🔐", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isSignUp) "Create Secure Account" else "Welcome Back",
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSignUp) "Join the next generation of voice calling" else "Sign in to access your secure AI pipelines",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            if (isSignUp) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = PrimaryNeon) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Rounded.Mail, contentDescription = null, tint = PrimaryNeon) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Secure Password") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null, tint = PrimaryNeon) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        if (isSignUp) {
                            viewModel.signUp(name, email, "Kinyarwanda")
                        } else {
                            viewModel.login(email)
                        }
                        isLoading = false
                        navController.navigate(Screen.Phone.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppGradients.PremiumGradient, RoundedCornerShape(16.dp))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isSignUp) "REGISTER NOW" else "SECURE LOGIN",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(
                    text = if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    color = PrimaryNeon,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
