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
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Deck
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                }
                ConnectionIndicator(state = callState)
                Spacer(modifier = Modifier.width(48.dp))
            }

            // Remote Profile Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    val infiniteTransition = rememberInfiniteTransition(label = "ring_glow")
                    val ringScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.25f,
                        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
                        label = "ring"
                    )
                    Box(
                        modifier = Modifier
                            .size(100.dp * ringScale)
                            .border(1.5.dp, PrimaryNeon.copy(alpha = (1.25f - ringScale).coerceIn(0f, 1f)), CircleShape)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(90.dp)
                            .background(DarkSurface, CircleShape)
                            .border(2.5.dp, PrimaryNeon, CircleShape)
                    ) {
                        Text(remoteUser?.avatarUrl ?: "👤", fontSize = 42.sp)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = remoteUser?.name ?: "Connecting...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = remoteUser?.preferredLanguage ?: "Target Language",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryNeon,
                    letterSpacing = 1.sp
                )
            }

            // Transcript Scroll Box Panel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                    .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(28.dp))
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(transcript) { msg ->
                        TranslationBubbleItem(msg)
                    }
                }
            }

            // Active Pipeline Tracker Wave row
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                PipelineStatusLabel(status = pipelineStatus)
                Spacer(modifier = Modifier.height(6.dp))
                AnimatedSoundWave(
                    isActive = pipelineStatus == PipelineStatus.LISTENING || pipelineStatus == PipelineStatus.SPEAKING,
                    color = if (pipelineStatus == PipelineStatus.SPEAKING) SuccessGreen else PrimaryNeon
                )
            }

            // Controller Orbs Footer bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp, top = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlOrb(
                    icon = if (isMuted) Icons.Rounded.MicOff else Icons.Rounded.Mic,
                    isActive = isMuted,
                    activeColor = AccentPink,
                    onTap = { viewModel.toggleMute() }
                )

                AnimatedMic(
                    isListening = pipelineStatus == PipelineStatus.LISTENING,
                    level = audioLevel,
                    onTap = { viewModel.toggleListening() }
                )

                ControlOrb(
                    icon = Icons.Rounded.Close,
                    isActive = true,
                    activeColor = AccentPink,
                    isLarge = true,
                    onTap = {
                        viewModel.endCall()
                        navController.popBackStack()
                    }
                )

                ControlOrb(
                    icon = if (isSpeaker) Icons.Rounded.VolumeUp else Icons.Rounded.VolumeDown,
                    isActive = false,
                    onTap = { viewModel.toggleSpeaker() }
                )
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
