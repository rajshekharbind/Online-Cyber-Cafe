package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

enum class JobStatus(val label: String, val color: Color) {
    UPCOMING("Upcoming", Color(0xFF673AB7)),
    ACTIVE("Active", Color(0xFF4CAF50)),
    DEADLINE_APPROACHING("Deadline Approaching", Color(0xFFFFA000)),
    EXPIRED("Expired", Color(0xFFD32F2F)),
    CLOSED("Closed", Color(0xFF757575)),
    UNAVAILABLE("Temporarily Unavailable", Color(0xFFE91E63))
}

data class StandardizedJob(
    val id: String,
    val title: String,
    val organization: String,
    val description: String,
    val department: String,
    val type: String,
    val location: String,
    val qualification: String,
    val branch: String,
    val ageLimit: String,
    val categoryRules: String,
    val experience: String,
    val salary: String,
    val startDate: String,
    val lastDate: String,
    val officialFee: String,
    val serviceCharge: String = "₹50",
    val jobUrl: String,
    val notificationUrl: String,
    val importantInstructions: String,
    val source: String,
    var status: JobStatus,
    val lastVerified: String,
    val isActive: Boolean = true
)

enum class EligibilityStatus(val label: String, val color: Color) {
    ELIGIBLE("Eligible", Color(0xFF4CAF50)),
    NOT_ELIGIBLE("Not Eligible", Color(0xFFD32F2F)),
    POTENTIALLY_ELIGIBLE("Potentially Eligible - Verify Notif", Color(0xFFFFA000))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDiscoveryScreen(
    onBack: () -> Unit, 
    onNavigateToApply: (String, String, String, String) -> Unit, 
    onNavigateToNotifications: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // Mock Student Profile for matching
    val studentProfile = remember {
        mapOf(
            "age" to 21,
            "degree" to "B.Tech",
            "branch" to "CSE",
            "cgpa" to 7.47,
            "state" to "Bihar"
        )
    }

    // Use centralized JobStore
    val jobs = JobStore.jobs

    // Smart Eligibility Matching Logic
    fun calculateEligibility(job: StandardizedJob): EligibilityStatus {
        val studentAge = studentProfile["age"] as Int
        val studentDegree = studentProfile["degree"] as String
        val studentBranch = studentProfile["branch"] as String
        val studentCgpa = studentProfile["cgpa"] as Double
        val studentState = studentProfile["state"] as String

        // 1. Age Check
        val ageParts = job.ageLimit.split("-")
        if (ageParts.size == 2) {
            val minAge = ageParts[0].toIntOrNull() ?: 0
            val maxAge = ageParts[1].toIntOrNull() ?: 99
            if (studentAge < minAge || studentAge > maxAge) return EligibilityStatus.NOT_ELIGIBLE
        }

        // 2. Degree Check
        if (job.qualification != "Degree" && job.qualification != "Any" && job.qualification != studentDegree) {
            return EligibilityStatus.NOT_ELIGIBLE
        }

        // 3. Branch Check
        if (job.branch != "Any" && !job.branch.contains(studentBranch)) {
            return EligibilityStatus.POTENTIALLY_ELIGIBLE
        }

        // 4. CGPA Check
        if (job.categoryRules.contains("CGPA")) {
            val reqCgpa = job.categoryRules.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            if (studentCgpa < reqCgpa) return EligibilityStatus.NOT_ELIGIBLE
        }

        // 5. State Residency Check
        if (job.categoryRules.contains("Bihar") && studentState != "Bihar") {
            return EligibilityStatus.NOT_ELIGIBLE
        }

        return EligibilityStatus.ELIGIBLE
    }

    val visibleJobs = jobs.filter { 
        it.isActive &&
        (it.status == JobStatus.ACTIVE || it.status == JobStatus.DEADLINE_APPROACHING || it.status == JobStatus.UPCOMING) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.organization.contains(searchQuery, ignoreCase = true))
    }

    val historicalJobs = jobs.filter {
        it.isActive &&
        (it.status == JobStatus.EXPIRED || it.status == JobStatus.CLOSED) &&
        (it.title.contains(searchQuery, ignoreCase = true) || it.organization.contains(searchQuery, ignoreCase = true))
    }

    var selectedJobForDetail by remember { mutableStateOf<StandardizedJob?>(null) }

