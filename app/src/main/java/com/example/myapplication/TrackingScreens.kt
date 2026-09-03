package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ════════════════════════════════════════════════════════════
//  Application History List  (Point 28)
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationHistoryScreen(
    onBack: () -> Unit,
    onNavigateToTracking: (String) -> Unit
) {
    val liveApps = AssignmentStore.getAssignments()
    var filterStatus by remember { mutableStateOf("All") }
    val statusOptions = listOf("All", "Submitted", "In Progress", "Pending", "Failed")

    val filtered = when (filterStatus) {
        "Submitted" -> liveApps.filter { it.status == AppStatus.SUBMITTED.code }
        "In Progress" -> liveApps.filter { it.status == AppStatus.IN_PROGRESS.code }
        "Pending" -> liveApps.filter { it.status in listOf(AppStatus.PENDING_PROCESSING.code, AppStatus.ASSIGNED.code, AppStatus.WAITING_FOR_STUDENT.code) }
        "Failed" -> liveApps.filter { it.status in listOf(AppStatus.SUBMISSION_FAILED.code, AppStatus.CANCELLED.code) }
        else -> liveApps.toList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("My Applications", fontWeight = FontWeight.Bold)
                        Text("${liveApps.size} total", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Status filter chips
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusOptions.forEach { s ->
                    FilterChip(
                        selected = filterStatus == s,
                        onClick = { filterStatus = s },
                        label = { Text(s, fontSize = 11.sp) },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No applications found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { app ->
                        ApplicationHistoryCard(app = app, onClick = { onNavigateToTracking(app.appId) })
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun ApplicationHistoryCard(app: ApplicationAssignment, onClick: () -> Unit) {
    val color = StatusEngine.getStatusColor(app.status)
    val isSubmitted = app.status == AppStatus.SUBMITTED.code

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = color.copy(alpha = 0.12f),
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isSubmitted) Icons.Default.CheckCircle else Icons.Default.Description,
                            null, tint = color, modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(app.jobTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    if (app.organization.isNotBlank())
                        Text(app.organization, fontSize = 12.sp, color = Color.Gray)
                }
                // Status chip
                Surface(
                    color = color.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color)
                ) {
                    Text(
                        app.status.replace("_", " ").split(" ")
                            .joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                        color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            // Detail grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                AppInfoItem("App ID", app.appId, Modifier.weight(1f))
                AppInfoItem("Applied", app.appliedDate, Modifier.weight(1f))
            }
            if (app.applicationNumber.isNotBlank()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    AppInfoItem("App No.", app.applicationNumber, Modifier.weight(1f))
                    AppInfoItem("Official Fee", app.officialFee, Modifier.weight(1f))
                }
            }

            // Human-readable status
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(color, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(StatusEngine.getStudentLabel(app.status), fontSize = 12.sp, color = color, lineHeight = 16.sp)
            }

            // CTA
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onClick) {
                    Text("View Details →", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AppInfoItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, color = Color.Gray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ════════════════════════════════════════════════════════════
//  Application Detail Screen  (Point 29 — 16 fields)
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationTrackingScreen(appId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val app = AssignmentStore.getAssignments().find { it.appId == appId }
    val timeline = AssignmentStore.getTimelineForApp(appId)
    val currentStatus = app?.status ?: AppStatus.PENDING_PROCESSING.code
    val statusColor = StatusEngine.getStatusColor(currentStatus)
    val sr = app?.submissionRecord

    val isWaitingForStudent = currentStatus == AppStatus.WAITING_FOR_STUDENT.code
    val isSubmitted = currentStatus == AppStatus.SUBMITTED.code

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(app?.jobTitle ?: "Application Detail", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(appId, fontSize = 11.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (app?.officialAppUrl?.isNotBlank() == true) {
                        IconButton(onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(app.officialAppUrl))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                scope.launch { snackbar.showSnackbar("Cannot open URL") }
                            }
                        }) {
                            Icon(Icons.Default.OpenInNew, null, tint = statusColor)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── 1. Status Header ──
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.07f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(statusColor, CircleShape))
                            Spacer(Modifier.width(10.dp))
                            Text(StatusEngine.getStudentLabel(currentStatus), color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 19.sp)
                        }
                        if (app?.assignedEmployee?.isNotBlank() == true) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Processed by: ${app.assignedEmployee}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // ── 2. Action Required Banner ──
            if (isWaitingForStudent) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB74D)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Your Action is Required!", fontWeight = FontWeight.Bold, color = Color(0xFFE65100), fontSize = 15.sp)
                            }
                            Text("Our executive needs your OTP or biometric authentication. Please contact us immediately.", fontSize = 13.sp, lineHeight = 19.sp)
                            Button(
                                onClick = { scope.launch { snackbar.showSnackbar("Opening Support Chat…") } },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Chat, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Share OTP via Support Chat")
                            }
                        }
                    }
                }
            }

            // ── 3. Job Details ──
            item {
                DetailSection("Job Details", Icons.Default.Work) {
                    DetailRow("Job Title", app?.jobTitle ?: "—")
                    DetailRow("Organization", app?.organization?.ifBlank { "—" } ?: "—")
                    DetailRow("Official Fee", app?.officialFee ?: "—")
                    DetailRow("Service Fee", app?.serviceFee ?: "—")
                    val total = calculateTotal(app?.officialFee, app?.serviceFee)
                    DetailRow("Total Paid", total)
                    DetailRow("Date Applied", app?.appliedDate ?: "—")
                    DetailRow("Deadline", app?.deadline ?: "—")
                    if (app?.officialWebsite?.isNotBlank() == true) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Official Website", fontSize = 13.sp, color = Color.Gray)
                            TextButton(
                                onClick = {
                                    try {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(app.officialWebsite)))
                                    } catch (e: Exception) { }
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Open ↗", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                            }
                        }
                    }
                }
            }

            // ── 4. Application Info ──
            item {
                DetailSection("Application Info", Icons.Default.Assignment) {
                    DetailRow("Application ID", appId)
                    DetailRow("Application No.", app?.applicationNumber?.ifBlank { "Pending" } ?: "Pending")
                    if (sr?.registrationNumber?.isNotBlank() == true)
                        DetailRow("Registration No.", sr.registrationNumber)
                    if (sr != null) {
                        DetailRow("Submitted Date", sr.submissionDate)
                        DetailRow("Submitted Time", sr.submissionTime)
                    }
                    if (app?.remarks?.isNotBlank() == true) {
                        Spacer(Modifier.height(4.dp))
                        Text("Employee Remarks", fontSize = 12.sp, color = Color.Gray)
                        Surface(
                            color = Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(app.remarks, modifier = Modifier.padding(10.dp), fontSize = 13.sp, lineHeight = 18.sp)
                        }
                    }
                }
            }

            // ── 5. Historical Snapshot ──
            if (isSubmitted) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F51B5)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.History, null, tint = Color(0xFF3F51B5), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Historical Snapshot", fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                            }
                            Text("This reflects exactly what was submitted — unaffected by any future profile changes.", fontSize = 11.sp, color = Color.Gray)
                            HorizontalDivider(color = Color(0xFFC5CAE9))
                            DetailRow("Student Name", app?.studentName ?: "—")
                            DetailRow("Application No.", app?.applicationNumber?.ifBlank { "—" } ?: "—")
                            DetailRow("Processing Executive", app?.assignedEmployee ?: "—")
                        }
                    }
                }
            }

            // ── 6. Downloads ──
            item {
                DetailSection("Documents & Downloads", Icons.Default.FolderOpen) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DownloadButton(
                            "Payment Receipt",
                            Icons.Default.Receipt,
                            Color(0xFF2E7D32),
                            enabled = true
                        ) { scope.launch { snackbar.showSnackbar("Downloading payment receipt…") } }

                        DownloadButton(
                            "Official Portal Receipt",
                            Icons.Default.Article,
                            Color(0xFF1565C0),
                            enabled = sr?.portalReceiptUrl?.isNotBlank() == true || isSubmitted
                        ) { scope.launch { snackbar.showSnackbar("Downloading portal receipt…") } }

                        DownloadButton(
                            "Submitted Application PDF",
                            Icons.Default.PictureAsPdf,
                            Color(0xFFD32F2F),
                            enabled = sr?.applicationPdfUrl?.isNotBlank() == true || isSubmitted
                        ) { scope.launch { snackbar.showSnackbar("Downloading application PDF…") } }
                    }
                }
            }

            // ── 7. Timeline ──
            item {
                Text("Application Timeline", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            if (timeline.isEmpty()) {
                item { Text("No timeline events yet.", color = Color.Gray, fontSize = 13.sp) }
            } else {
                items(timeline.size) { idx ->
                    val event = timeline[idx]
                    DynamicTimelineStep(event = event, dotColor = StatusEngine.getStatusColor(event.status), isLast = idx == timeline.size - 1)
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────

@Composable
fun DetailSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            HorizontalDivider(color = Color(0xFFEEEEEE))
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.2f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
    }
}

@Composable
fun DownloadButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, enabled: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (enabled) color else Color.LightGray)
    ) {
        Icon(icon, null, tint = if (enabled) color else Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            if (enabled) label else "$label (Not Available)",
            color = if (enabled) color else Color.Gray, fontSize = 13.sp
        )
        Spacer(Modifier.weight(1f))
        if (enabled) Icon(Icons.Default.FileDownload, null, tint = color, modifier = Modifier.size(16.dp))
    }
}

