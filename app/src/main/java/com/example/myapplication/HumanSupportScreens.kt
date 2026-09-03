package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════
//  1.  STUDENT  ─  Support Hub  (Entry Screen)
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentSupportHubScreen(
    onBack: () -> Unit,
    onNavigateToSubmitTicket: () -> Unit,
    onNavigateToTicketHistory: () -> Unit,
    onNavigateToLiveChat: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Icon(Icons.Default.SupportAgent, null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("How can we help?", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Real humans are here to assist you.\nNot bots — actual support staff.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // PRIMARY: Talk to Human - Prominent button
            item {
                Button(
                    onClick = onNavigateToLiveChat,
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Talk to a Human", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Connect with live support staff", fontSize = 11.sp, color = Color.White.copy(0.85f))
                    }
                }
            }

            // Call Helpline
            item {
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+911800123456"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1565C0))
                ) {
                    Icon(Icons.Default.Call, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Call Helpline: 1800-123-456", fontWeight = FontWeight.Bold)
                }
            }

            item { Text("Support Options", fontWeight = FontWeight.Bold, fontSize = 18.sp) }

            // Submit Ticket
            item {
                SupportOptionCard(
                    icon = Icons.Default.ConfirmationNumber,
                    iconColor = Color(0xFF6A1B9A),
                    title = "Submit Support Request",
                    subtitle = "Describe your issue and we'll get back to you",
                    onClick = onNavigateToSubmitTicket
                )
            }

            // Ticket History
            item {
                SupportOptionCard(
                    icon = Icons.Default.History,
                    iconColor = Color(0xFF00695C),
                    title = "My Ticket History",
                    subtitle = "Track all your past and ongoing support tickets",
                    onClick = onNavigateToTicketHistory
                )
            }

            // Common FAQ categories
            item { Text("Common Issues", fontWeight = FontWeight.Bold, fontSize = 18.sp) }

            item {
                FaqCard("Payment Failed / Deducted", "How to resolve payment issues?", Icons.Default.Payment, Color(0xFFF57F17))
            }
            item {
                FaqCard("OTP / Authentication Help", "What to do when OTP is not received?", Icons.Default.Sms, Color(0xFF1565C0))
            }
            item {
                FaqCard("Document Upload Issues", "Supported formats, size limits & tips", Icons.Default.UploadFile, Color(0xFF2E7D32))
            }
            item {
                FaqCard("Application Status Tracking", "How to check your form submission status?", Icons.Default.TrackChanges, Color(0xFF880E4F))
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun SupportOptionCard(icon: ImageVector, iconColor: Color, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = iconColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}

@Composable
fun FaqCard(title: String, subtitle: String, icon: ImageVector, color: Color) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.06f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = color)
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    "For this issue please contact our support staff directly via the 'Talk to a Human' button or call the helpline. Our team is available 9 AM – 7 PM, Mon–Sat.",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 10.dp),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  2.  STUDENT  ─  Submit New Ticket
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitSupportTicketScreen(onBack: () -> Unit, onSubmitSuccess: () -> Unit) {
    var subject by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Select Category") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val categories = listOf("Payment Issue", "Document Problem", "OTP / Authentication", "Application Status", "Fee Related", "Other")

    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp)) },
            title = { Text("Ticket Submitted!", fontWeight = FontWeight.Bold) },
            text = { Text("Your support request has been submitted successfully. Our team will respond within 2-4 hours during business hours.") },
            confirmButton = {
                Button(onClick = onSubmitSuccess) { Text("View My Tickets") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Support Request", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Dropdown
            FilterDropdownField("Issue Category", selectedCategory, categories) { selectedCategory = it }

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                placeholder = { Text("Brief title of the issue") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Title, null) }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe your problem") },
                placeholder = { Text("Please provide as much detail as possible...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 5
            )

            // Important Notice
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(14.dp)) {
                    Icon(Icons.Default.Info, null, tint = Color(0xFFF9A825), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Support is handled by our trained human staff, not automated bots. Average response time: 2-4 hours (Mon-Sat, 9 AM - 7 PM).",
                        fontSize = 12.sp, color = Color(0xFF5D4037), lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (subject.isNotBlank() && description.isNotBlank() && selectedCategory != "Select Category") {
                        isSubmitting = true
                        scope.launch {
                            delay(1200)
                            SupportTicketStore.submitTicket(
                                SupportTicket(
                                    ticketId = SupportTicketStore.generateTicketId(),
                                    studentName = "Rahul Kumar",
                                    studentPhone = "9876543210",
                                    category = selectedCategory,
                                    subject = subject,
                                    description = description,
                                    submittedAt = "Just now",
                                    messages = mutableListOf(
                                        TicketMessage("Student", description, "Just now")
                                    )
                                )
                            )
                            isSubmitting = false
                            showSuccess = true
                        }
                    }
                },
                enabled = !isSubmitting && subject.isNotBlank() && description.isNotBlank() && selectedCategory != "Select Category",
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.Send, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Submit Support Request", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  3.  STUDENT  ─  Ticket History
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTicketHistoryScreen(onBack: () -> Unit, onOpenTicket: (String) -> Unit) {
    val tickets = SupportTicketStore.getTickets()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Support Tickets", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (tickets.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ConfirmationNumber, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("No tickets yet", color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("Submit a support request to get started.", fontSize = 13.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    StudentTicketCard(ticket = ticket, onClick = { onOpenTicket(ticket.ticketId) })
                }
            }
        }
    }
}