    if (selectedJobForDetail != null) {
        JobDetailDialog(
            job = selectedJobForDetail!!,
            eligibility = calculateEligibility(selectedJobForDetail!!),
            onDismiss = { selectedJobForDetail = null },
            onApply = { 
                val serviceFee = ServiceChargeEngine.calculateServiceFee(selectedJobForDetail!!.officialFee.replace("₹","").toInt())
                val totalAmount = "₹${selectedJobForDetail!!.officialFee.replace("₹","").toInt() + serviceFee}"
                onNavigateToApply(selectedJobForDetail!!.title, selectedJobForDetail!!.officialFee, "₹$serviceFee", totalAmount)
                selectedJobForDetail = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discovery Jobs", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search jobs, organizations...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { if(searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (visibleJobs.isNotEmpty()) {
                    item { Text("Active Listings", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp) }
                    items(visibleJobs) { job ->
                        JobCard(job, eligibility = calculateEligibility(job), onDetail = { selectedJobForDetail = job })
                    }
                }
                if (historicalJobs.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Past Listings", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                    }
                    items(historicalJobs) { job ->
                        JobCard(job, eligibility = calculateEligibility(job), onDetail = { selectedJobForDetail = job })
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(job: StandardizedJob, eligibility: EligibilityStatus, onDetail: () -> Unit) {
    Card(
        onClick = onDetail,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(job.status == JobStatus.EXPIRED || job.status == JobStatus.CLOSED) Color(0xFFFAFAFA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if(job.status == JobStatus.ACTIVE) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(job.status == JobStatus.EXPIRED) Color.Gray else Color(0xFF1976D2))
                    Text(job.organization, color = Color.Gray, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = job.status.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, job.status.color)
                    ) {
                        Text(text = job.status.label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = job.status.color)
                    }
                    Spacer(Modifier.height(4.dp))
                    Surface(color = eligibility.color.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text(text = if (eligibility == EligibilityStatus.ELIGIBLE) "Eligible ✓" else eligibility.label, color = eligibility.color, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    val dateColor = if(job.status == JobStatus.DEADLINE_APPROACHING) Color(0xFFD32F2F) else Color.Gray
                    Text("Deadline: ${job.lastDate}", fontSize = 12.sp, color = dateColor, fontWeight = if(job.status == JobStatus.DEADLINE_APPROACHING) FontWeight.Bold else FontWeight.Normal)
                    Text("Fee: ${job.officialFee} + ${job.serviceCharge}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailDialog(job: StandardizedJob, eligibility: EligibilityStatus, onDismiss: () -> Unit, onApply: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onApply, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { 
                Text("Apply Through Our Service", fontWeight = FontWeight.Bold) 
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Close") }
        },
        title = { 
            Column {
                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text(job.organization, color = Color.Gray, fontSize = 14.sp)
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Description Section
                Text("Description", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                Text(job.description, fontSize = 14.sp, color = Color.DarkGray)
                
                HorizontalDivider(color = Color(0xFFF5F5F5))
                
                // Eligibility Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Eligibility Status: ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(eligibility.label, color = eligibility.color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                
                DetailItem("Age Limit", job.ageLimit)
                DetailItem("Qualification", job.qualification)
                DetailItem("Branch/Stream", job.branch)
                DetailItem("Category Rules", job.categoryRules)
                DetailItem("Location", job.location)
                DetailItem("Salary", job.salary)
                
                HorizontalDivider(color = Color(0xFFF5F5F5))

                // Fees Section
                Text("Fee Structure", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                DetailItem("Official Application Fee", job.officialFee)
                DetailItem("Our Service Fee", job.serviceCharge)
                val total = job.officialFee.replace("₹","").toInt() + job.serviceCharge.replace("₹","").toInt()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payable Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("₹$total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4CAF50))
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))

                // Dates Section
                DetailItem("Application Start", job.startDate)
                DetailItem("Application Deadline", job.lastDate)

                // Links Section
                Spacer(Modifier.height(8.dp))
                Button(onClick = { /* Open jobUrl */ }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))) {
                    Icon(Icons.Default.Language, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Official Website")
                }
                OutlinedButton(onClick = { /* Open notificationUrl */ }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PictureAsPdf, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Official Notification")
                }

                Spacer(Modifier.height(8.dp))
                Text("Important Instructions", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFD32F2F))
                Text(job.importantInstructions, fontSize = 13.sp, color = Color.DarkGray)
            }
        }
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