fun calculateTotal(officialFee: String?, serviceFee: String?): String {
    val off = officialFee?.replace("₹", "")?.trim()?.toIntOrNull() ?: 0
    val srv = serviceFee?.replace("₹", "")?.trim()?.toIntOrNull() ?: 0
    return if (off + srv == 0) "—" else "₹${off + srv}"
}

// ════════════════════════════════════════════════════════════
//  Dynamic Timeline Step Composable
// ════════════════════════════════════════════════════════════

@Composable
fun DynamicTimelineStep(event: TimelineEvent, dotColor: Color, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(28.dp)) {
            Box(Modifier.size(14.dp).background(dotColor, CircleShape))
            if (!isLast) Box(Modifier.width(2.dp).height(44.dp).background(Color(0xFFE0E0E0)))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 8.dp)) {
            Text(event.label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = dotColor)
            if (event.note.isNotBlank()) Text(event.note, fontSize = 11.sp, color = Color.DarkGray)
            Text(event.timestamp, fontSize = 10.sp, color = Color.Gray)
        }
    }
}

// ════════════════════════════════════════════════════════════
//  Human Support Chat Screen (kept for SupportChat route)
// ════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HumanSupportChatScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Support Executive", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Online", fontSize = 12.sp, color = Color(0xFF4CAF50))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ChatBubble("Hello! I'm Amit, your application executive. I'm currently filling your SSC CGL form.", false)
                    ChatBubble("The portal just asked for an OTP sent to your mobile. Please provide it here.", false)
                    ChatBubble("My OTP is 445210", true)
                    ChatBubble("Thank you! Processing now…", false)
                }
            }
            Surface(tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Type message…") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    FloatingActionButton(onClick = {}, modifier = Modifier.size(48.dp)) {
                        Icon(Icons.Default.Send, null)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isUser) 16.dp else 0.dp, bottomEnd = if (isUser) 0.dp else 16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isUser) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5)),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(text = text, modifier = Modifier.padding(12.dp), color = if (isUser) Color.White else Color.Black, fontSize = 14.sp)
        }
    }
}

// Legacy chip
@Composable
fun StatusChip(status: String) {
    val color = StatusEngine.getStatusColor(status)
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, color)) {
        Text(text = status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
