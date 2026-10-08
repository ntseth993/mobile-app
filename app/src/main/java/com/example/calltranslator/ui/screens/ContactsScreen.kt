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
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var contacts by remember { mutableStateOf<List<Contact>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddContactDialog by remember { mutableStateOf(false) }

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
            .background(if (isDarkMode) Color(0xFF0F0F1A) else Color(0xFFF5F5F5))
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
                color = if (isDarkMode) Color.White else Color.Black
            )
            IconButton(onClick = { showAddContactDialog = true }) {
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
            colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1A1A2E) else Color.White)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search contacts...", color = if (isDarkMode) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)) },
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
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = if (isDarkMode) Color.White else Color.Black,
                    unfocusedTextColor = if (isDarkMode) Color.White else Color.Black
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
                    onCallClick = {
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
                    },
                    onDetailsClick = {
                        // Show contact details
                    },
                    isDarkMode = isDarkMode
                )
            }
        }
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        AddContactDialog(
            onDismiss = { showAddContactDialog = false },
            onAddContact = { name, phone ->
                // Add contact logic would go here
                showAddContactDialog = false
            }
        )
    }
}

@Composable
fun ModernContactListItem(contact: Contact, onCallClick: () -> Unit, onDetailsClick: () -> Unit, isDarkMode: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDarkMode) Color(0xFF1A1A2E) else Color.White),
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onDetailsClick)
            ) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = if (isDarkMode) Color.White else Color.Black
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = if (isDarkMode) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
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

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAddContact: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Contact",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAddContact(name, phone) },
                enabled = name.isNotBlank() && phone.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF1A1A2E),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}
