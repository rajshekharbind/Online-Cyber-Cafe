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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterProfileScreen(onBack: () -> Unit, onNavigateToNotifications: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Personal", "Address", "Category")

    // Req 58: Profile Change Audit Trail
    val profileAuditLog = remember { mutableStateListOf<String>() }
    var showAuditLog by remember { mutableStateOf(false) }
    
    // Dynamic Profile State
    val personalInfo = remember { mutableStateMapOf(
        "Full Name" to "Rahul Kumar", "Father's Name" to "", "Mother's Name" to "",
        "DOB" to "15/08/2002", "Gender" to "Male", "Mobile Number" to "",
        "Email" to "rahul.k@example.com", "Nationality" to "Indian", "Marital Status" to "Single"
    ) }
    
    val addressInfo = remember { mutableStateMapOf(
        "Current Address" to "", "Permanent Address" to "", "Village/Town" to "", 
        "District" to "", "State" to "", "PIN Code" to ""
    ) }
    
    val categoryInfo = remember { mutableStateMapOf(
        "Category" to "OBC", "Sub-category" to "", "EWS Information" to "", 
        "PwD Information" to "No", "Ex-serviceman" to "No"
    ) }

    val allFields = personalInfo.values + addressInfo.values + categoryInfo.values
    val filledFieldsCount = allFields.count { it.isNotBlank() }
    val completionPercentage = (filledFieldsCount.toFloat() / allFields.size.toFloat() * 100).toInt()
    val missingFields = (personalInfo + addressInfo + categoryInfo).filter { it.value.isBlank() }.keys.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Master Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Completion Status
            LinearProgressIndicator(
                progress = { completionPercentage / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (completionPercentage == 100) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Profile Completion: $completionPercentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (missingFields.isNotEmpty()) {
                    Text("Missing: ${missingFields.first()}", fontSize = 12.sp, color = Color.Red)
                } else {
                    Text("Profile 100% Complete!", fontSize = 12.sp, color = Color(0xFF4CAF50))
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> AuditedInfoSection(personalInfo, profileAuditLog)
                    1 -> AuditedInfoSection(addressInfo, profileAuditLog)
                    2 -> AuditedInfoSection(categoryInfo, profileAuditLog)
                }
            }

            // Req 58: Audit Log Section
            if (profileAuditLog.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = Color(0xFF6A1B9A), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Profile Edit History (${profileAuditLog.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6A1B9A))
                            }
                            TextButton(onClick = { showAuditLog = !showAuditLog }) {
                                Text(if (showAuditLog) "Hide" else "Show", fontSize = 12.sp)
                            }
                        }
                        if (showAuditLog) {
                            profileAuditLog.takeLast(5).reversed().forEach { entry ->
                                Text("• $entry", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Profile")
            }
        }
    }
}

@Composable
fun InfoSection(stateMap: MutableMap<String, String>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stateMap.keys.forEach { label ->
            ProfileTextField(
                label = label, 
                value = stateMap[label] ?: "",
                onValueChange = { stateMap[label] = it }
            )
        }
    }
}

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

/** Req 58: Wraps InfoSection so every field edit is logged with a timestamp */
@Composable
fun AuditedInfoSection(stateMap: MutableMap<String, String>, auditLog: MutableList<String>) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stateMap.keys.forEach { label ->
            ProfileTextField(
                label = label,
                value = stateMap[label] ?: "",
                onValueChange = { newValue ->
                    val oldValue = stateMap[label] ?: ""
                    if (newValue != oldValue) {
                        stateMap[label] = newValue
                        val ts = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                        auditLog.add("[$ts] '$label' changed from '${oldValue.ifBlank { "(empty)" }}' to '${newValue.ifBlank { "(empty)" }}'")
                    }
                }
            )
        }
    }
}
