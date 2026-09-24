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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.model.TranscriptMessage
import com.example.calltranslator.ui.components.AnimatedMic
import com.example.calltranslator.ui.components.AnimatedSoundWave
import com.example.calltranslator.ui.components.ConnectionIndicator
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.viewmodel.CallState
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel
import com.example.calltranslator.viewmodel.PipelineStatus
import kotlinx.coroutines.launch

@Composable
fun CallScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val callState by viewModel.callState.collectAsState()
    val remoteUser by viewModel.remoteUser.collectAsState()
    val transcript by viewModel.transcript.collectAsState()
    val pipelineStatus by viewModel.pipelineStatus.collectAsState()
    val audioLevel by viewModel.audioWaveLevel.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()
    val isSpeaker by viewModel.isSpeakerPhone.collectAsState()
    var isTranslationEnabled by remember { mutableStateOf(false) }
    var showKeypad by remember { mutableStateOf(false) }

    val myLang by viewModel.myLang.collectAsState()
    val remoteLang by viewModel.remoteLang.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Scroll to bottom when transcripts grow
    LaunchedEffect(transcript.size) {
        if (transcript.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(transcript.size - 1)
            }
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F0F1A), Color(0xFF1A1A2E))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = when (callState) {
                        CallState.CONNECTING -> "Connecting..."
                        CallState.CONNECTED -> "Connected"
                        else -> "Call"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Caller info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFF00D4FF), Color(0xFF0099CC))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(remoteUser?.avatarUrl ?: "👤", fontSize = 56.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = remoteUser?.name ?: "Unknown",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = remoteUser?.uid ?: "",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }

            // Translation panel (shown when enabled)
            if (isTranslationEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "$myLang → $remoteLang",
                                fontSize = 14.sp,
                                color = Color(0xFF00D4FF),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (pipelineStatus == PipelineStatus.LISTENING) "Listening..." 
                                      else if (pipelineStatus == PipelineStatus.TRANSLATING) "Translating..."
                                      else "Ready",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F0F1A))
                                .padding(12.dp)
                        ) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(transcript) { msg ->
                                    TranslationBubbleItem(msg)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Call controls - iPhone style
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // First row: Mute, Keypad, Speaker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallControlButton(
                        icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                        label = "Mute",
                        isActive = isMuted,
                        onClick = { viewModel.toggleMute() }
                    )
                    CallControlButton(
                        icon = Icons.Rounded.Dialpad,
                        label = "Keypad",
                        isActive = showKeypad,
                        onClick = { showKeypad = !showKeypad }
                    )
                    CallControlButton(
                        icon = if (isSpeaker) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeDown,
                        label = "Speaker",
                        isActive = isSpeaker,
                        onClick = { viewModel.toggleSpeaker() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Second row: Add Call, Hold, Translate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CallControlButton(
                        icon = Icons.Rounded.Add,
                        label = "Add Call",
                        isActive = false,
                        onClick = { /* Add call */ }
                    )
                    CallControlButton(
                        icon = Icons.Rounded.Pause,
                        label = "Hold",
                        isActive = false,
                        onClick = { /* Hold */ }
                    )
                    CallControlButton(
                        icon = Icons.Rounded.Translate,
                        label = "Translate",
                        isActive = isTranslationEnabled,
                        activeColor = Color(0xFF00D4FF),
                        onClick = { isTranslationEnabled = !isTranslationEnabled }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // End call button
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
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TranslationBubbleItem(msg: TranscriptMessage) {
    val alignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
    val horizontalArrangement = if (msg.isMe) Arrangement.End else Arrangement.Start
    val bubbleShape = if (msg.isMe) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 20.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 12.dp),
        contentAlignment = alignment
    ) {
        Column(horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start) {
            Row(horizontalArrangement = horizontalArrangement, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = msg.senderName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (msg.isMe) PrimaryNeon else SecondaryNeon
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = msg.timestamp, fontSize = 10.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(bubbleShape)
                    .background(if (msg.isMe) DarkSurfaceLight.copy(alpha = 0.9f) else SecondaryNeon.copy(alpha = 0.12f))
                    .border(1.dp, if (msg.isMe) PrimaryNeon.copy(alpha = 0.2f) else SecondaryNeon.copy(alpha = 0.25f), bubbleShape)
                    .padding(14.dp)
            ) {
                Column {
                    Text(text = msg.originalText, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.1f)))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Translate, contentDescription = null, tint = PrimaryNeon, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = msg.translatedText, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = PrimaryNeon)
                    }
                }
            }
        }
    }
}

@Composable
fun PipelineStatusLabel(status: PipelineStatus) {
    val text = when (status) {
        PipelineStatus.LISTENING -> "LISTENING..."
        PipelineStatus.TRANSLATING -> "TRANSLATING AI PIPELINE..."
        PipelineStatus.SPEAKING -> "SYNTHESIZING AUDIO OUTPUT..."
        else -> "SYSTEM STANDBY"
    }
    val icon = when (status) {
        PipelineStatus.LISTENING -> Icons.Rounded.GraphicEq
        PipelineStatus.TRANSLATING -> Icons.Rounded.Psychology
        PipelineStatus.SPEAKING -> Icons.Rounded.VolumeUp
        else -> Icons.Rounded.CheckCircleOutline
    }
    val color = when (status) {
        PipelineStatus.LISTENING -> PrimaryNeon
        PipelineStatus.TRANSLATING -> SecondaryNeon
        PipelineStatus.SPEAKING -> SuccessGreen
        else -> Color.Gray
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color, letterSpacing = 0.8.sp)
    }
}

@Composable
fun CallControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = if (isActive) activeColor else Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isActive) activeColor else Color.White.copy(alpha = 0.7f),
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ControlOrb(icon: ImageVector, isActive: Boolean, activeColor: Color = PrimaryNeon, isLarge: Boolean = false, onTap: () -> Unit) {
    val size = if (isLarge) 64.dp else 54.dp
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isActive) activeColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f))
            .border(1.5.dp, if (isActive) activeColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable { onTap() }
    ) {
        Icon(icon, contentDescription = null, tint = if (isActive) activeColor else Color.White, modifier = Modifier.size(if (isLarge) 32.dp else 24.dp))
    }
}
