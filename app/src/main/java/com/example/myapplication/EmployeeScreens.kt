package com.example.myapplication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDashboardScreen(email: String, onLogout: () -> Unit, onNavigateToApplication: (String) -> Unit) {
    // In a real app, this name would come from the logged-in user profile API
    val loggedInEmployee = if (email.contains("employee", ignoreCase = true)) "Executive Amit" else "Executive Priya"
    val stats = AssignmentStore.getStatsForEmployee(loggedInEmployee)
    
    // Get all assignments for this employee
    val assignments = AssignmentStore.getAssignments().filter { it.assignedEmployee == loggedInEmployee }
    
    // Priority Sorting: High Priority First, then based on early deadlines
    val sortedAssignments = assignments.sortedWith(
        compareByDescending<ApplicationAssignment> { it.priority == "High" }
            .thenBy { it.deadline }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Employee Console", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
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
                Text("Today's Overview", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            item {
                // Responsive Stats Grid (Requested Format)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmployeeStatCard("Today's Apps", stats.total.toString(), Color(0xFF1976D2), Modifier.weight(1f))
                        EmployeeStatCard("Pending", stats.pending.toString(), Color(0xFFFFA000), Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmployeeStatCard("In Progress", stats.inProgress.toString(), Color(0xFF2196F3), Modifier.weight(1f))
                        EmployeeStatCard("Waiting (S)", stats.waiting.toString(), Color(0xFF9C27B0), Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EmployeeStatCard("Submitted", stats.submitted.toString(), Color(0xFF388E3C), Modifier.weight(1f))
                        EmployeeStatCard("Failed", stats.failed.toString(), Color(0xFFD32F2F), Modifier.weight(1f))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Your Assignments", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Priority First", fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            if (sortedAssignments.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No applications assigned to you yet.", color = Color.Gray)
                    }
                }
            } else {
                items(sortedAssignments) { assignment ->
                    AssignmentPriorityCard(assignment, onClick = { onNavigateToApplication(assignment.appId) })
                }
            }
            
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun EmployeeStatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(
                text = label, 
                fontSize = 11.sp, 
                color = color.copy(alpha = 0.8f), 
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AssignmentPriorityCard(assignment: ApplicationAssignment, onClick: () -> Unit) {
    val isHighPriority = assignment.priority == "High"
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighPriority) Color(0xFFFFEBEE) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHighPriority) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = if(isHighPriority) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if(isHighPriority) Icons.Default.PriorityHigh else Icons.AutoMirrored.Filled.Assignment, 
                        contentDescription = null, 
                        tint = Color.White, 
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(assignment.jobTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if(isHighPriority) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = Color(0xFFD32F2F), shape = RoundedCornerShape(4.dp)) {
                            Text("URGENT", color = Color.White, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text("Student: ${assignment.studentName}", fontSize = 13.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Schedule, null, tint = if(isHighPriority) Color(0xFFD32F2F) else Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Deadline: ${assignment.deadline}", fontSize = 12.sp, color = if(isHighPriority) Color(0xFFD32F2F) else Color.Gray, fontWeight = if(isHighPriority) FontWeight.Bold else FontWeight.Normal)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationProcessingScreen(appId: String, onBack: () -> Unit) {
    val assignment = remember { AssignmentStore.getAssignments().find { it.appId == appId } }
    
    var status by remember { mutableStateOf(assignment?.status ?: AppStatus.PENDING_PROCESSING.code) }
    var applicationNumber by remember { mutableStateOf(assignment?.applicationNumber ?: "") }
    var remarks by remember { mutableStateOf(assignment?.remarks ?: "") }
    var hasReceipt by remember { mutableStateOf(assignment?.hasReceipt ?: false) }
    
    var showReceiptUpload by remember { mutableStateOf(false) }
    var problemReported by remember { mutableStateOf<String?>(null) }
    var showVerificationRequested by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            hasReceipt = true
            showReceiptUpload = true
            AssignmentStore.updateAssignment(appId, hasReceipt = true)
        }
    }

    if (showReceiptUpload) {
        AlertDialog(
            onDismissRequest = { showReceiptUpload = false },
            title = { Text("Receipt Uploaded") },
            text = { Text("The official application receipt has been successfully attached to $appId.") },
            confirmButton = { Button(onClick = { showReceiptUpload = false }) { Text("OK") } }
        )
    }

    if (problemReported != null) {
        AlertDialog(
            onDismissRequest = { problemReported = null },
            title = { Text("Report Issue") },
            text = {
                OutlinedTextField(
                    value = problemReported!!,
                    onValueChange = { problemReported = it },
                    label = { Text("Describe the problem") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = { 
                    status = "FAILED"
                    AssignmentStore.updateAssignment(appId, status = "FAILED", remarks = "ISSUE: $problemReported")
                    problemReported = null 
                }) { Text("Send Report") }
            },
            dismissButton = { TextButton(onClick = { problemReported = null }) { Text("Cancel") } }
        )
    }

    if (showVerificationRequested) {
        AlertDialog(
            onDismissRequest = { showVerificationRequested = false },
            title = { Text("Verification Requested") },
            text = { Text("The student has been notified to complete the OTP/Biometric authentication step personally via the support chat.") },
            confirmButton = { Button(onClick = { showVerificationRequested = false }) { Text("OK") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Application Workspace", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Student Information
            WorkspaceSection("Student Information", Icons.Default.Person) {
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        InfoRow("Full Name", assignment?.studentName ?: "Rahul Kumar")
                        InfoRow("Mobile", "98765 43210")
                        InfoRow("Email", "rahul.k@example.com")
                        InfoRow("Profile Status", "Verified (82%)")
                    }
                }
            }

            // Section 2: Required Documents
            WorkspaceSection("Relevant Documents", Icons.Default.Description) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp)
                ) {
                    DocumentChip("10th Marksheet")
                    DocumentChip("12th Marksheet")
                    DocumentChip("Aadhar Card")
                    DocumentChip("Caste Certificate")
                }
            }

            // Section 3: Job Information
            WorkspaceSection("Job Information", Icons.Default.Work) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(assignment?.jobTitle ?: "Job Title", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
                        
                        InfoRow("Organization", "Staff Selection Commission")
                        InfoRow("Deadline", assignment?.deadline ?: "N/A")
                        InfoRow("Official Fee", "₹100.00")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFF5F5F5))
                        
                        Text("Action Links", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = {}, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                                Icon(Icons.Default.Language, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Official Site", fontSize = 12.sp)
                            }
                            OutlinedButton(onClick = {}, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 12.dp)) {
                                Icon(Icons.AutoMirrored.Filled.OpenInNew, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Apply URL", fontSize = 12.sp)
                            }
                        }
                        
                        Box(modifier = Modifier.background(Color(0xFFFFF9C4), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, null, tint = Color(0xFFFBC02D), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Processing Instructions:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Verify Roll Number & Photo Background.", fontSize = 11.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }

            // Section 4: Application Controls (7 Required Actions)
            WorkspaceSection("Application Controls", Icons.Default.SettingsSuggest) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Status Badge (human-readable for employee too)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Current Status: ", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        val statusColor = StatusEngine.getStatusColor(status)
                        Surface(
                            color = statusColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                        ) {
                            Text(
                                StatusEngine.getStudentLabel(status),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Action Buttons Row 1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton("Start Processing", Icons.Default.PlayArrow, Color(0xFF1976D2), Modifier.weight(1f)) {
                            status = AppStatus.IN_PROGRESS.code
                            AssignmentStore.updateAssignment(appId, status = AppStatus.IN_PROGRESS.code)
                        }
                        ActionButton("Req Verification", Icons.Default.HourglassEmpty, Color(0xFF9C27B0), Modifier.weight(1f)) {
                            status = AppStatus.WAITING_FOR_STUDENT.code
                            AssignmentStore.updateAssignment(appId, status = AppStatus.WAITING_FOR_STUDENT.code)
                            showVerificationRequested = true
                        }
                    }

                    // Action Buttons Row 2
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton("Mark Submitted", Icons.Default.CheckCircle, Color(0xFF388E3C), Modifier.weight(1f)) {
                            status = AppStatus.SUBMITTED.code
                            AssignmentStore.updateAssignment(appId, status = AppStatus.SUBMITTED.code)
                        }
                        ActionButton("Portal Unavailable", Icons.Default.CloudOff, Color(0xFFE65100), Modifier.weight(1f)) {
                            status = AppStatus.WAITING_FOR_PORTAL.code
                            AssignmentStore.updateAssignment(appId, status = AppStatus.WAITING_FOR_PORTAL.code, note = "Official website is temporarily unavailable")
                        }
                    }

                    // Action Buttons Row 3
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActionButton("Docs Required", Icons.Default.FolderOpen, Color(0xFF6D4C41), Modifier.weight(1f)) {
                            status = AppStatus.DOCUMENTS_REQUIRED.code
                            AssignmentStore.updateAssignment(appId, status = AppStatus.DOCUMENTS_REQUIRED.code)
                        }
                        ActionButton("Report Issue", Icons.Default.ReportProblem, Color(0xFFD32F2F), Modifier.weight(1f)) {
                            problemReported = ""
                        }
                    }

                    // Multi-functional Controls
                    OutlinedTextField(
                        value = applicationNumber,
                        onValueChange = { 
                            applicationNumber = it
                            AssignmentStore.updateAssignment(appId, appNo = it)
                        },
                        label = { Text("Add Application Number") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.ConfirmationNumber, null) },
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { 
                            remarks = it
                            AssignmentStore.updateAssignment(appId, remarks = it)
                        },
                        label = { Text("Add Remarks / Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = { launcher.launch("application/pdf,image/*") },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if(hasReceipt) Color(0xFF4527A0) else MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if(hasReceipt) Icons.Default.TaskAlt else Icons.Default.FileUpload, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if(hasReceipt) "Receipt Uploaded" else "Upload Official Receipt")
                    }
                }
            }

            // ── Section 5: Submission Recording (Point 27) ──
            if (status == AppStatus.SUBMITTED.code) {
                WorkspaceSection("Record Submission Details", Icons.Default.AssignmentTurnedIn) {
                    var regNumber by remember { mutableStateOf(assignment?.submissionRecord?.registrationNumber ?: "") }
                    var subDate by remember { mutableStateOf(assignment?.submissionRecord?.submissionDate ?: todayDate()) }
                    var subTime by remember { mutableStateOf(assignment?.submissionRecord?.submissionTime ?: currentTime()) }
                    var subRemarks by remember { mutableStateOf(assignment?.submissionRecord?.employeeRemarks ?: "") }
                    var hasPdf by remember { mutableStateOf(assignment?.submissionRecord?.applicationPdfUrl?.isNotBlank() == true) }
                    var hasPayReceipt by remember { mutableStateOf(assignment?.submissionRecord?.paymentReceiptUrl?.isNotBlank() == true) }
                    var hasPortalReceipt by remember { mutableStateOf(assignment?.submissionRecord?.portalReceiptUrl?.isNotBlank() == true) }
                    var saved by remember { mutableStateOf(assignment?.submissionRecord != null) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = applicationNumber,
                                onValueChange = {
                                    applicationNumber = it
                                    AssignmentStore.updateAssignment(appId, appNo = it)
                                },
                                label = { Text("Application Number *") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.ConfirmationNumber, null) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regNumber,
                                onValueChange = { regNumber = it },
                                label = { Text("Registration Number (if applicable)") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.Numbers, null) },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = subDate,
                                    onValueChange = { subDate = it },
                                    label = { Text("Date") },
                                    modifier = Modifier.weight(1f),
                                    leadingIcon = { Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(18.dp)) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = subTime,
                                    onValueChange = { subTime = it },
                                    label = { Text("Time") },
                                    modifier = Modifier.weight(1f),
                                    leadingIcon = { Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(18.dp)) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                            // Upload buttons
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                UploadRow("Portal Receipt", hasPortalReceipt) {
                                    launcher.launch("application/pdf,image/*")
                                    hasPortalReceipt = true
                                }
                                UploadRow("Application PDF", hasPdf) {
                                    launcher.launch("application/pdf")
                                    hasPdf = true
                                }
                                UploadRow("Payment Receipt", hasPayReceipt) {
                                    launcher.launch("application/pdf,image/*")
                                    hasPayReceipt = true
                                }
                            }
                            OutlinedTextField(
                                value = subRemarks,
                                onValueChange = { subRemarks = it },
                                label = { Text("Submission Remarks") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp)
                            )
                            Button(
                                onClick = {
                                    val rec = SubmissionRecord(
                                        applicationNumber = applicationNumber,
                                        registrationNumber = regNumber,
                                        submissionDate = subDate,
                                        submissionTime = subTime,
                                        portalReceiptUrl = if (hasPortalReceipt) "local://portal_receipt" else "",
                                        applicationPdfUrl = if (hasPdf) "local://app_pdf" else "",
                                        paymentReceiptUrl = if (hasPayReceipt) "local://pay_receipt" else "",
                                        employeeRemarks = subRemarks
                                    )
                                    AssignmentStore.updateAssignment(appId, submissionRecord = rec)
                                    saved = true
                                },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(12.dp),
                                enabled = applicationNumber.isNotBlank()
                            ) {
                                Icon(if (saved) Icons.Default.CheckCircle else Icons.Default.Save, null)
                                Spacer(Modifier.width(10.dp))
                                Text(if (saved) "Submission Record Saved ✓" else "Save & Notify Student", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}


@Composable
fun WorkspaceSection(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
fun DocumentChip(label: String) {
    AssistChip(
        onClick = { /* View Document */ },
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp)) }
    )
}

@Composable
fun StatusFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        modifier = Modifier.height(32.dp)
    )
}

@Composable
fun UploadRow(label: String, hasFile: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (hasFile) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, if (hasFile) Color(0xFF2E7D32) else Color.LightGray)
        ) {
            Icon(if (hasFile) Icons.Default.CheckCircle else Icons.Default.FileUpload, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(if (hasFile) "Uploaded" else "Upload", fontSize = 11.sp)
        }
    }
}
