package com.example.myapplication

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillsExperienceScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Skills", "Experience", "Projects")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skills & Experience") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            SecondaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> SkillsSection()
                    1 -> ExperienceSection()
                    2 -> ProjectsSection()
                }
            }
        }
    }
}

@Composable
fun SkillsSection() {
    val techSkills = remember { mutableStateListOf("Android", "Kotlin", "Java", "Firebase") }
    val softSkills = remember { mutableStateListOf("Communication", "Leadership", "Teamwork") }
    val languages = remember { mutableStateListOf("English", "Hindi", "Marathi") }

    var selectedResumeUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedResumeUri = uri
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SkillCategory("Technical Skills", techSkills)
        SkillCategory("Soft Skills", softSkills)
        SkillCategory("Languages", languages)
        
        Spacer(Modifier.height(20.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CloudUpload, null, tint = Color(0xFF1976D2))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Resume / CV", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        if (selectedResumeUri == null) "Last updated: 24 Aug 2024" else "Selected: ${selectedResumeUri!!.path?.takeLast(15)}",
                        fontSize = 12.sp, 
                        color = Color.Gray
                    )
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = { launcher.launch("application/pdf,image/*") },
                    shape = RoundedCornerShape(20.dp)
                ) { 
                    Text("Update") 
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillCategory(title: String, skills: MutableList<String>) {
    var showDialog by remember { mutableStateOf(false) }
    var newSkill by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add $title") },
            text = {
                OutlinedTextField(value = newSkill, onValueChange = { newSkill = it }, label = { Text("Skill Name") })
            },
            confirmButton = {
                Button(onClick = {
                    if (newSkill.isNotBlank()) {
                        skills.add(newSkill)
                        newSkill = ""
                        showDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(24.dp), tint = Color(0xFF1976D2))
            }
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            skills.forEach { skill ->
                InputChip(
                    selected = true,
                    onClick = { skills.remove(skill) },
                    label = { Text(skill) },
                    trailingIcon = { Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
    }
}

@Composable
fun ExperienceSection() {
    val workExp = remember { mutableStateListOf(
        ExperienceItem("Software Intern", "Tech Corp", "2023 - Present", "Working on mobile apps"),
        ExperienceItem("Campus Ambassador", "College", "2022 - 2023", "Promoted events")
    ) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditIndex by remember { mutableStateOf<Int?>(null) }

    if (showAddDialog || showEditIndex != null) {
        var role by remember { mutableStateOf(if (showEditIndex != null) workExp[showEditIndex!!].role else "") }
        var company by remember { mutableStateOf(if (showEditIndex != null) workExp[showEditIndex!!].company else "") }
        var duration by remember { mutableStateOf(if (showEditIndex != null) workExp[showEditIndex!!].duration else "") }
        var desc by remember { mutableStateOf(if (showEditIndex != null) workExp[showEditIndex!!].desc else "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false; showEditIndex = null },
            title = { Text(if (showAddDialog) "Add Experience" else "Edit Experience") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Role") })
                    OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company") })
                    OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newItem = ExperienceItem(role, company, duration, desc)
                    if (showAddDialog) workExp.add(newItem) else workExp[showEditIndex!!] = newItem
                    showAddDialog = false; showEditIndex = null
                }) { Text("Save") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Work & Internship", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(20.dp)
            ) { 
                Text("Add New") 
            }
        }

        workExp.forEachIndexed { index, item ->
            ExperienceCard(
                item = item, 
                onDelete = { workExp.remove(item) },
                onEdit = { showEditIndex = index }
            )
        }

        Text("Certifications", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Google Cloud Professional", fontWeight = FontWeight.Bold)
                Text("Issued: Jan 2024", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ExperienceCard(item: ExperienceItem, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.role, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 18.sp, modifier = Modifier.weight(1f))
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp)) }
                }
            }
            Text(item.company, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text(item.duration, fontSize = 12.sp, color = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(item.desc, fontSize = 14.sp)
        }
    }
}

data class ExperienceItem(val role: String, val company: String, val duration: String, val desc: String)

@Composable
fun ProjectsSection() {
    val projects = remember { mutableStateListOf(
        ProjectItem("Cyber Cafe App", "Kotlin, Compose", "Personal Project"),
        ProjectItem("E-Commerce Web", "React, Node.js", "Hackathon Entry")
    ) }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditIndex by remember { mutableStateOf<Int?>(null) }

    if (showAddDialog || showEditIndex != null) {
        var title by remember { mutableStateOf(if (showEditIndex != null) projects[showEditIndex!!].title else "") }
        var tech by remember { mutableStateOf(if (showEditIndex != null) projects[showEditIndex!!].tech else "") }
        var type by remember { mutableStateOf(if (showEditIndex != null) projects[showEditIndex!!].type else "") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false; showEditIndex = null },
            title = { Text(if (showAddDialog) "Add Project" else "Edit Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                    OutlinedTextField(value = tech, onValueChange = { tech = it }, label = { Text("Tech Used") })
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newItem = ProjectItem(title, tech, type)
                    if (showAddDialog) projects.add(newItem) else projects[showEditIndex!!] = newItem
                    showAddDialog = false; showEditIndex = null
                }) { Text("Save") }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Academic Projects", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(20.dp)
            ) { 
                Text("Add Project") 
            }
        }

        projects.forEachIndexed { index, item ->
            ProjectCard(
                item = item, 
                onDelete = { projects.remove(item) },
                onEdit = { showEditIndex = index }
            )
        }
    }
}

@Composable
fun ProjectCard(item: ProjectItem, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.weight(1f))
                Row {
                    IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(18.dp)) }
                    IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.Gray, modifier = Modifier.size(18.dp)) }
                }
            }
            Text(item.tech, fontSize = 14.sp, color = Color(0xFF388E3C), fontWeight = FontWeight.Medium)
            Text(item.type, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

data class ProjectItem(val title: String, val tech: String, val type: String)
