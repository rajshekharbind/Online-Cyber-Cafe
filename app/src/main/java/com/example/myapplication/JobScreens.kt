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
import androidx.lifecycle.viewmodel.compose.viewModel

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
    onNavigateToNotifications: () -> Unit,
    jobViewModel: JobViewModel = viewModel(factory = LocalAppViewModelFactory.current)
) {
    val jobsState by jobViewModel.jobsUiState.collectAsState()
    val searchQuery by jobViewModel.searchQuery.collectAsState()
    
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

    // Smart Eligibility Matching Logic
    fun calculateEligibility(job: JobEntity): EligibilityStatus {
        val studentCgpa = studentProfile["cgpa"] as Double
        val studentBranch = studentProfile["branch"] as String

        if (studentCgpa < job.minCgpa) return EligibilityStatus.NOT_ELIGIBLE
        if (job.eligibleBranches != "Any" && !job.eligibleBranches.contains(studentBranch)) {
            return EligibilityStatus.POTENTIALLY_ELIGIBLE
        }
        return EligibilityStatus.ELIGIBLE
    }

    var selectedJobForDetail by remember { mutableStateOf<JobEntity?>(null) }

    if (selectedJobForDetail != null) {
        val job = selectedJobForDetail!!
        JobDetailDialog(
            job = job,
            eligibility = calculateEligibility(job),
            onDismiss = { selectedJobForDetail = null },
            onApply = { 
                val officialFeeNum = job.officialFee.replace("₹", "").toIntOrNull() ?: 0
                val serviceFeeNum = ServiceChargeEngine.calculateServiceFee(officialFeeNum)
                val totalAmount = "₹${officialFeeNum + serviceFeeNum}"
                onNavigateToApply(job.title, job.officialFee, "₹$serviceFeeNum", totalAmount)
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
                onValueChange = { jobViewModel.setSearchQuery(it) },
                placeholder = { Text("Search jobs, organizations...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { if(searchQuery.isNotEmpty()) IconButton(onClick = { jobViewModel.setSearchQuery("") }) { Icon(Icons.Default.Clear, null) } }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (val state = jobsState) {
                is UiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading jobs: ${state.message}", color = Color.Red)
                    }
                }
                is UiState.Empty -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No jobs found matching your search.", color = Color.Gray)
                    }
                }
                is UiState.Success -> {
                    val allJobs = state.data
                    val visibleJobs = allJobs.filter { it.status == "ACTIVE" || it.status == "PUBLISHED" }
                    val historicalJobs = allJobs.filter { it.status == "CLOSED" || it.status == "EXPIRED" }

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
    }
}

@Composable
fun JobCard(job: JobEntity, eligibility: EligibilityStatus, onDetail: () -> Unit) {
    val jobStatus = try { JobStatus.valueOf(job.status) } catch (e: Exception) { JobStatus.CLOSED }
    Card(
        onClick = onDetail,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if(jobStatus == JobStatus.EXPIRED || jobStatus == JobStatus.CLOSED) Color(0xFFFAFAFA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if(jobStatus == JobStatus.ACTIVE) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(job.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if(jobStatus == JobStatus.EXPIRED) Color.Gray else Color(0xFF1976D2))
                    Text(job.organization, color = Color.Gray, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = jobStatus.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, jobStatus.color)
                    ) {
                        Text(text = jobStatus.label, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = jobStatus.color)
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
                    val dateColor = if(jobStatus == JobStatus.DEADLINE_APPROACHING) Color(0xFFD32F2F) else Color.Gray
                    Text("Deadline: ${job.applicationDeadline}", fontSize = 12.sp, color = dateColor, fontWeight = if(jobStatus == JobStatus.DEADLINE_APPROACHING) FontWeight.Bold else FontWeight.Normal)
                    Text("Fee: ${job.officialFee} + ${job.serviceFee}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailDialog(job: JobEntity, eligibility: EligibilityStatus, onDismiss: () -> Unit, onApply: () -> Unit) {
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
                
                DetailItem("Age Limit", "Min 18")
                DetailItem("Qualification", job.jobType)
                DetailItem("Branch/Stream", job.eligibleBranches)
                DetailItem("Category Rules", job.eligibilityCriteria)
                DetailItem("Location", job.location)
                DetailItem("Salary", job.salary)
                
                HorizontalDivider(color = Color(0xFFF5F5F5))

                // Fees Section
                Text("Fee Structure", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1976D2))
                DetailItem("Official Application Fee", job.officialFee)
                DetailItem("Our Service Fee", job.serviceFee)
                val officialFeeNum = job.officialFee.replace("₹", "").toIntOrNull() ?: 0
                val serviceFeeNum = job.serviceFee.replace("₹", "").toIntOrNull() ?: 0
                val total = officialFeeNum + serviceFeeNum
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Payable Amount", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("₹$total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4CAF50))
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))

                // Dates Section
                DetailItem("Application Start", job.applicationStartDate)
                DetailItem("Application Deadline", job.applicationDeadline)

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
                Text(job.requiredDocuments, fontSize = 13.sp, color = Color.DarkGray)
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
