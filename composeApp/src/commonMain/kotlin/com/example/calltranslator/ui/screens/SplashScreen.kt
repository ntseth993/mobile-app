package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.ui.theme.AppGradients
import com.example.calltranslator.ui.theme.DarkBackground
import com.example.calltranslator.ui.theme.PrimaryNeon
import com.example.calltranslator.ui.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scaleAnim = remember { Animatable(0.2f) }
    val textFadeAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1.0f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
        textFadeAnim.animateTo(1.0f, animationSpec = tween(1000))
        delay(1200)
        navController.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF151C2C), DarkBackground),
                    radius = 1200f
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .scale(scaleAnim.value)
                    .background(AppGradients.PremiumGradient, CircleShape)
            ) {
                Text("🌍", fontSize = 60.sp)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "LinguaPhone",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Speak your language. Everyone understands.",
                fontSize = 15.sp,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = PrimaryNeon.copy(alpha = textFadeAnim.value),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}
