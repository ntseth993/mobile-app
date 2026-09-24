package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.model.UserModel
import com.example.calltranslator.service.Contact
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var phoneNumber by remember { mutableStateOf("") }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var showContacts by remember { mutableStateOf(false) }
    var isTranslating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contacts = viewModel.loadContacts()
    }

    val filteredContacts = remember(phoneNumber, contacts) {
        if (phoneNumber.isEmpty()) emptyList()
        else contacts.filter { 
            it.name.contains(phoneNumber, ignoreCase = true) || 
            it.phoneNumber.contains(phoneNumber, ignoreCase = true) 
        }
    }

    var myLang by remember { mutableStateOf("Kinyarwanda") }
    var remoteLang by remember { mutableStateOf("English") }

    // AI Feature: Auto-detect language from input
    LaunchedEffect(phoneNumber) {
        if (phoneNumber.length > 3) {
            isTranslating = true
            kotlinx.coroutines.delay(500)
            isTranslating = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F1A))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // AI Status Indicator
        if (isTranslating) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFF00D4FF)
                )
                Text(
                    text = "AI Detecting...",
                    fontSize = 12.sp,
                    color = Color(0xFF00D4FF),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Phone number display with AI suggestions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatPhoneNumber(phoneNumber),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                // AI-powered contact suggestion
                if (phoneNumber.length >= 3 && filteredContacts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00D4FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "AI Suggestion: ${filteredContacts.first().name}",
                            fontSize = 12.sp,
                            color = Color(0xFF00D4FF)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Language selector with AI translation status
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ModernLanguageChip(
                    label = myLang,
                    flag = "🇷🇼",
                    selected = true,
                    onClick = { }
                )
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF00D4FF)
                )
                ModernLanguageChip(
                    label = remoteLang,
                    flag = "🇬🇧",
                    selected = true,
                    onClick = { }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Modern dial pad
        ModernDialPad(
            onNumberClick = { digit ->
                if (phoneNumber.length < 15) {
                    phoneNumber += digit
                }
            },
            onDeleteClick = {
                if (phoneNumber.isNotEmpty()) {
                    phoneNumber = phoneNumber.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Modern call button with gradient
        val scale by animateFloatAsState(
            targetValue = if (phoneNumber.isNotEmpty()) 1f else 0.9f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 300f),
            label = "call_button"
        )

        Box(
            modifier = Modifier
                .size(72.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF00D4FF),
                            Color(0xFF0099CC)
                        )
                    )
                )
                .clickable(
                    enabled = phoneNumber.isNotEmpty(),
                    onClick = {
                        viewModel.updateLanguages(myLang, remoteLang)
                        val contact = UserModel(
                            uid = phoneNumber,
                            name = phoneNumber,
                            email = "",
                            preferredLanguage = remoteLang,
                            avatarUrl = "📞"
                        )
                        viewModel.startCall(contact)
                        navController.navigate(Screen.Call.route)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.Phone,
                contentDescription = "Call",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // Contacts dropdown
        if (showContacts && filteredContacts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(filteredContacts.take(10)) { contact ->
                        ModernContactItem(
                            contact = contact,
                            onClick = {
                                phoneNumber = contact.phoneNumber
                                showContacts = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernDialPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit
) {
    val buttons = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                row.forEach { digit ->
                    ModernDialButton(
                        digit = digit,
                        onClick = { onNumberClick(digit) }
                    )
                }
            }
        }
        
        // Delete button
        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                Icons.Rounded.Backspace,
                contentDescription = "Delete",
                tint = Color(0xFF00D4FF),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ModernDialButton(digit: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A2E))
            .border(2.dp, Color(0xFF00D4FF), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ModernLanguageChip(label: String, flag: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF00D4FF).copy(alpha = 0.2f) else Color.Transparent,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(
            1.dp, Color(0xFF00D4FF).copy(alpha = 0.3f)
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(flag, fontSize = 20.sp)
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00D4FF)
            )
        }
    }
}

@Composable
fun ModernContactItem(contact: Contact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
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
            Text(
                contact.name.first().toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = contact.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = contact.phoneNumber,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
        Icon(
            Icons.Rounded.Phone,
            contentDescription = "Call",
            tint = Color(0xFF00D4FF)
        )
    }
}

fun formatPhoneNumber(phoneNumber: String): String {
    if (phoneNumber.isEmpty()) return ""
    if (phoneNumber.length <= 3) return phoneNumber
    if (phoneNumber.length <= 6) return "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3)}"
    return "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3, 6)} ${phoneNumber.substring(6)}"
}
