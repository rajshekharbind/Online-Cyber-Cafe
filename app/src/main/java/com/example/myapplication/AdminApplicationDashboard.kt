package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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

/**
 * Data model for a comprehensive application record (11 Columns as requested)
 */
data class AdminApplicationRecord(
    val appId: String,
    val studentName: String,
    val studentMobile: String,
    val jobTitle: String,
    val paymentStatus: String,
    val amount: String,
    val employeeStatus: String,
    val createdAt: String,
    val deadline: String,
    val priority: String, // High, Medium, Low
    val lastUpdated: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApplicationDashboardScreen(onBack: () -> Unit, onNavigateToApplication: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showAssignDialog by remember { mutableStateOf<AdminApplicationRecord?>(null) }

    // Filter States (8 Filters as requested)
    var filterDate by remember { mutableStateOf("All") }
    var filterJob by remember { mutableStateOf("All") }
    var filterStudent by remember { mutableStateOf("All") }
    var filterEmployeeStatus by remember { mutableStateOf("All") }
    var filterPaymentStatus by remember { mutableStateOf("All") }
    var filterDeadline by remember { mutableStateOf("All") }
    var filterPriority by remember { mutableStateOf("All") }
    
    // Mock Data
    val allApplications = remember {
        mutableStateListOf(
            AdminApplicationRecord("APP-101", "Rahul Kumar", "9876543210", "SSC CGL 2024", "Success", "₹150", "Submitted", "24 Aug 2026", "15 Sep 2026", "High", "Just now"),
            AdminApplicationRecord("APP-105", "Priya Sharma", "9123456789", "IBPS PO XIV", "Success", "₹800", "In Progress", "25 Aug 2026", "30 Aug 2026", "High", "2h ago"),
            AdminApplicationRecord("APP-109", "Amit Singh", "9988776655", "Railway Tech", "Pending", "₹550", "Pending", "26 Aug 2026", "05 Oct 2026", "Medium", "Yesterday"),
            AdminApplicationRecord("APP-098", "Sneha Gupta", "9443322110", "UPSC CSE 2024", "Failed", "₹0", "Failed", "20 Aug 2026", "24 Aug 2026", "Low", "3 days ago")
        )
    }

    // Dynamic Filter Logic (Search + 8 Filters)
    val filteredApps = allApplications.filter { record ->
        // Search Logic: App ID, Student Name, Mobile, Job Title
        val matchesSearch = record.appId.contains(searchQuery, ignoreCase = true) ||
                record.studentName.contains(searchQuery, ignoreCase = true) ||
                record.studentMobile.contains(searchQuery, ignoreCase = true) ||
                record.jobTitle.contains(searchQuery, ignoreCase = true)
        
        val matchesDate = filterDate == "All" || record.createdAt.contains(filterDate)
        val matchesJob = filterJob == "All" || record.jobTitle == filterJob
        val matchesStudent = filterStudent == "All" || record.studentName == filterStudent
        val matchesEmpStatus = filterEmployeeStatus == "All" || record.employeeStatus == filterEmployeeStatus
        val matchesPayStatus = filterPaymentStatus == "All" || record.paymentStatus == filterPaymentStatus
        val matchesDeadline = filterDeadline == "All" || record.deadline.contains(filterDeadline)
        val matchesPriority = filterPriority == "All" || record.priority == filterPriority
        
        matchesSearch && matchesDate && matchesJob && matchesStudent && matchesEmpStatus && matchesPayStatus && matchesDeadline && matchesPriority
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Application Dashboard", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    BadgedBox(badge = {
                        val activeFiltersCount = listOf(filterDate, filterJob, filterStudent, filterEmployeeStatus, filterPaymentStatus, filterDeadline, filterPriority).count { it != "All" }
                        if (activeFiltersCount > 0) {
                            Badge { Text(activeFiltersCount.toString()) }
                        }
                    }) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Search Bar (Works for: Application ID, Student name, Mobile number, Job title)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search ID, Name, Mobile, Job...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if(searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, null) } },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Critical Applications Dashboard (Req 56)
            val criticalApps = filteredApps.filter { it.priority == "Critical" || it.priority == "High" }
            if (criticalApps.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = "Urgent", tint = Color.Red)
                            Spacer(Modifier.width(8.dp))
                            Text("URGENT: ${criticalApps.size} Critical Applications Approaching Deadline", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Horizontal Table headers and list
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column {
                    // Header Row (11 Columns)
                    ApplicationRow(
                        record = AdminApplicationRecord(
                            "Application ID", "Student", "Mobile", "Job", "Payment", "Amount", "Employee Status", "Created At", "Deadline", "Priority", "Last Updated"
                        ),
                        isHeader = true
                    )
                    
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredApps) { record ->
                            val assignment = AssignmentStore.getAssignments().find { it.appId == record.appId }
                            val displayRecord = if (assignment != null) {
                                record.copy(
                                    employeeStatus = assignment.assignedEmployee,
                                    priority = assignment.priority,
                                    deadline = assignment.deadline
                                )
                            } else record

                            ApplicationRow(
                                record = displayRecord,
                                onAssignClick = { showAssignDialog = displayRecord },
                                onRowClick = { onNavigateToApplication(displayRecord.appId) }
                            )
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }
            
            if (filteredApps.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No results found matching your criteria", color = Color.Gray)
                }
            }
        }
    }
    
    if (showFilterSheet) {
        FilterDialog(
            currentDate = filterDate,
            currentJob = filterJob,
            currentStudent = filterStudent,
            currentEmpStatus = filterEmployeeStatus,
            currentPayStatus = filterPaymentStatus,
            currentDeadline = filterDeadline,
            currentPriority = filterPriority,
            onApply = { date, job, student, emp, pay, deadline, prio ->
                filterDate = date
                filterJob = job
                filterStudent = student
                filterEmployeeStatus = emp
                filterPaymentStatus = pay
                filterDeadline = deadline
                filterPriority = prio
                showFilterSheet = false
            },
            onReset = {
                filterDate = "All"
                filterJob = "All"
                filterStudent = "All"
                filterEmployeeStatus = "All"
                filterPaymentStatus = "All"
                filterDeadline = "All"
                filterPriority = "All"
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }
    if (showAssignDialog != null) {
        AssignmentDialog(
            appId = showAssignDialog!!.appId,
            currentEmployee = showAssignDialog!!.employeeStatus,
            currentPriority = showAssignDialog!!.priority,
            currentDeadline = showAssignDialog!!.deadline,
            onDismiss = { showAssignDialog = null }
        )
    }
}

@Composable
fun ApplicationRow(
    record: AdminApplicationRecord, 
    isHeader: Boolean = false,
    onAssignClick: (() -> Unit)? = null,
    onRowClick: (() -> Unit)? = null
) {
    val backgroundColor = if (isHeader) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal
    val textColor = if (isHeader) MaterialTheme.colorScheme.onPrimaryContainer else Color.Unspecified

    Row(
        modifier = Modifier
            .background(backgroundColor)
            .clickable(enabled = onRowClick != null) { onRowClick?.invoke() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(record.appId, 120.dp, fontWeight, textColor)
        TableCell(record.studentName, 150.dp, fontWeight, textColor)
        TableCell(record.studentMobile, 120.dp, fontWeight, textColor)
        TableCell(record.jobTitle, 180.dp, fontWeight, textColor)
        TableCell(record.paymentStatus, 110.dp, fontWeight, textColor)
        TableCell(record.amount, 100.dp, fontWeight, textColor)
        TableCell(record.employeeStatus, 140.dp, fontWeight, textColor)
        TableCell(record.createdAt, 130.dp, fontWeight, textColor)
        TableCell(record.deadline, 130.dp, fontWeight, textColor)
        TableCell(record.priority, 100.dp, fontWeight, textColor)
        TableCell(record.lastUpdated, 130.dp, fontWeight, textColor)
        
        if (!isHeader) {
            IconButton(onClick = { onAssignClick?.invoke() }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.EditCalendar, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            }
        } else {
            Spacer(Modifier.width(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentDialog(
    appId: String,
    currentEmployee: String,
    currentPriority: String,
    currentDeadline: String,
    onDismiss: () -> Unit
) {
    var employee by remember { mutableStateOf(if(currentEmployee == "Unassigned") "Executive Amit" else currentEmployee) }
    var priority by remember { mutableStateOf(currentPriority) }
    var deadline by remember { mutableStateOf(currentDeadline) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Application: $appId", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterDropdownField("Select Employee", employee, listOf("Executive Amit", "Executive Priya", "Executive Vikram")) { employee = it }
                FilterDropdownField("Set Priority", priority, listOf("High", "Medium", "Low")) { priority = it }
                
                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Deadline") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.CalendarToday, null) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                AssignmentStore.assignApplication(appId, employee, priority, deadline)
                onDismiss()
            }) { Text("Confirm Assignment") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TableCell(text: String, width: androidx.compose.ui.unit.Dp, fontWeight: FontWeight, color: Color) {
    Text(
        text = text,
        modifier = Modifier.width(width).padding(end = 8.dp),
        fontWeight = fontWeight,
        fontSize = 12.sp,
        maxLines = 1,
        color = color
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentDate: String,
    currentJob: String,
    currentStudent: String,
    currentEmpStatus: String,
    currentPayStatus: String,
    currentDeadline: String,
    currentPriority: String,
    onApply: (String, String, String, String, String, String, String) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var date by remember { mutableStateOf(currentDate) }
    var job by remember { mutableStateOf(currentJob) }
    var student by remember { mutableStateOf(currentStudent) }
    var empStatus by remember { mutableStateOf(currentEmpStatus) }
    var payStatus by remember { mutableStateOf(currentPayStatus) }
    var deadline by remember { mutableStateOf(currentDeadline) }
    var priority by remember { mutableStateOf(currentPriority) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filters (8 criteria)", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                FilterDropdownField("Filter by Date", date, listOf("All", "Aug 2026", "Jul 2026")) { date = it }
                FilterDropdownField("Filter by Job", job, listOf("All", "SSC CGL 2024", "IBPS PO XIV", "Railway Tech", "UPSC CSE 2024")) { job = it }
                FilterDropdownField("Filter by Student", student, listOf("All", "Rahul Kumar", "Priya Sharma", "Amit Singh", "Sneha Gupta")) { student = it }
                FilterDropdownField("Filter by Emp Status", empStatus, listOf("All", "Submitted", "In Progress", "Pending", "Failed")) { empStatus = it }
                FilterDropdownField("Filter by Pay Status", payStatus, listOf("All", "Success", "Pending", "Failed")) { payStatus = it }
                FilterDropdownField("Filter by Deadline", deadline, listOf("All", "Sep 2026", "Oct 2026")) { deadline = it }
                FilterDropdownField("Filter by Priority", priority, listOf("All", "High", "Medium", "Low")) { priority = it }
            }
        },
        confirmButton = {
            Button(onClick = { onApply(date, job, student, empStatus, payStatus, deadline, priority) }) { Text("Apply Filters") }
        },
        dismissButton = {
            TextButton(onClick = onReset) { Text("Reset All", color = Color.Gray) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdownField(label: String, selectedValue: String, options: List<String>, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
