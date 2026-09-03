package com.example.myapplication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    onNavigateToJobs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToDocs: () -> Unit,
    onNavigateToMaster: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var avatarUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        avatarUri = uri
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Jobs") }, selected = false, onClick = onNavigateToJobs)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = false, onClick = onNavigateToProfile)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Waves and Greeting
            Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                WaveHeader()
                
                Column(modifier = Modifier.padding(24.dp).align(Alignment.CenterStart)) {
                    Text("Welcome back,", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
                    Text("Rahul Kumar", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { onNavigateToNotifications() }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("3 New Updates", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .padding(24.dp)
                        .size(60.dp)
                        .align(Alignment.TopEnd)
                        .clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.3f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (avatarUri != null) {
                            coil.compose.AsyncImage(
                                model = avatarUri,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-40).dp)) {
                // Quick Action Cards
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    HomeActionCard("Find Jobs", "Browse active government & private openings", Icons.Default.Search, Color(0xFF1976D2), Modifier.weight(1f), onNavigateToJobs)
                    HomeActionCard("Doc Vault", "Securely manage all your certificates", Icons.Default.Description, Color(0xFF4527A0), Modifier.weight(1f), onNavigateToDocs)
                }
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    HomeActionCard("Payments", "Receipts and history", Icons.Default.Payments, Color(0xFF388E3C), Modifier.weight(1f), onNavigateToPayments)
                    HomeActionCard("Settings", "App preferences", Icons.Default.Settings, Color(0xFF607D8B), Modifier.weight(1f), onNavigateToSettings)
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Status Widget
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToMaster() },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Profile Completion", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { 0.82f },
                                modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text("82%", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Text("Missing: 12th Roll Number", fontSize = 11.sp, color = Color.Red, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text("Closing Soon", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFD32F2F))
                Spacer(Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth().clickable { onNavigateToJobs() }, colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)), shape = RoundedCornerShape(12.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, null, tint = Color(0xFFE65100))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("SSC CGL 2024", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Deadline: Tomorrow. You haven't applied.", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Ongoing Applications", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("View All", fontSize = 13.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Medium, modifier = Modifier.clickable { onNavigateToHistory() }.padding(4.dp))
                }
                Spacer(Modifier.height(12.dp))
                
                OngoingAppItem("SSC CGL 2024", "Awaiting Portal Login", 0.6f, Color(0xFFFFA000), onClick = { onNavigateToTracking("SSC") })
                OngoingAppItem("IBPS PO XIV", "Documents Uploading", 0.3f, Color(0xFF1976D2), onClick = { onNavigateToTracking("IBPS") })
                
                Spacer(Modifier.height(24.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToSupport() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HelpCenter, null, tint = Color(0xFF2E7D32))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Need Assistance?", fontWeight = FontWeight.Bold)
                            Text("Talk to our expert executives", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaveHeader() {
    Canvas(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF1976D2), Color(0xFF1E88E5))))) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.7f)
            quadraticTo(size.width * 0.25f, size.height * 0.6f, size.width * 0.5f, size.height * 0.8f)
            quadraticTo(size.width * 0.75f, size.height * 0.95f, size.width, size.height * 0.75f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path, color = Color.White.copy(alpha = 0.1f))
        
        val path2 = Path().apply {
            moveTo(0f, size.height * 0.85f)
            quadraticTo(size.width * 0.35f, size.height * 0.75f, size.width * 0.6f, size.height * 0.9f)
            quadraticTo(size.width * 0.85f, size.height * 1f, size.width, size.height * 0.85f)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(path2, color = Color.White.copy(alpha = 0.05f))
    }
}

@Composable
fun HomeActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.height(130.dp),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(color = color.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(18.dp)) }
            }
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 10.sp, color = Color.Gray, lineHeight = 12.sp)
            }
        }
    }
}

@Composable
fun OngoingAppItem(title: String, status: String, progress: Float, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(status, fontSize = 11.sp, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp), color = color)
            }
            Spacer(Modifier.width(16.dp))
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}