@Composable
fun StudentTicketCard(ticket: SupportTicket, onClick: () -> Unit) {
    val statusColor = when (ticket.status) {
        "Open" -> Color(0xFFF57F17)
        "In Progress" -> Color(0xFF1565C0)
        "Resolved" -> Color(0xFF2E7D32)
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(ticket.ticketId, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(ticket.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                }
            }
            Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(ticket.category, fontSize = 12.sp, color = Color(0xFF1565C0))
            Text(ticket.submittedAt, fontSize = 11.sp, color = Color.Gray)
            if (ticket.responseMessage.isNotBlank()) {
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Reply, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(ticket.responseMessage, fontSize = 12.sp, color = Color(0xFF2E7D32))
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  4.  STUDENT  ─  Ticket Detail / In-Thread Chat
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(ticketId: String, onBack: () -> Unit) {
    val ticket = SupportTicketStore.getTicketById(ticketId)
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var refreshKey by remember { mutableStateOf(0) }

    if (ticket == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ticket not found.")
        }
        return
    }

    val messages by remember(refreshKey) { mutableStateOf(ticket.messages.toList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(ticket.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${ticket.ticketId} • ${ticket.status}", fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    val isStudent = msg.sender == "Student"
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isStudent) Arrangement.End else Arrangement.Start) {
                        if (!isStudent) {
                            Surface(color = Color(0xFF1565C0), shape = CircleShape, modifier = Modifier.size(28.dp).align(Alignment.Bottom)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.SupportAgent, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        Card(
                            shape = RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = if (isStudent) 16.dp else 4.dp,
                                bottomEnd = if (isStudent) 4.dp else 16.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isStudent) Color(0xFF1565C0) else Color(0xFFF5F5F5)
                            ),
                            modifier = Modifier.widthIn(max = 260.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp, 8.dp)) {
                                Text(msg.text, color = if (isStudent) Color.White else Color.Black, fontSize = 13.sp)
                                Text(msg.time, color = if (isStudent) Color.White.copy(0.6f) else Color.Gray, fontSize = 9.sp)
                            }
                        }
                    }
                }
            }

            if (ticket.status != "Resolved" && ticket.status != "Closed") {
                Surface(tonalElevation = 4.dp) {
                    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            placeholder = { Text("Type your message…") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        FloatingActionButton(
                            onClick = {
                                if (message.isNotBlank()) {
                                    SupportTicketStore.addMessage(ticketId, TicketMessage("Student", message, "Just now"))
                                    message = ""
                                    refreshKey++
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF1565C0)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                        }
                    }
                }
            } else {
                Surface(color = Color(0xFFF1F8E9), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("This ticket is ${ticket.status}.", fontSize = 13.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════
//  5.  ADMIN / SUPPORT STAFF  ─  Support Dashboard
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSupportDashboardScreen(onBack: () -> Unit, onOpenTicket: (String) -> Unit) {
    val allTickets = SupportTicketStore.getTickets()
    var filterStatus by remember { mutableStateOf("All") }
    val statuses = listOf("All", "Open", "In Progress", "Resolved")

    val filteredTickets = if (filterStatus == "All") allTickets else allTickets.filter { it.status == filterStatus }

    val openCount = allTickets.count { it.status == "Open" }
    val inProgressCount = allTickets.count { it.status == "In Progress" }
    val resolvedCount = allTickets.count { it.status == "Resolved" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Support Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1565C0),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Stats Row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SupportStatChip("Open", openCount.toString(), Color(0xFFF57F17), Modifier.weight(1f))
                    SupportStatChip("In Progress", inProgressCount.toString(), Color(0xFF1565C0), Modifier.weight(1f))
                    SupportStatChip("Resolved", resolvedCount.toString(), Color(0xFF2E7D32), Modifier.weight(1f))
                }
            }

            // Filter Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statuses.forEach { s ->
                        FilterChip(
                            selected = filterStatus == s,
                            onClick = { filterStatus = s },
                            label = { Text(s, fontSize = 12.sp) }
                        )
                    }
                }
            }

            if (filteredTickets.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("No tickets found", color = Color.Gray)
                    }
                }
            }

            items(filteredTickets) { ticket ->
                AdminTicketCard(ticket = ticket, onClick = { onOpenTicket(ticket.ticketId) })
            }
        }
    }
}

