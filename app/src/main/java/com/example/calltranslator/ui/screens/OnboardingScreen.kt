package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.ui.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> OnboardingPage(
                        title = "Break Language Barriers",
                        description = "Connect instantly with callers globally. Speak your native tongue, they hear theirs instantly.",
                        graphic = { BubblesGraphic() }
                    )
                    1 -> OnboardingPage(
                        title = "Speak Naturally",
                        description = "Our high fidelity continuous AI models capture every natural inflection without delays.",
                        graphic = { MicWavesGraphic() }
                    )
                    2 -> OnboardingPage(
                        title = "Understand Each Other",
                        description = "Two-way autonomous audio pipelines translate and play speech concurrently both ways.",
                        graphic = { MorphingGraphic() }
                    )
                }
            }

            // Bottom control navigation dock
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
            ) {
                // Indicator dots
                Row {
                    repeat(3) { index ->
                        val active = pagerState.currentPage == index
                        val width = if (active) 24.dp else 8.dp
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(8.dp)
                                .width(width)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (active) PrimaryNeon else Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }

                // Forward floating round button
                Button(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < 2) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                navController.navigate(Screen.Auth.route) {
                                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppGradients.PremiumGradient, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (pagerState.currentPage == 2) Icons.Rounded.Done else Icons.Rounded.ArrowForward,
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPage(title: String, description: String, graphic: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(5f)
                .fillMaxWidth()
        ) {
            graphic()
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(4f)
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun BubblesGraphic() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = (-40).dp, y = (-20).dp)
                .size(70.dp)
                .background(DarkSurface, CircleShape)
                .border(2.dp, PrimaryNeon, CircleShape)
        ) {
            Text("🇷🇼", fontSize = 32.sp)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(x = 40.dp, y = 20.dp)
                .size(70.dp)
                .background(DarkSurface, CircleShape)
                .border(2.dp, SecondaryNeon, CircleShape)
        ) {
            Text("🇬🇧", fontSize = 32.sp)
        }
    }
}

@Composable
fun MicWavesGraphic() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_graphic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Box(modifier = Modifier.size(140.dp * pulseScale).background(PrimaryNeon.copy(alpha = 0.08f), CircleShape))
        Box(modifier = Modifier.size(110.dp * pulseScale).background(SecondaryNeon.copy(alpha = 0.12f), CircleShape))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .background(AppGradients.PremiumGradient, CircleShape)
        ) {
            Icon(Icons.Rounded.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
        }
    }
}

@Composable
fun MorphingGraphic() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = (-40).dp)
                .size(60.dp)
                .background(DarkSurface, CircleShape)
                .border(2.dp, AccentPink, CircleShape)
        ) {
            Text("🇫🇷", fontSize = 26.sp)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = 40.dp)
                .size(60.dp)
                .background(DarkSurface, CircleShape)
                .border(2.dp, SuccessGreen, CircleShape)
        ) {
            Text("🇰🇪", fontSize = 26.sp)
        }
    }
}
