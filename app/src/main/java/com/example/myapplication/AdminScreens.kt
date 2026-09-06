package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToPostJob: () -> Unit,
    onNavigateToManageStudents: () -> Unit,
    onNavigateToManageEmployees: () -> Unit,
    onNavigateToServiceCharges: () -> Unit,
    onNavigateToAuditLogs: () -> Unit,
    onNavigateToActiveJobs: () -> Unit,
    onNavigateToRefunds: () -> Unit,
    onNavigateToSupportRequests: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToApplicationDashboard: () -> Unit,
    onNavigateToEmployeeDashboard: () -> Unit,
    onNavigateToSupportDashboard: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Console", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle actual notification view */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Users Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total", "150", Icons.Default.People, Color(0xFF1976D2), Modifier.weight(1f))
                    StatCard("New", "12", Icons.Default.PersonAdd, Color(0xFF388E3C), Modifier.weight(1f))
                    StatCard("Active", "45", Icons.Default.DirectionsRun, Color(0xFFF57C00), Modifier.weight(1f))
                }
            }

            item {
                Text("Applications & Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Pending", "24", Icons.Default.PendingActions, Color(0xFFFFA000), Modifier.weight(1f))
                    StatCard("Submitted", "105", Icons.Default.CheckCircle, Color(0xFF4CAF50), Modifier.weight(1f))
                    StatCard("Failed", "5", Icons.Default.Error, Color(0xFFD32F2F), Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Active Jobs", "18", Icons.Default.Work, Color(0xFF009688), Modifier.weight(1f))
                    StatCard("Closing Soon", "3", Icons.Default.Warning, Color(0xFFE64A19), Modifier.weight(1f))
                }
            }
            
            item {
                Text("Revenue (Req 46)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Official", "₹45k", Icons.Default.AccountBalance, Color(0xFF303F9F), Modifier.weight(1f))
                    StatCard("Service", "₹12.5k", Icons.Default.MonetizationOn, Color(0xFF388E3C), Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Today", "₹2.1k", Icons.Default.Today, Color(0xFF0288D1), Modifier.weight(1f))
                    StatCard("Monthly", "₹35k", Icons.Default.DateRange, Color(0xFF7B1FA2), Modifier.weight(1f))
                }
            }

            item {
                Text("Employee Performance (Req 47)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                EmployeePerformanceCard(
                    name = "Executive Amit",
                    assigned = 120,
                    submitted = 105,
                    pending = 10,
                    failed = 5,
                    avgTime = "18 mins",
                    successRate = "87.5%"
                )
                Spacer(Modifier.height(8.dp))
                EmployeePerformanceCard(
                    name = "Executive Priya",
                    assigned = 90,
                    submitted = 85,
                    pending = 3,
                    failed = 2,
                    avgTime = "14 mins",
                    successRate = "94.4%"
                )
            }
            
            item {
                Text("Core Management", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            
            item {
                AdminActionCard("Application Dashboard", "Complete application list & filters", Icons.Default.Dashboard, Color(0xFF3F51B5), onClick = onNavigateToApplicationDashboard)
            }
            
            item {
                AdminActionCard("Employee Dashboard", "View task processing dashboard", Icons.Default.Work, Color(0xFF009688), onClick = onNavigateToEmployeeDashboard)
            }
            
            item {
                AdminActionCard("Manage Students", "150+ Users Registered", Icons.Default.People, Color(0xFF1976D2), onClick = onNavigateToManageStudents)
            }
            
            item {
                AdminActionCard("Manage Employees", "Staff Task Assignments", Icons.Default.Badge, Color(0xFF673AB7), onClick = onNavigateToManageEmployees)
            }

            item {
                AdminActionCard("Manage Jobs", "Active Listings & Deadlines", Icons.AutoMirrored.Filled.List, Color(0xFFE91E63), onClick = onNavigateToActiveJobs)
            }

            item {
                Text("System Control", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            item {
                AdminActionCard("Human Support Dashboard", "Tickets, live chat & student queries", Icons.Default.SupportAgent, Color(0xFF0277BD), onClick = onNavigateToSupportDashboard)
            }

            item {
                AdminActionCard("Manage Notifications", "Send Alerts to Students", Icons.Default.Campaign, Color(0xFF2196F3), onClick = onNavigateToNotifications)
            }

            item {
                AdminActionCard("Service Charges", "Edit Portal & Service Fees", Icons.Default.CurrencyRupee, Color(0xFF4CAF50), onClick = onNavigateToServiceCharges)
            }

            item {
                AdminActionCard("Process Refunds", "Handle student payment refunds", Icons.Default.MoneyOff, Color(0xFF9C27B0), onClick = onNavigateToRefunds)
            }

            item {
                AdminActionCard("System Reports", "Revenue & Processing Stats", Icons.Default.Assessment, Color(0xFFF44336), onClick = onNavigateToReports)
            }
            
            item {
                AdminActionCard("System Audit Logs", "View Operational History", Icons.Default.History, Color(0xFF607D8B), onClick = onNavigateToAuditLogs)
            }

            item {
                AdminActionCard("System Settings", "Configure App Parameters", Icons.Default.Settings, Color(0xFF424242), onClick = onNavigateToSettings)
            }

            item {
                AdminActionCard("Post New Job", "Create a new job entry for students", Icons.Default.Add, MaterialTheme.colorScheme.primary, onClick = onNavigateToPostJob)
            }
            
            item {
                Text("Assigned to Employees", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            
            val assignments = AssignmentStore.getAssignments()
            if (assignments.isEmpty()) {
                item { Text("No assignments yet.", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(assignments) { assignment ->
                    ApplicationAssignmentItem(
                        "${assignment.appId} -> ${assignment.assignedEmployee}", 
                        assignment.priority,
                        onReassign = {
                            onNavigateToApplicationDashboard() // Direct to dashboard for management
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicationAssignmentItem(title: String, priority: String, onReassign: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Priority: $priority", fontSize = 12.sp, color = if(priority == "High") Color.Red else Color.Gray)
            }
            Button(onClick = onReassign, modifier = Modifier.height(32.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("Manage", fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun EmployeePerformanceCard(
    name: String,
    assigned: Int,
    submitted: Int,
    pending: Int,
    failed: Int,
    avgTime: String,
    successRate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Assigned: $assigned", fontSize = 12.sp)
                Text("Submitted: $submitted", fontSize = 12.sp, color = Color(0xFF388E3C))
                Text("Pending: $pending", fontSize = 12.sp, color = Color(0xFFFFA000))
            }
            Spacer(Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Failed: $failed", fontSize = 12.sp, color = Color(0xFFD32F2F))
                Text("Avg Time: $avgTime", fontSize = 12.sp)
                Text("Success: $successRate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    title: String, 
    usersList: List<String>, 
    onBack: () -> Unit
) {
    var users by remember { mutableStateOf(usersList) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newUserName by remember { mutableStateOf("") }
    var selectedEmployeeForStats by remember { mutableStateOf<String?>(null) }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New User") },
            text = {
                OutlinedTextField(
                    value = newUserName,
                    onValueChange = { newUserName = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (newUserName.isNotBlank()) {
                        users = users + newUserName
                        newUserName = ""
                        showAddDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (selectedEmployeeForStats != null) {
        EmployeeStatsDialog(
            employeeName = selectedEmployeeForStats!!,
            onDismiss = { selectedEmployeeForStats = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                val isEmployee = title.contains("Employee")
                val stats = if(isEmployee) AssignmentStore.getStatsForEmployee(user) else null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isEmployee) { selectedEmployeeForStats = user },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, null) }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user, fontWeight = FontWeight.Bold)
                            if (isEmployee && stats != null) {
                                Text("Pending: ${stats.pending} | In Progress: ${stats.inProgress}", fontSize = 11.sp, color = Color.Gray)
                            } else {
                                Text("Active Status", fontSize = 11.sp, color = Color(0xFF4CAF50))
                            }
                        }
                        if(isEmployee) {
                            Icon(Icons.Default.BarChart, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        }
                        IconButton(onClick = { users = users.filter { it != user } }) { Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeStatsDialog(employeeName: String, onDismiss: () -> Unit) {
    val currentStats = AssignmentStore.getStatsForEmployee(employeeName)
    var isEditing by remember { mutableStateOf(false) }
    
    var total by remember { mutableStateOf(currentStats.total.toString()) }
    var pending by remember { mutableStateOf(currentStats.pending.toString()) }
    var inProgress by remember { mutableStateOf(currentStats.inProgress.toString()) }
    var waiting by remember { mutableStateOf(currentStats.waiting.toString()) }
    var submitted by remember { mutableStateOf(currentStats.submitted.toString()) }
    var failed by remember { mutableStateOf(currentStats.failed.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${employeeName}'s Performance", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                IconButton(onClick = { isEditing = !isEditing }) {
                    Icon(if (isEditing) Icons.Default.Close else Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                EditableStatRow("Total Assigned", total, Color(0xFF1976D2), isEditing) { total = it }
                EditableStatRow("Pending (Priority)", pending, Color(0xFFFFA000), isEditing) { pending = it }
                EditableStatRow("In Progress", inProgress, Color(0xFF2196F3), isEditing) { inProgress = it }
                EditableStatRow("Waiting for Student", waiting, Color(0xFF9C27B0), isEditing) { waiting = it }
                EditableStatRow("Successfully Submitted", submitted, Color(0xFF388E3C), isEditing) { submitted = it }
                EditableStatRow("Failed / Rejected", failed, Color(0xFFD32F2F), isEditing) { failed = it }
            }
        },
        confirmButton = {
            if (isEditing) {
                Button(onClick = {
                    AssignmentStore.updateEmployeeStats(
                        employeeName,
                        EmployeeStats(
                            total = total.toIntOrNull() ?: 0,
                            pending = pending.toIntOrNull() ?: 0,
                            inProgress = inProgress.toIntOrNull() ?: 0,
                            waiting = waiting.toIntOrNull() ?: 0,
                            submitted = submitted.toIntOrNull() ?: 0,
                            failed = failed.toIntOrNull() ?: 0
                        )
                    )
                    isEditing = false
                }) {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save Changes")
                }
            } else {
                Button(onClick = onDismiss, shape = RoundedCornerShape(24.dp)) { 
                    Text("Close", modifier = Modifier.padding(horizontal = 16.dp)) 
                }
            }
        }
    )
}

@Composable
fun EditableStatRow(label: String, value: String, color: Color, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
        if (isEditing) {
            OutlinedTextField(
                value = value,
                onValueChange = { if (it.all { char -> char.isDigit() }) onValueChange(it) },
                modifier = Modifier.width(70.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End, fontWeight = FontWeight.Bold),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = color)
            )
        } else {
            Text(value, fontWeight = FontWeight.Bold, color = color, fontSize = 16.sp)
        }
    }
}

@Composable
fun StatDetailRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Bold, color = color)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageServiceChargesScreen(onBack: () -> Unit) {
    val tiers = ServiceChargeEngine.tiers
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Success") },
            text = { Text("Pricing rules updated. Students will now see the new service fees immediately.") },
            confirmButton = {
                Button(onClick = { showSuccess = false; onBack() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Service Fees") },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tiered Pricing Rules", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1976D2))
            Text("Set how much you charge based on the job\u0027s official fee.", fontSize = 13.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(8.dp))

            tiers.forEachIndexed { index, tier ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val rangeText = if (tier.maxFee == Int.MAX_VALUE) "₹${tier.minFee}+" else "₹${tier.minFee} - ₹${tier.maxFee}"
                            Text("Official Fee: $rangeText", fontWeight = FontWeight.Medium)
                            Text("Student pays official + Service Fee", fontSize = 11.sp, color = Color.Gray)
                        }
                        
                        OutlinedTextField(
                            value = tier.serviceFee.toString(),
                            onValueChange = { newValue ->
                                val fee = newValue.filter { it.isDigit() }.toIntOrNull() ?: 0
                                ServiceChargeEngine.updateTierFee(index, fee)
                            },
                            label = { Text("Fee (₹)") },
                            modifier = Modifier.width(90.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { showSuccess = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Save Pricing Rules", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageNotificationsAdminScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedTarget by remember { mutableStateOf("All Students") }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Notification Sent") },
            text = { Text("Your broadcast message has been sent to all registered students.") },
            confirmButton = {
                Button(onClick = { showSuccess = false; onBack() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Send New Broadcast", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = message, onValueChange = { message = it }, label = { Text("Message Content") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            
            Text("Target Audience: $selectedTarget", color = Color.Gray)
            
            Button(
                onClick = { if(title.isNotBlank()) showSuccess = true }, 
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("Send Notification")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemReportsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Reports") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Monthly Statistics", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            ReportItem("Total Applications", "342", "+12% from last month", Color(0xFF1976D2))
            ReportItem("Success Rate", "94.5%", "High Performance", Color(0xFF388E3C))
            ReportItem("Total Revenue", "₹84,200", "Includes Service Charges", Color(0xFFFFA000))
            ReportItem("Failed Applications", "8", "Action Required", Color(0xFFD32F2F))
        }
    }
}

@Composable
fun ReportItem(label: String, value: String, subtitle: String, color: Color) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(4.dp, 40.dp).background(color))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, color = Color.Gray, fontSize = 12.sp)
                Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSettingsAdminScreen(onBack: () -> Unit) {
    var appName by remember { mutableStateOf("Online Cyber Cafe") }
    var maintenanceMode by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Settings Saved") },
            text = { Text("System configurations have been updated.") },
            confirmButton = {
                Button(onClick = { showSuccess = false; onBack() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("Application Name") }, modifier = Modifier.fillMaxWidth())
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Maintenance Mode")
                Switch(checked = maintenanceMode, onCheckedChange = { maintenanceMode = it })
            }
            
            Button(onClick = { showSuccess = true }, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Save Settings") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportRequestsScreen(onBack: () -> Unit) {
    val requests = remember {
        mutableStateListOf(
            "Rahul Kumar" to "Payment Deduction issue",
            "Priya Sharma" to "Document upload failing",
            "Amit Singh" to "OTP not received"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support Requests") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(requests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(req.first, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(req.second, fontSize = 13.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = { requests.remove(req) }, 
                            modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("Resolve", fontSize = 12.sp)
                        }
                    }
                }
            }
            
            if (requests.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No pending support requests", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemAuditLogsScreen(onBack: () -> Unit) {
    val logs = listOf(
        "Admin added job: UPSC CSE 2024" to "10:30 AM",
        "Employee Amit updated status for APP-101" to "09:45 AM",
        "Payment received: ₹150 (Rahul Kumar)" to "Yesterday",
        "Service fee changed to ₹50" to "2 days ago"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Audit Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(logs) { log ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(log.first, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(log.second, fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 14.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AdminActionCard(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(onBack: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var organization by remember { mutableStateOf("") }
    var officialUrl by remember { mutableStateOf("") } // Req 61
    var notificationUrl by remember { mutableStateOf("") } // Req 61
    var deadline by remember { mutableStateOf("") }
    var officialFee by remember { mutableStateOf("") }
    var serviceFee by remember { mutableStateOf("") }
    var eligibility by remember { mutableStateOf("") } // Req 61
    var otherDetails by remember { mutableStateOf("") } // Req 61
    var showSuccess by remember { mutableStateOf(false) }

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false },
            title = { Text("Success") },
            text = { Text("Job added successfully.") },
            confirmButton = {
                Button(onClick = { showSuccess = false; onBack() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post New Job") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Job Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = organization, onValueChange = { organization = it }, label = { Text("Organization") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = officialUrl, onValueChange = { officialUrl = it }, label = { Text("Official Source URL") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = notificationUrl, onValueChange = { notificationUrl = it }, label = { Text("Notification PDF / Link") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = deadline, onValueChange = { deadline = it }, label = { Text("Deadline Date") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = officialFee, onValueChange = { officialFee = it }, label = { Text("Official Fee (₹)") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = serviceFee, onValueChange = { serviceFee = it }, label = { Text("Service Fee (₹)") }, modifier = Modifier.weight(1f))
            }
            OutlinedTextField(value = eligibility, onValueChange = { eligibility = it }, label = { Text("Eligibility Criteria") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            OutlinedTextField(value = otherDetails, onValueChange = { otherDetails = it }, label = { Text("Other Details (Instructions)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = { 
                if (title.isNotBlank()) {
                    JobStore.addJob(
                        StandardizedJob(
                            id = "JOB-${(100..999).random()}",
                            title = title,
                            organization = organization,
                            description = otherDetails.ifBlank { "Manually added by Admin" },
                            department = "Various",
                            type = "Government",
                            location = "India",
                            qualification = eligibility.ifBlank { "Any" },
                            branch = "Any",
                            ageLimit = "18-40",
                            categoryRules = "Standard",
                            experience = "Fresher",
                            salary = "Standard",
                            startDate = "Today",
                            lastDate = deadline,
                            officialFee = "₹$officialFee",
                            serviceCharge = "₹$serviceFee",
                            jobUrl = officialUrl.ifBlank { "https://example.com" },
                            notificationUrl = notificationUrl.ifBlank { "https://example.com" },
                            importantInstructions = otherDetails,
                            source = "Admin Manual Entry",
                            status = JobStatus.ACTIVE,
                            lastVerified = "Just now",
                            isActive = true
                        )
                    )
                    showSuccess = true
                }
            }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Publish Job Listing")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminJobManagementScreen(onBack: () -> Unit) {
    var jobs = JobStore.jobs
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Jobs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(jobs) { job ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (job.isActive) Color.White else Color(0xFFFAFAFA)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(
                                color = job.status.color.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, job.status.color)
                            ) {
                                Text(text = job.status.label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = job.status.color)
                            }
                        }
                        Text("${job.organization} | Deadline: ${job.lastDate}", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(14.dp), tint = Color(0xFF4CAF50))
                            Spacer(Modifier.width(4.dp))
                            Text("Verified: ${job.lastVerified}", fontSize = 11.sp, color = Color.DarkGray)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { JobStore.markVerified(job.id) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Verify Now", fontSize = 12.sp)
                            }
                            
                            val activeText = if(job.isActive) "Archive" else "Activate"
                            Button(
                                onClick = { JobStore.updateJob(job.copy(isActive = !job.isActive)) },
                                modifier = Modifier.weight(1f).height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = if(job.isActive) Color(0xFFD32F2F) else Color(0xFF4CAF50)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(activeText, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRefundScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val transactions = TransactionStore.getTransactions()
    
    val filteredTransactions = transactions.filter {
        it.paymentId.contains(searchQuery, ignoreCase = true) || it.applicationId.contains(searchQuery, ignoreCase = true)
    }

    var selectedTransactionForRefund by remember { mutableStateOf<TransactionRecord?>(null) }
    var refundReason by remember { mutableStateOf("") }

    if (selectedTransactionForRefund != null) {
        AlertDialog(
            onDismissRequest = { selectedTransactionForRefund = null },
            title = { Text("Initiate Refund") },
            text = {
                Column {
                    Text("Transaction: ${selectedTransactionForRefund!!.paymentId}")
                    Text("Amount: ${selectedTransactionForRefund!!.totalAmount}")
                    Spacer(Modifier.height(16.dp))
                    Text("Reason for refund:")
                    val reasons = listOf("Portal permanently closed", "Duplicate payment", "Technical failure", "Student cancellation")
                    reasons.forEach { reason ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { refundReason = reason }.padding(4.dp)) {
                            RadioButton(selected = (refundReason == reason), onClick = { refundReason = reason })
                            Spacer(Modifier.width(8.dp))
                            Text(reason, fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (refundReason.isNotEmpty()) {
                        TransactionStore.refundTransaction(selectedTransactionForRefund!!.paymentId, refundReason)
                        SecurityStore.logAction("Admin", "Initiated Refund for ${selectedTransactionForRefund!!.paymentId}. Reason: $refundReason")
                        selectedTransactionForRefund = null
                        refundReason = ""
                    }
                }) { Text("Process Refund") }
            },
            dismissButton = {
                TextButton(onClick = { selectedTransactionForRefund = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Refund Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by Payment ID or App ID") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredTransactions) { tx ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(tx.paymentId, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(tx.status.label, color = tx.status.color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("App: ${tx.applicationId} | ${tx.timestamp}", color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(12.dp))
                            if (tx.status == PaymentStatus.SUCCESSFUL) {
                                OutlinedButton(onClick = { selectedTransactionForRefund = tx }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Initiate Refund for ${tx.totalAmount}", color = Color(0xFFD32F2F))
                                }
                            } else if (tx.status == PaymentStatus.REFUND_INITIATED) {
                                Text("Refund Reason: ${tx.refundStatus}", color = Color(0xFF9C27B0), fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAuditLogsScreen(onBack: () -> Unit) {
    val logs = SecurityStore.auditLogs

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System Audit Logs") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(logs) { log ->
                val severityColor = when(log.severity) {
                    "CRITICAL" -> Color(0xFFD32F2F)
                    "WARNING" -> Color(0xFFFFA000)
                    else -> Color(0xFF1976D2)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(log.actor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(log.severity, color = severityColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(log.action, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(log.timestamp, color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
