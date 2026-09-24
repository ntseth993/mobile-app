package com.example.calltranslator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = PrimaryNeon,
    secondary = SecondaryNeon,
    background = DarkBackground,
    surface = DarkSurface,
    error = AccentPink,
    onSurface = Color.White,
    onBackground = Color.White
)

private val CustomLightColorScheme = lightColorScheme(
    primary = SecondaryDeep,
    secondary = PrimaryDeep,
    background = LightBackground,
    surface = LightSurface,
    error = AccentPink,
    onSurface = PrimaryDeep,
    onBackground = PrimaryDeep
)

object AppGradients {
    val PremiumGradient = Brush.linearGradient(
        colors = listOf(PrimaryNeon, SecondaryNeon)
    )
    val AlertGradient = Brush.linearGradient(
        colors = listOf(AccentPink, SecondaryNeon)
    )
    val LiveStreamGradient = Brush.linearGradient(
        colors = listOf(SuccessGreen, PrimaryNeon)
    )
}

@Composable
fun CallTranslatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) CustomDarkColorScheme else CustomLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
