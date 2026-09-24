package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@Composable
fun IncomingCallScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val remoteUser by viewModel.remoteUser.collectAsState()
    
    // Pulse animation for avatar
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )
    
    // Blink animation for "Incoming call" text
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "text_blink"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0F1A),
                        Color(0xFF1A1A2E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Caller avatar with pulse effect
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00D4FF),
                                Color(0xFF0099CC)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    remoteUser?.avatarUrl ?: "👤",
                    fontSize = 64.sp
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Caller name
            Text(
                text = remoteUser?.name ?: "Unknown",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Phone number
            Text(
                text = remoteUser?.uid ?: "",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Incoming call text with blink effect
            Text(
                text = "Incoming call...",
                fontSize = 18.sp,
                color = Color(0xFF00D4FF).copy(alpha = alpha),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decline button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.endCall()
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF44336))
                    ) {
                        Icon(
                            Icons.Rounded.CallEnd,
                            contentDescription = "Decline",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Decline",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Accept button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = {
                            viewModel.startCall(remoteUser ?: com.example.calltranslator.model.UserModel(
                                uid = "unknown",
                                name = "Unknown",
                                email = "",
                                preferredLanguage = "English",
                                avatarUrl = "👤"
                            ))
                            navController.navigate(Screen.Call.route)
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    ) {
                        Icon(
                            Icons.Rounded.Phone,
                            contentDescription = "Accept",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Accept",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Message button
            TextButton(
                onClick = { /* Show message options */ }
            ) {
                Text(
                    text = "Message",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
