package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.model.UserModel
import com.example.calltranslator.model.TranscriptMessage
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.CallState
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel
import com.example.calltranslator.viewmodel.PipelineStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val callState by viewModel.callState.collectAsState()
    val remoteUser by viewModel.remoteUser.collectAsState()
    val transcript by viewModel.transcript.collectAsState()
    val pipelineStatus by viewModel.pipelineStatus.collectAsState()
    val audioWaveLevel by viewModel.audioWaveLevel.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeakerPhone by viewModel.isSpeakerPhone.collectAsState()
    val myLang by viewModel.myLang.collectAsState()
    val remoteLang by viewModel.remoteLang.collectAsState()

    var isTranslationEnabled by remember { mutableStateOf(true) }
    var showKeypad by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }

    // iPhone colors
    val iPhoneDark = Color(0xFF000000)
    val iPhoneGray = Color(0xFF1C1C1E)
    val iPhoneGrayLight = Color(0xFF2C2C2E)
    val iPhoneGreen = Color(0xFF34C759)
    val iPhoneRed = Color(0xFFFF3B30)
    val iPhoneBlue = Color(0xFF0A84FF)

    LaunchedEffect(callState) {
        if (callState == CallState.CONNECTED) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                elapsedSeconds++
            }
        }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val activeCall = com.example.calltranslator.service.CallInCallService.activeCall.collectAsState()

    LaunchedEffect(transcript.size) {
        if (transcript.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(transcript.size - 1)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(iPhoneDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top header with close button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Close",
                        tint = iPhoneBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(1.dp))

                Text(
                    text = when (callState) {
                        CallState.CONNECTING -> "Connecting..."
                        CallState.CONNECTED -> formatTimeiPhone(elapsedSeconds)
                        CallState.DISCONNECTED -> "Call ended"
                        else -> "Call"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Semibold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(1.dp))

                IconButton(
                    onClick = {
                        activeCall.value?.disconnect()
                        viewModel.endCall()
                        navController.popBackStack()
                    },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "End Call",
                        tint = iPhoneRed,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Center section - caller info
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Caller avatar with glow
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            ambientColor = iPhoneBlue.copy(alpha = 0.3f),
                            spotColor = iPhoneBlue.copy(alpha = 0.3f)
                        )
                        .background(
                            Brush.radialGradient(
                                colors = listOf(iPhoneBlue, Color(0xFF0066FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(remoteUser?.avatarUrl ?: "👤", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Caller name
                Text(
                    text = remoteUser?.name ?: "Unknown",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Caller number
                Text(
                    text = remoteUser?.uid ?: "",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Regular
                )
            }

            // AI Translation panel
            if (isTranslationEnabled && transcript.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = iPhoneGrayLight
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI Translation",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Semibold,
                                color = iPhoneBlue
                            )
                            Text(
                                text = when (pipelineStatus) {
                                    PipelineStatus.LISTENING -> "🎙️ Listening"
                                    PipelineStatus.TRANSLATING -> "🤖 Translating"
                                    PipelineStatus.SPEAKING -> "🔊 Speaking"
                                    else -> "⏸️ Ready"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f),
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .background(iPhoneGray.copy(alpha = 0.8f))
                                .padding(12.dp)
                        ) {
                            items(transcript) { msg ->
                                iPhoneTranscriptBubble(msg, iPhoneBlue)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bottom controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // First row of controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    iPhoneControlButton(
                        icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        label = "Mute",
                        isActive = isMuted,
                        onClick = { viewModel.toggleMute() }
                    )
                    iPhoneControlButton(
                        icon = Icons.Rounded.Dialpad,
                        label = "Keypad",
                        isActive = showKeypad,
                        onClick = { showKeypad = !showKeypad }
                    )
                    iPhoneControlButton(
                        icon = if (isSpeakerPhone) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeDown,
                        label = "Speaker",
                        isActive = isSpeakerPhone,
                        onClick = { viewModel.toggleSpeaker() }
                    )
                    iPhoneControlButton(
                        icon = Icons.Rounded.Translate,
                        label = "AI",
                        isActive = isTranslationEnabled,
                        onClick = { isTranslationEnabled = !isTranslationEnabled }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End call button - red and large
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .shadow(
                            elevation = 20.dp,
                            shape = CircleShape,
                            ambientColor = iPhoneRed.copy(alpha = 0.4f),
                            spotColor = iPhoneRed.copy(alpha = 0.4f)
                        )
                        .background(iPhoneRed)
                        .clickable {
                            activeCall.value?.disconnect()
                            viewModel.endCall()
                            navController.popBackStack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun iPhoneControlButton(
    icon: androidx.compose.material.icons.Icons,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val iPhoneGrayLight = Color(0xFF2C2C2E)
    val iPhoneBlue = Color(0xFF0A84FF)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(iPhoneGrayLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isActive) iPhoneBlue else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isActive) iPhoneBlue else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isActive) FontWeight.Semibold else FontWeight.Regular
        )
    }
}

@Composable
fun iPhoneTranscriptBubble(msg: TranscriptMessage, iPhoneBlue: Color) {
    val alignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleShape = if (msg.isMe) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 18.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(bubbleShape)
                .background(
                    if (msg.isMe) iPhoneBlue else Color(0xFF1C1C1E)
                )
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = msg.originalText,
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Regular
                )
                Spacer(modifier = Modifier.height(6.dp))
                Divider(
                    color = Color.White.copy(alpha = 0.2f),
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = msg.translatedText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (msg.isMe) Color.White else iPhoneBlue
                )
            }
        }
    }
}

fun formatTimeiPhone(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, secs)
    } else {
        String.format("%02d:%02d", minutes, secs)
    }
}
