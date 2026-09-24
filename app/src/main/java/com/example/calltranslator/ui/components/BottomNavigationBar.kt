package com.example.calltranslator.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val screens = listOf(
        Screen.Phone,
        Screen.Contacts,
        Screen.Recents,
        Screen.Settings
    )

    val currentRoute = navController.currentBackStackEntry?.destination?.route

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF0F0F1A),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val selected = currentRoute == screen.route
                val scale by animateFloatAsState(
                    targetValue = if (selected) 1.1f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    label = "nav_item"
                )

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Phone.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) {
                                    Color(0xFF00D4FF)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (screen) {
                                Screen.Phone -> Icons.Rounded.Phone
                                Screen.Contacts -> Icons.Rounded.Contacts
                                Screen.Recents -> Icons.Rounded.History
                                Screen.Settings -> Icons.Rounded.Settings
                                else -> Icons.Rounded.Phone
                            },
                            contentDescription = screen.title,
                            tint = if (selected) Color.White else Color(0xFF00D4FF).copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = screen.title,
                        fontSize = 11.sp,
                        color = if (selected) Color(0xFF00D4FF) else Color.White.copy(alpha = 0.5f),
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
