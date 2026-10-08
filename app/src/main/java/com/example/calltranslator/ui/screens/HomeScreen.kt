package com.example.calltranslator.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.model.UserModel
import com.example.calltranslator.service.Contact
import com.example.calltranslator.service.SimCard
import com.example.calltranslator.ui.components.SimSelectionDialog
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneScreen(navController: NavController, viewModel: LinguaPhoneViewModel, initialNumber: String? = null) {
    val context = LocalContext.current
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val currentUser by viewModel.currentUser.collectAsState()

    var phoneNumber by remember { mutableStateOf(initialNumber ?: "") }
    var showContacts by remember { mutableStateOf(false) }
    var showSimSelection by remember { mutableStateOf(false) }
    var recentCalls by remember { mutableStateOf<List<com.example.calltranslator.service.CallLogEntry>>(emptyList()) }
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var showContextMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contacts = viewModel.loadContacts()
        recentCalls = viewModel.loadCallLogs()
    }

    val filteredContacts = remember(phoneNumber, contacts) {
        if (phoneNumber.isEmpty()) emptyList()
        else contacts.filter {
            it.name.contains(phoneNumber, ignoreCase = true) ||
                it.phoneNumber.contains(phoneNumber, ignoreCase = true)
        }
    }

    val backgroundBrush = if (isDarkMode) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B0E14),
                Color(0xFF121A2A),
                Color(0xFF0F172A)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF4F7FC),
                Color(0xFFE9EEF8),
                Color(0xFFF7FAFF)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF00E5FF), Color(0xFF7000FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "LinguaPhone",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF0A2540)
                        )
                        Text(
                            text = "AI call translator",
                            fontSize = 12.sp,
                            color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF0A2540).copy(alpha = 0.6f)
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleTheme() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.7f)
                        )
                ) {
                    Icon(
                        if (isDarkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                        contentDescription = "Toggle theme",
                        tint = if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF0A2540),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Header stats cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumStatCard(
                    title = "Calls",
                    value = "24",
                    subtitle = "Today",
                    icon = Icons.Rounded.Call,
                    gradient = listOf(Color(0xFF00E5FF), Color(0xFF0099CC)),
                    modifier = Modifier.weight(1f)
                )
                PremiumStatCard(
                    title = "AI",
                    value = "96%",
                    subtitle = "Live",
                    icon = Icons.Rounded.Psychology,
                    gradient = listOf(Color(0xFF7000FF), Color(0xFF4A148C)),
                    modifier = Modifier.weight(1f)
                )
                PremiumStatCard(
                    title = "Ready",
                    value = "24/7",
                    subtitle = "Support",
                    icon = Icons.Rounded.CheckCircle,
                    gradient = listOf(Color(0xFF05FF9B), Color(0xFF1FAF79)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionChip(
                    label = "Dial",
                    icon = Icons.Rounded.Dialpad,
                    selected = true,
                    onClick = { }
                )
                QuickActionChip(
                    label = "Contacts",
                    icon = Icons.Rounded.Contacts,
                    selected = false,
                    onClick = { showContacts = !showContacts }
                )
                QuickActionChip(
                    label = "Recents",
                    icon = Icons.Rounded.History,
                    selected = false,
                    onClick = { navController.navigate(Screen.Recents.route) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Phone number panel
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp), ambientColor = Color(0xFF00E5FF).copy(alpha = 0.15f)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF171E2B) else Color.White
                ),
                border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val scrollState = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        showContextMenu = true
                                    }
                                )
                            }
                    ) {
                        Text(
                            text = phoneNumber.ifEmpty { "Enter number" },
                            fontSize = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkMode) Color.White else Color(0xFF0A2540),
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Visible
                        )
                    }

                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(phoneNumber))
                        },
                        enabled = phoneNumber.isNotEmpty()
                    ) {
                        Icon(
                            Icons.Rounded.ContentCopy,
                            contentDescription = "Copy",
                            tint = if (phoneNumber.isNotEmpty()) (if (isDarkMode) Color.White else Color(0xFF0A2540)) else Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboardText = clipboardManager.getText()?.text ?: ""
                            if (clipboardText.isNotEmpty()) {
                                phoneNumber += clipboardText.filter { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }
                            }
                        }
                    ) {
                        Icon(
                            Icons.Rounded.ContentPaste,
                            contentDescription = "Paste",
                            tint = if (isDarkMode) Color.White else Color(0xFF0A2540),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = showContextMenu,
                onDismissRequest = { showContextMenu = false },
                modifier = Modifier.background(if (isDarkMode) Color(0xFF171E2B) else Color.White)
            ) {
                DropdownMenuItem(
                    text = { Text("Copy", color = if (isDarkMode) Color.White else Color.Black) },
                    onClick = {
                        clipboardManager.setText(AnnotatedString(phoneNumber))
                        showContextMenu = false
                    },
                    leadingIcon = { Icon(Icons.Rounded.ContentCopy, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Paste", color = if (isDarkMode) Color.White else Color.Black) },
                    onClick = {
                        val clipboardText = clipboardManager.getText()?.text ?: ""
                        if (clipboardText.isNotEmpty()) {
                            phoneNumber += clipboardText.filter { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }
                        }
                        showContextMenu = false
                    },
                    leadingIcon = { Icon(Icons.Rounded.ContentPaste, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Clear", color = if (isDarkMode) Color.White else Color.Black) },
                    onClick = {
                        phoneNumber = ""
                        showContextMenu = false
                    },
                    leadingIcon = { Icon(Icons.Rounded.Clear, contentDescription = null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dial pad
            ModernDialPad(
                onNumberClick = { digit ->
                    if (phoneNumber.length < 100) {
                        phoneNumber += digit
                    }
                },
                onDeleteClick = {
                    if (phoneNumber.isNotEmpty()) {
                        phoneNumber = phoneNumber.dropLast(1)
                    }
                },
                isDarkMode = isDarkMode
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Call button
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .clip(CircleShape)
                    .shadow(16.dp, CircleShape, ambientColor = Color(0xFF00E5FF).copy(alpha = 0.45f))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00E5FF),
                                Color(0xFF0099CC)
                            )
                        )
                    )
                    .clickable(
                        enabled = phoneNumber.isNotEmpty(),
                        onClick = {
                            if (!viewModel.isDefaultDialer()) {
                                val roleIntent = viewModel.requestDefaultDialerRole()
                                roleIntent?.let { context.startActivity(it) }
                            } else {
                                val availableSims = viewModel.getAvailableSims()
                                if (availableSims.size > 1) {
                                    showSimSelection = true
                                } else {
                                    viewModel.startCall(phoneNumber, availableSims.firstOrNull())
                                    navController.navigate(Screen.Call.route)
                                }
                            }
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Call,
                    contentDescription = "Call",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Recent calls card
            if (recentCalls.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkMode) Color(0xFF161D2D) else Color.White
                    ),
                    border = BorderStroke(1.dp, if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent calls",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) Color.White else Color(0xFF0A2540)
                        )
                        TextButton(onClick = { navController.navigate(Screen.Recents.route) }) {
                            Text(
                                text = "See all",
                                fontSize = 12.sp,
                                color = Color(0xFF00E5FF),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)) {
                        recentCalls.take(3).forEach { call ->
                            RecentCallItem(
                                call = call,
                                onClick = { phoneNumber = call.phoneNumber },
                                isDarkMode = isDarkMode
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    }

    if (showContacts && filteredContacts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
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

    if (showSimSelection) {
        val availableSims = viewModel.getAvailableSims()
        SimSelectionDialog(
            sims = availableSims,
            onSimSelected = { sim ->
                viewModel.startCall(phoneNumber, sim)
                showSimSelection = false
                navController.navigate(Screen.Call.route)
            },
            onDismiss = { showSimSelection = false }
        )
    }
}

@Composable
private fun PremiumStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(horizontal = 12.dp, vertical = 14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) Color(0xFF00E5FF).copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, if (selected) Color(0xFF00E5FF).copy(alpha = 0.35f) else Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) Color(0xFF00E5FF) else Color.White, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color(0xFF00E5FF) else Color.White
            )
        }
    }
}

@Composable
fun ModernDialPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    isDarkMode: Boolean
) {
    val buttons = listOf(
        listOf("1" to "", "2" to "ABC", "3" to "DEF"),
        listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
        listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
        listOf("*" to "", "0" to "+", "#" to "")
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { (digit, letters) ->
                    ModernDialButton(
                        digit = digit,
                        letters = letters,
                        onClick = { onNumberClick(digit) },
                        isDarkMode = isDarkMode
                    )
                }
            }
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                Icons.Rounded.Backspace,
                contentDescription = "Delete",
                tint = Color(0xFF00E5FF),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun ModernDialButton(digit: String, letters: String, onClick: () -> Unit, isDarkMode: Boolean) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (isDarkMode) Color(0xFF171E2B) else Color(0xFFE7EEF8))
            .border(
                1.dp,
                if (isDarkMode) Color.White.copy(alpha = 0.08f) else Color(0xFFCBD5E1),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = digit,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDarkMode) Color.White else Color(0xFF0A2540)
            )
            if (letters.isNotEmpty()) {
                Text(
                    text = letters,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color(0xFF0A2540).copy(alpha = 0.55f),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ModernLanguageChip(label: String, flag: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
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
                color = Color(0xFF00E5FF)
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
                            Color(0xFF00E5FF),
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
            tint = Color(0xFF00E5FF)
        )
    }
}

@Composable
fun RecentCallItem(call: com.example.calltranslator.service.CallLogEntry, onClick: () -> Unit, isDarkMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    if (isDarkMode) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.8f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                when (call.type) {
                    com.example.calltranslator.service.CallType.INCOMING -> Icons.Rounded.CallReceived
                    com.example.calltranslator.service.CallType.OUTGOING -> Icons.Rounded.CallMade
                    else -> Icons.Rounded.PhoneMissed
                },
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = call.name ?: call.phoneNumber,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = if (isDarkMode) Color.White else Color(0xFF0A2540)
            )
            Text(
                text = call.phoneNumber,
                fontSize = 12.sp,
                color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color(0xFF0A2540).copy(alpha = 0.6f)
            )
        }
        Text(
            text = call.date,
            fontSize = 11.sp,
            color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color(0xFF0A2540).copy(alpha = 0.65f)
        )
    }
}

fun formatPhoneNumber(phoneNumber: String): String {
    if (phoneNumber.isEmpty()) return ""
    if (phoneNumber.length <= 3) return phoneNumber
    if (phoneNumber.length <= 6) return "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3)}"
    return "${phoneNumber.substring(0, 3)} ${phoneNumber.substring(3, 6)} ${phoneNumber.substring(6)}"
}
