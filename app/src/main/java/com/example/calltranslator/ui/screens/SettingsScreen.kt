package com.example.calltranslator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var myLanguage by remember { mutableStateOf("Kinyarwanda") }
    var targetLanguage by remember { mutableStateOf("English") }
    var translationEnabled by remember { mutableStateOf(true) }
    var speechSpeed by remember { mutableStateOf(1.0f) }
    var saveHistory by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1A))
            .verticalScroll(rememberScrollState())
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
                text = "Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Translation Settings
            ModernSettingsCard(title = "Translation") {
                Column {
                    // My Language
                    var showMyLangMenu by remember { mutableStateOf(false) }
                    ModernSettingItem(
                        icon = Icons.Rounded.Translate,
                        title = "My Language",
                        subtitle = myLanguage,
                        onClick = { showMyLangMenu = true }
                    )
                    
                    DropdownMenu(
                        expanded = showMyLangMenu,
                        onDismissRequest = { showMyLangMenu = false }
                    ) {
                        listOf("Kinyarwanda", "English", "French").forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = { myLanguage = lang; showMyLangMenu = false }
                            )
                        }
                    }
                    
                    // Target Language
                    var showTargetLangMenu by remember { mutableStateOf(false) }
                    ModernSettingItem(
                        icon = Icons.Rounded.Language,
                        title = "Target Language",
                        subtitle = targetLanguage,
                        onClick = { showTargetLangMenu = true }
                    )
                    
                    DropdownMenu(
                        expanded = showTargetLangMenu,
                        onDismissRequest = { showTargetLangMenu = false }
                    ) {
                        listOf("Kinyarwanda", "English", "French").forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = { targetLanguage = lang; showTargetLangMenu = false }
                            )
                        }
                    }
                    
                    // Translation Toggle
                    ModernSwitchItem(
                        icon = Icons.Rounded.AutoAwesome,
                        title = "Enable Translation",
                        checked = translationEnabled,
                        onCheckedChange = { translationEnabled = it }
                    )
                }
            }
            
            // Voice Settings
            ModernSettingsCard(title = "Voice") {
                Column {
                    // Speech Speed
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Speed,
                                contentDescription = null,
                                tint = Color(0xFF00D4FF)
                            )
                            Text(
                                "Speech Speed",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            "${(speechSpeed * 100).toInt()}%",
                            color = Color(0xFF00D4FF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = speechSpeed,
                        onValueChange = { speechSpeed = it },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF00D4FF),
                            thumbColor = Color(0xFF00D4FF)
                        )
                    )
                }
            }
            
            // Privacy Settings
            ModernSettingsCard(title = "Privacy") {
                ModernSwitchItem(
                    icon = Icons.Rounded.Security,
                    title = "Save Translation History",
                    subtitle = "Store translated conversations",
                    checked = saveHistory,
                    onCheckedChange = { saveHistory = it }
                )
            }
            
            // Appearance
            ModernSettingsCard(title = "Appearance") {
                ModernSwitchItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Dark Mode",
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.toggleTheme() }
                )
            }
            
            // About
            ModernSettingsCard(title = "About") {
                ModernSettingItem(
                    icon = Icons.Rounded.Info,
                    title = "Version",
                    subtitle = "1.0.0",
                    onClick = { }
                )
            }
        }
    }
}

@Composable
fun ModernSettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00D4FF)
            )
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun ModernSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00D4FF),
                            Color(0xFF0099CC)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
        }
        Icon(
            Icons.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF00D4FF).copy(alpha = 0.5f)
        )
    }
}

@Composable
fun ModernSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00D4FF),
                            Color(0xFF0099CC)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00D4FF),
                checkedTrackColor = Color(0xFF00D4FF).copy(alpha = 0.3f)
            )
        )
    }
}