@Composable
fun SupportStatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
            Text(label, fontSize = 11.sp, color = color.copy(0.8f))
        }
    }
}

@Composable
fun AdminTicketCard(ticket: SupportTicket, onClick: () -> Unit) {
    val statusColor = when (ticket.status) {
        "Open" -> Color(0xFFF57F17)
        "In Progress" -> Color(0xFF1565C0)
        "Resolved" -> Color(0xFF2E7D32)
        else -> Color.Gray
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(ticket.ticketId, fontSize = 11.sp, color = Color.Gray)
                    Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
                ) {
                    Text(ticket.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(ticket.studentName, fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(Modifier.width(4.dp))
                    Text(ticket.studentPhone, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(ticket.category, fontSize = 12.sp, color = Color(0xFF6A1B9A))
                Text(ticket.submittedAt, fontSize = 11.sp, color = Color.Gray)
            }
            Text("Assigned to: ${ticket.assignedTo}", fontSize = 12.sp,
                color = if (ticket.assignedTo == "Unassigned") Color(0xFFF57F17) else Color(0xFF1565C0))
        }
    }
}

// ════════════════════════════════════════════════════════════
//  6.  ADMIN / SUPPORT STAFF  ─  Ticket Detail + Reply
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTicketDetailScreen(ticketId: String, onBack: () -> Unit) {
    val ticket = SupportTicketStore.getTicketById(ticketId)
    var replyText by remember { mutableStateOf("") }
    var selectedEmployee by remember { mutableStateOf("Unassigned") }
    var selectedStatus by remember { mutableStateOf(ticket?.status ?: "Open") }
    var refreshKey by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    if (ticket == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Ticket not found") }
        return
    }

    val messages by remember(refreshKey) { mutableStateOf(ticket.messages.toList()) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(ticket.subject, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(ticket.ticketId, fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ticket Info Card
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("From: ${ticket.studentName}", fontWeight = FontWeight.Bold)
                                Text(ticket.submittedAt, fontSize = 11.sp, color = Color.Gray)
                            }
                            Text("📞 ${ticket.studentPhone}", fontSize = 12.sp, color = Color.Gray)
                            Text("Category: ${ticket.category}", fontSize = 12.sp, color = Color(0xFF6A1B9A))
                        }
                    }
                }

                // Assignment Controls
                item {
                    Card {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Manage Ticket", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            FilterDropdownField("Assign to Employee", selectedEmployee,
                                listOf("Unassigned", "Executive Amit", "Executive Priya", "Executive Vikram")) {
                                selectedEmployee = it
                            }
                            FilterDropdownField("Set Status", selectedStatus,
                                listOf("Open", "In Progress", "Resolved", "Closed")) {
                                selectedStatus = it
                            }
                            Button(
                                onClick = {
                                    SupportTicketStore.updateTicket(ticketId, status = selectedStatus, assignedTo = selectedEmployee)
                                    scope.launch { snackbarHostState.showSnackbar("Ticket updated successfully!") }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Save, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Save Changes")
                            }
                        }
                    }
                }

                // Conversation Thread
                item { Text("Conversation", fontWeight = FontWeight.Bold, fontSize = 16.sp) }

                items(messages) { msg ->
                    val isStudent = msg.sender == "Student"
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isStudent) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isStudent) Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                            ),
                            modifier = Modifier.widthIn(max = 260.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp, 8.dp)) {
                                Text(msg.sender, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                                    color = if (isStudent) Color(0xFF1565C0) else Color(0xFF2E7D32))
                                Text(msg.text, fontSize = 13.sp)
                                Text(msg.time, fontSize = 9.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Reply Box
            Surface(tonalElevation = 4.dp) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Type your reply to the student…") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                    Button(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                SupportTicketStore.addMessage(ticketId, TicketMessage("Support", replyText, "Just now"))
                                SupportTicketStore.updateTicket(ticketId, response = replyText)
                                replyText = ""
                                refreshKey++
                                scope.launch { snackbarHostState.showSnackbar("Reply sent!") }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = replyText.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Send Reply to Student")
                    }
                }
            }
        }
    }
}
