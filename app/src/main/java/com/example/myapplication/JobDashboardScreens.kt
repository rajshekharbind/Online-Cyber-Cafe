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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentJobDashboardScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToJobs: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToApply: (String, String, String, String) -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    // Shared state for jobs
    val allJobs = remember {
        mutableStateListOf(
            StandardizedJob(
                "JOB-101", "SSC CGL 2024", "SSC", "Combined Graduate Level Examination for various Group B & C posts.", "Central", "Government", "India", "Degree", "Any", "18-32", "OBC/SC/ST as per Govt", "Fresher", "₹45k-1.5L", "24 Jun 2024", "15 Sep 2024", "₹100", "₹50", "https://ssc.gov.in", "https://ssc.gov.in/notif", "Ensure all documents are clear before upload.", "Official SSC Portal", JobStatus.ACTIVE, "10 mins ago"
            ),
            StandardizedJob(
                "JOB-106", "Specialist Officer", "SBI", "Recruitment of Junior Associates and Specialist Officers in SBI.", "Banking", "Government", "India", "B.Tech", "CSE/IT", "18-27", "Min 7.0 CGPA", "1 Year", "₹65k+", "15 Aug 2024", "25 Aug 2024", "₹750", "₹50", "https://sbi.co.in", "https://sbi.co.in/careers", "Keep mobile ready for OTP during payment.", "SBI Careers", JobStatus.ACTIVE, "Just now"
            ),
            StandardizedJob(
                "JOB-102", "Software Engineer", "TCS", "Software development roles at TCS.", "IT", "Private", "Noida", "B.Tech", "CSE/IT", "21-28", "N/A", "1-2 Years", "₹6.5 LPA", "01 Aug 2024", "30 Aug 2024", "₹0", "₹100", "https://tcs.com", "https://tcs.com/careers", "Check email for interview slots.", "TCS Careers", JobStatus.ACTIVE, "1 hour ago"
            ),
            StandardizedJob(
                "JOB-103", "Railway Technician", "RRB", "Railways Recruitment Board - Technician Grade III.", "Railways", "Government", "Zonal", "Diploma", "Mechanical", "18-30", "Govt Rules", "ITI/Diploma", "₹35k+", "10 Jul 2024", "05 Oct 2024", "₹500", "₹50", "https://rrb.gov.in", "https://rrb.gov.in/notif", "Offline signature scan required.", "RRB Website", JobStatus.ACTIVE, "Recently"
            )
        )
    }

    // Student profile
    val studentProfile = remember {
        mapOf("age" to 21, "degree" to "B.Tech", "branch" to "CSE", "cgpa" to 7.47, "state" to "Bihar")
    }

    // Eligibility Logic (duplicated for simplicity in this demo, in real app move to ViewModel)
    fun calculateEligibility(job: StandardizedJob): EligibilityStatus {
        val studentAge = studentProfile["age"] as Int
        val studentDegree = studentProfile["degree"] as String
        val studentBranch = studentProfile["branch"] as String
        val studentCgpa = studentProfile["cgpa"] as Double
        val studentState = studentProfile["state"] as String

        if (job.ageLimit.contains("-")) {
            val parts = job.ageLimit.split("-")
            val min = parts[0].toIntOrNull() ?: 0
            val max = parts[1].toIntOrNull() ?: 99
            if (studentAge < min || studentAge > max) return EligibilityStatus.NOT_ELIGIBLE
        }
        if (job.qualification != "Degree" && job.qualification != "Any" && job.qualification != studentDegree) return EligibilityStatus.NOT_ELIGIBLE
        if (job.branch != "Any" && !job.branch.contains(studentBranch)) return EligibilityStatus.POTENTIALLY_ELIGIBLE
        if (job.categoryRules.contains("CGPA")) {
            val req = job.categoryRules.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            if (studentCgpa < req) return EligibilityStatus.NOT_ELIGIBLE
        }
        return EligibilityStatus.ELIGIBLE
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
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                DashboardJobSection(
                    title = "Recommended Jobs",
                    subtitle = "Based on your B.Tech CSE profile",
                    jobs = allJobs.filter { calculateEligibility(it) == EligibilityStatus.ELIGIBLE },
                    onJobClick = { selectedJobForDetail = it }
                )
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategorySmallCard("Government", allJobs.count { it.type == "Government" }.toString(), Icons.Default.AccountBalance, Color(0xFF1976D2), Modifier.weight(1f))
                    CategorySmallCard("Private", allJobs.count { it.type == "Private" }.toString(), Icons.Default.Business, Color(0xFF388E3C), Modifier.weight(1f))
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

@Composable
fun DashboardJobSection(title: String, subtitle: String, jobs: List<StandardizedJob>, onJobClick: (StandardizedJob) -> Unit) {
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
fun ElevatedJobCard(job: StandardizedJob, color: Color, onClick: () -> Unit) {
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
fun CompactJobRow(job: StandardizedJob, onClick: () -> Unit) {
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
