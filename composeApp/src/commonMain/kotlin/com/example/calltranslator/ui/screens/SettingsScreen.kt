package com.example.calltranslator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.ui.theme.PrimaryNeon
import com.example.calltranslator.ui.theme.SuccessGreen
import com.example.calltranslator.ui.theme.AccentPink
import com.example.calltranslator.ui.theme.SecondaryNeon
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val speechSpeed by viewModel.speechSpeed.collectAsState()
    val voiceVolume by viewModel.voiceVolume.collectAsState()
    val activeVoiceModel by viewModel.activeVoiceModel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Customization", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SectionHeaderLabel("VOICE TRANSCRIPTION OPTIONS")
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Speed, contentDescription = null, tint = PrimaryNeon)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Speech Rate speed", fontWeight = FontWeight.Bold)
                            Text("${speechSpeed}x speed multiplier", fontSize = 12.sp, color = Color.Gray)
                        }
                        Slider(
                            value = speechSpeed,
                            onValueChange = { viewModel.updateSpeechSpeed(it) },
                            valueRange = 0.5f..2.0f,
                            steps = 5,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(alpha = 0.1f)))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.VolumeUp, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Voice Synthesis Volume", fontWeight = FontWeight.Bold)
                            Text("${(voiceVolume * 100).roundToInt()}% scale", fontSize = 12.sp, color = Color.Gray)
                        }
                        Slider(
                            value = voiceVolume,
                            onValueChange = { viewModel.updateVoiceVolume(it) },
                            valueRange = 0.0f..1.0f,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }
            }

            SectionHeaderLabel("AI CONFIGURATION ENGINE")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Active Speech Model", fontWeight = FontWeight.Bold) },
                        supportingContent = { Text(activeVoiceModel, fontSize = 12.sp, color = Color.Gray) },
                        leadingContent = { Icon(Icons.Rounded.RecordVoiceOver, contentDescription = null, tint = SecondaryNeon) },
                        trailingContent = { Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null) },
                        modifier = Modifier.clickable { viewModel.cycleVoiceModel() }
                    )
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.Gray.copy(alpha = 0.1f)))
                    ListItem(
                        headlineContent = { Text("Auto Language Sense", fontWeight = FontWeight.Bold) },
                        leadingContent = { Icon(Icons.Rounded.Autorenew, contentDescription = null, tint = PrimaryNeon) },
                        trailingContent = { Switch(checked = true, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedThumbColor = PrimaryNeon)) }
                    )
                }
            }

            SectionHeaderLabel("SECURITY & PERSONAL PRIVACY")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ListItem(
                    headlineContent = { Text("Purge Cached Transcripts", fontWeight = FontWeight.Bold, color = AccentPink) },
                    supportingContent = { Text("Permanently delete live voice log files", fontSize = 12.sp, color = Color.Gray) },
                    leadingContent = { Icon(Icons.Rounded.DeleteSweep, contentDescription = null, tint = AccentPink) },
                    modifier = Modifier.clickable {
                        viewModel.clearTranscripts()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(
                    text = "LinguaPhone App v1.0.0 (Production Ready)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SectionHeaderLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp,
        color = Color.Gray,
        modifier = Modifier.padding(start = 4.dp)
    )
}
