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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.model.UserModel
import com.example.calltranslator.ui.theme.*
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var myLang by remember { mutableStateOf("Kinyarwanda") }
    var myFlag by remember { mutableStateOf("🇷🇼") }
    var remoteLang by remember { mutableStateOf("English") }
    var remoteFlag by remember { mutableStateOf("🇬🇧") }
    var isSwapped by remember { mutableStateOf(false) }

    val rotationAngle by animateFloatAsState(
        targetValue = if (isSwapped) 180f else 0f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "swap_rotate"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌍 ", fontSize = 24.sp)
                        Text("LinguaPhone", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                            contentDescription = "Theme",
                            tint = PrimaryNeon
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Rounded.SettingsSuggest, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "👋 Hello, ${currentUser?.name ?: "Seth"}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "AI Translation Pipeline active",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .background(AppGradients.PremiumGradient, CircleShape)
                    ) {
                        Text(currentUser?.avatarUrl ?: "🇷🇼", fontSize = 22.sp)
                    }
                }
            }

            // Translation mapping deck box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TRANSLATION CHANNEL MAP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            color = PrimaryNeon
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MiniLanguageNode(myFlag, myLang, "My Language")
                            
                            IconButton(
                                onClick = {
                                    isSwapped = !isSwapped
                                    val tL = myLang
                                    val tF = myFlag
                                    myLang = remoteLang
                                    myFlag = remoteFlag
                                    remoteLang = tL
                                    remoteFlag = tF
                                },
                                modifier = Modifier
                                    .rotate(rotationAngle)
                                    .background(PrimaryNeon, CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Rounded.SwapHoriz, contentDescription = "Swap", tint = DarkBackground)
                            }

                            MiniLanguageNode(remoteFlag, remoteLang, "Target Language")
                        }
                    }
                }
            }

            // Pulsing Call trigger button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val contact = viewModel.contacts[1] // Eric Munyaneza
                        viewModel.updateLanguages(myLang, remoteLang)
                        viewModel.startCall(contact)
                        navController.navigate(Screen.Call.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppGradients.PremiumGradient, RoundedCornerShape(30.dp))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PhoneInTalk, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("START TRANSLATION CALL", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "SECURE INSTANT CONTACTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            // Contacts lists map
            items(viewModel.contacts) { contact ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    ListItem(
                        headlineContent = { Text(contact.name, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("Prefers ${contact.preferredLanguage}") },
                        leadingContent = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            ) {
                                Text(contact.avatarUrl, fontSize = 20.sp)
                            }
                        },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(if (contact.isOnline) SuccessGreen else Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                IconButton(onClick = {
                                    viewModel.updateLanguages(myLang, contact.preferredLanguage)
                                    viewModel.startCall(contact)
                                    navController.navigate(Screen.Call.route)
                                }) {
                                    Icon(Icons.Rounded.PhoneForwarded, contentDescription = "Call", tint = PrimaryNeon)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MiniLanguageNode(flag: String, name: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(flag, fontSize = 34.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(sub, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
    }
}
