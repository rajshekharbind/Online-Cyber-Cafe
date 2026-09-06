package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(onBack: () -> Unit) {
    var twoFactorEnabled by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy & Security", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Security Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            SecurityToggleItem(
                title = "Two-Factor Authentication",
                subtitle = "Add an extra layer of security to your account",
                checked = twoFactorEnabled,
                onCheckedChange = { twoFactorEnabled = it }
            )
            
            SecurityToggleItem(
                title = "Biometric Login",
                subtitle = "Use fingerprint or face ID to login quickly",
                checked = biometricEnabled,
                onCheckedChange = { biometricEnabled = it }
            )

            HorizontalDivider()

            Text("Data Privacy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            PrivacyLinkItem("Privacy Policy", "Read our commitment to your data")
            PrivacyLinkItem("Terms of Service", "Our rules for using the platform")
            PrivacyLinkItem("Data Export", "Download a copy of your personal data")
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedButton(
                onClick = { /* Change Password */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Change Account Password")
            }
        }
    }
}

@Composable
fun SecurityToggleItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun PrivacyLinkItem(title: String, subtitle: String) {
    Card(
        onClick = { /* Open Link */ },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF1976D2))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBack: () -> Unit, onNavigateToChat: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1976D2))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("How can we help you?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("We're here to assist with your applications", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }

            Text("Common Issues", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            SupportActionCard("Raise a Support Ticket", "App status, missing OTP, etc.", Icons.Default.ConfirmationNumber) // Req 49
            SupportActionCard("Payment Failed", "Help with transaction issues", Icons.Default.Payment)
            SupportActionCard("Job Application Status", "Tracking your form submission", Icons.Default.Info)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Human-First Support", fontWeight = FontWeight.Bold, fontSize = 18.sp) // Req 51

            Button(
                onClick = onNavigateToChat,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Talk to a Human (Live Chat)")
            }

            OutlinedButton(
                onClick = { /* Call Support */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Talk to a Human (Call Us)")
            }
        }
    }
}

@Composable
fun SupportActionCard(title: String, subtitle: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
