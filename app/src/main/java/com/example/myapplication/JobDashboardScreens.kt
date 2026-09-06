package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentJobDashboardScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToJobs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToApply: (String, String, String, String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit,
    jobViewModel: JobViewModel = viewModel(factory = LocalAppViewModelFactory.current)
) {
    val jobsState by jobViewModel.jobsUiState.collectAsState()

    // Student profile (mock for now, should come from ProfileViewModel)
    val studentProfile = remember {
        mapOf("age" to 21, "degree" to "B.Tech", "branch" to "CSE", "cgpa" to 7.47, "state" to "Bihar")
    }

    // Eligibility Logic using JobEntity
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
                title = { Text("Cyber Cafe Hub", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = false, onClick = onNavigateToHome)
                NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Jobs") }, selected = true, onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = false, onClick = onNavigateToProfile)
            }
        }
    ) { innerPadding ->
        when (val state = jobsState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading jobs: ${state.message}", color = Color.Red)
                }
            }
            is UiState.Empty -> {
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No jobs found.", color = Color.Gray)
                }
            }
            is UiState.Success -> {
                val allJobs = state.data
                LazyColumn(
                    modifier = Modifier.padding(innerPadding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        DashboardJobSection(
                            title = "Recommended Jobs",
                            subtitle = "Based on your profile",
                            jobs = allJobs.filter { calculateEligibility(it) == EligibilityStatus.ELIGIBLE },
                            onJobClick = { selectedJobForDetail = it }
                        )
                    }

                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CategorySmallCard("Government", allJobs.count { it.jobType == "Government" }.toString(), Icons.Default.AccountBalance, Color(0xFF1976D2), Modifier.weight(1f))
                            CategorySmallCard("Private", allJobs.count { it.jobType == "Private" }.toString(), Icons.Default.Business, Color(0xFF388E3C), Modifier.weight(1f))
                        }
                    }

                    item {
                        Text("All Available Jobs", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            allJobs.forEach { job ->
                                CompactJobRow(job, onClick = { selectedJobForDetail = job })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardJobSection(title: String, subtitle: String, jobs: List<JobEntity>, onJobClick: (JobEntity) -> Unit) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(jobs) { job ->
                ElevatedJobCard(job, Color(0xFF1976D2), onClick = { onJobClick(job) })
            }
        }
    }
}

@Composable
fun ElevatedJobCard(job: JobEntity, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(260.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(job.organization, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(job.officialFee, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("View Details", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategorySmallCard(title: String, count: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(count, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(title, fontSize = 12.sp, color = color)
        }
    }
}

@Composable
fun CompactJobRow(job: JobEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFFF5F5F5)) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.WorkOutline, null, tint = Color.Gray) }
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(job.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("${job.organization} • ${job.location}", fontSize = 13.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
    }
}
