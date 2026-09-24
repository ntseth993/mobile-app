package com.example.calltranslator.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.calltranslator.service.Contact
import com.example.calltranslator.ui.navigation.Screen
import com.example.calltranslator.viewmodel.LinguaPhoneViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController, viewModel: LinguaPhoneViewModel) {
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        contacts = viewModel.loadContacts()
    }

    val filteredContacts = remember(searchQuery, contacts) {
        if (searchQuery.isEmpty()) contacts
        else contacts.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.phoneNumber.contains(searchQuery, ignoreCase = true) 
        }
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
                text = "Contacts",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            IconButton(onClick = { /* Add contact */ }) {
                Icon(
                    Icons.Rounded.PersonAdd,
                    contentDescription = "Add Contact",
                    tint = Color(0xFF00D4FF)
                )
            }
        }

        // Search bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search contacts...") },
                leadingIcon = { 
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        tint = Color(0xFF00D4FF)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contacts list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredContacts) { contact ->
                ModernContactListItem(
                    contact = contact,
                    onClick = {
                        viewModel.startCall(
                            com.example.calltranslator.model.UserModel(
                                uid = contact.phoneNumber,
                                name = contact.name,
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
fun ModernContactListItem(contact: Contact, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            IconButton(onClick = onClick) {
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
