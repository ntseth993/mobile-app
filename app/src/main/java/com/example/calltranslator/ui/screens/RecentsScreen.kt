package com.example.calltranslator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.service.CallLogEntry
import com.example.calltranslator.service.CallType
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentsScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    var recentCalls by remember { mutableStateOf<List<CallLogEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        recentCalls = viewModel.loadCallLog(50)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1A))
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recents",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = { /* Clear history */ }) {
                Icon(
                    Icons.Rounded.Delete,
                    contentDescription = "Clear History",
                    tint = Color(0xFF00D4FF)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Call history list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recentCalls) { call ->
                ModernCallRecordItem(
                    call = call,
                    onCallClick = {
                        viewModel.startCall(
                            com.example.calltranslator.model.UserModel(
                                uid = call.phoneNumber,
                                name = call.name ?: "Unknown",
                                email = "",
                                preferredLanguage = "English",
                                avatarUrl = "👤"
                            )
                        )
                        navController.navigate(Screen.Call.route)
                    }
                )
            }
        }
    }
}

@Composable
fun ModernCallRecordItem(call: CallLogEntry, onCallClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        when (call.type) {
                            CallType.INCOMING -> Color(0xFF4CAF50)
                            CallType.OUTGOING -> Color(0xFF2196F3)
                            CallType.MISSED -> Color(0xFFF44336)
                            CallType.VOICEMAIL -> Color(0xFF9C27B0)
                            CallType.REJECTED -> Color(0xFFFF9800)
                            CallType.BLOCKED -> Color(0xFF607D8B)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (call.type) {
                        CallType.INCOMING -> Icons.Rounded.CallReceived
                        CallType.OUTGOING -> Icons.Rounded.CallMade
                        CallType.MISSED -> Icons.Rounded.PhoneMissed
                        CallType.VOICEMAIL -> Icons.Rounded.Voicemail
                        CallType.REJECTED -> Icons.Rounded.PhoneDisabled
                        CallType.BLOCKED -> Icons.Rounded.Block
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = call.name ?: "Unknown",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = formatCallTime(call.timestamp, call.duration),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onCallClick) {
                Icon(
                    Icons.Rounded.Phone,
                    contentDescription = "Call",
                    tint = Color(0xFF00D4FF),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

fun formatCallTime(timestamp: Long, duration: Long): String {
    val date = Date(timestamp)
    val timeFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val timeStr = timeFormat.format(date)
    
    val durationStr = if (duration > 0) {
        val minutes = duration / 60
        val seconds = duration % 60
        " • ${minutes}m ${seconds}s"
    } else {
        ""
    }
    
    return timeStr + durationStr
}
