package com.example.myapplication

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

data class VaultDocument(
    val type: String,
    val name: String,
    var status: String = "Not Uploaded",
    var uri: Uri? = null,
    val isDeletable: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVaultScreen(onBack: () -> Unit) {
    val documents = remember {
        mutableStateListOf(
            VaultDocument("Identity", "Passport-size Photograph", isDeletable = false),
            VaultDocument("Identity", "Signature", isDeletable = false),
            VaultDocument("Identity", "Aadhaar Card"),
            VaultDocument("Academic", "10th Marksheet"),
            VaultDocument("Academic", "12th Marksheet"),
            VaultDocument("Academic", "Graduation Marksheet"),
            VaultDocument("Academic", "Degree Certificate"),
            VaultDocument("Legal", "Category Certificate"),
            VaultDocument("Legal", "EWS Certificate"),
            VaultDocument("Legal", "PwD Certificate"),
            VaultDocument("Legal", "Domicile Certificate"),
            VaultDocument("Professional", "Experience Certificate"),
            VaultDocument("Professional", "Resume"),
            VaultDocument("Other", "Additional Certification 1")
        )
    }

    var selectedIndexForUpload by remember { mutableStateOf<Int?>(null) }
    var showViewDialog by remember { mutableStateOf<VaultDocument?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Int?>(null) }
    var fileValidationError by remember { mutableStateOf<String?>(null) } // Req 59

    val context = androidx.compose.ui.platform.LocalContext.current
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && selectedIndexForUpload != null) {
            // Req 59: Validate file type and size before accepting
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val allowedTypes = listOf("image/jpeg", "image/png", "application/pdf")
            val sizeBytes = context.contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: 0L
            val maxSizeBytes = 2 * 1024 * 1024L // 2 MB

            when {
                mimeType !in allowedTypes -> {
                    fileValidationError = "Invalid file type: '$mimeType'.\nOnly PDF, JPG, and PNG files are allowed."
                }
                sizeBytes > maxSizeBytes -> {
                    val sizeMb = String.format("%.1f", sizeBytes / (1024.0 * 1024.0))
                    fileValidationError = "File is too large (${sizeMb} MB).\nMaximum allowed size is 2 MB. Please compress or re-scan the document."
                }
                else -> {
                    val doc = documents[selectedIndexForUpload!!]
                    documents[selectedIndexForUpload!!] = doc.copy(uri = uri, status = "Pending Verification")
                }
            }
        }
        selectedIndexForUpload = null
    }

    // Req 59: File validation error dialog
    if (fileValidationError != null) {
        AlertDialog(
            onDismissRequest = { fileValidationError = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null, tint = Color.Red)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload Failed", fontWeight = FontWeight.Bold)
                }
            },
            text = { Text(fileValidationError!!) },
            confirmButton = { Button(onClick = { fileValidationError = null }) { Text("OK, Got It") } }
        )
    }

    if (showDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Document") },
            text = { Text("Are you sure you want to remove this document? You will need to upload it again for future applications.") },
            confirmButton = {
                Button(onClick = {
                    val doc = documents[showDeleteConfirm!!]
                    documents[showDeleteConfirm!!] = doc.copy(uri = null, status = "Not Uploaded")
                    showDeleteConfirm = null
                }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) { Text("Cancel") }
            }
        )
    }

    if (showViewDialog != null) {
        AlertDialog(
            onDismissRequest = { showViewDialog = null },
            title = { Text(showViewDialog!!.name) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (showViewDialog!!.uri != null) {
                        AsyncImage(
                            model = showViewDialog!!.uri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text("No preview available. Please upload the document first.")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showViewDialog = null }) { Text("Close") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Secure Document Vault", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Add Generic */ }) { Icon(Icons.Default.Add, null) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val grouped = documents.groupBy { it.type }
            grouped.forEach { (type, docs) ->
                item {
                    Text(
                        text = type,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                itemsIndexed(docs) { _, doc ->
                    val globalIndex = documents.indexOf(doc)
                    VaultItemCard(
                        doc = doc,
                        onUpload = { selectedIndexForUpload = globalIndex; pickerLauncher.launch("*/*") },
                        onView = { showViewDialog = doc },
                        onReplace = { selectedIndexForUpload = globalIndex; pickerLauncher.launch("*/*") },
                        onDelete = { showDeleteConfirm = globalIndex }
                    )
                }
            }
        }
    }
}

@Composable
fun VaultItemCard(
    doc: VaultDocument,
    onUpload: () -> Unit,
    onView: () -> Unit,
    onReplace: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = if (doc.uri != null) Color(0xFFE8F5E9) else Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (doc.name.contains("Photo")) Icons.Default.AccountBox else Icons.AutoMirrored.Filled.InsertDriveFile,
                            contentDescription = null,
                            tint = if (doc.uri != null) Color(0xFF4CAF50) else Color.Gray
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(
                        text = doc.status,
                        color = when(doc.status) {
                            "Verified" -> Color(0xFF4CAF50)
                            "Pending Verification" -> Color(0xFFFFA000)
                            else -> Color.Gray
                        },
                        fontSize = 12.sp
                    )
                }
                if (doc.uri != null) {
                    IconButton(onClick = onView) { Icon(Icons.Default.Visibility, null) }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (doc.uri == null) {
                    Button(
                        onClick = onUpload,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Upload, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Upload", fontSize = 12.sp)
                    }
                } else {
                    OutlinedButton(
                        onClick = onReplace,
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text("Replace", fontSize = 12.sp)
                    }
                    if (doc.isDeletable) {
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { /* Download */ }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Download, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit, 
    onNavigateToDocs: () -> Unit,
    onNavigateToEducation: () -> Unit,
    onNavigateToSkills: () -> Unit,
    onNavigateToPayments: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToMaster: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToJobs: () -> Unit
) {
    var name by remember { mutableStateOf("Rahul Kumar") }
    var phone by remember { mutableStateOf("+91 98765 43210") }
    var address by remember { mutableStateOf("Sector 62, Noida, UP") }
    val email = "rahul.k@example.com"
    val dob = "15 Aug 2002"

    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(name) }
    var tempPhone by remember { mutableStateOf(phone) }
    var tempAddress by remember { mutableStateOf(address) }

    var showPlaceholderDialog by remember { mutableStateOf<String?>(null) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempName, 
                        onValueChange = { tempName = it }, 
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone, 
                        onValueChange = { tempPhone = it }, 
                        label = { Text("Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempAddress, 
                        onValueChange = { tempAddress = it }, 
                        label = { Text("Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    name = tempName
                    phone = tempPhone
                    address = tempAddress
                    showEditDialog = false 
                }) { Text("Save Changes") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    tempName = name
                    tempPhone = phone
                    tempAddress = address
                    showEditDialog = false 
                }) { Text("Cancel") }
            }
        )
    }

    if (showPlaceholderDialog != null) {
        AlertDialog(
            onDismissRequest = { showPlaceholderDialog = null },
            title = { Text(showPlaceholderDialog!!) },
            text = { Text("This section is under construction. Features for ${showPlaceholderDialog!!.lowercase()} will be available soon.") },
            confirmButton = {
                Button(onClick = { showPlaceholderDialog = null }) { Text("Got it") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        tempName = name
                        tempPhone = phone
                        tempAddress = address
                        showEditDialog = true 
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = false, onClick = onNavigateToHome)
                NavigationBarItem(icon = { Icon(Icons.Default.Search, null) }, label = { Text("Jobs") }, selected = false, onClick = onNavigateToJobs)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = true, onClick = {})
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ProfileInfoRow("Name", name)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow("Email", email)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow("Phone", phone)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow("DOB", dob)
                    Spacer(modifier = Modifier.height(16.dp))
                    ProfileInfoRow("Address", address)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
            
            Text(
                text = "Account Settings",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProfileMenuItem("Personal Information", Icons.Default.Info, onClick = onNavigateToMaster)
            ProfileMenuItem("Educational Details", Icons.Default.School, onClick = onNavigateToEducation)
            ProfileMenuItem("Skills & Experience", Icons.Default.Work, onClick = onNavigateToSkills)
            ProfileMenuItem("My Applications (History)", Icons.Default.Assignment, onClick = onNavigateToHistory)
            ProfileMenuItem("Document Vault", Icons.Default.Description, onClick = onNavigateToDocs)
            ProfileMenuItem("Payment History", Icons.Default.History, onClick = onNavigateToPayments)
            ProfileMenuItem("Privacy & Security", Icons.Default.Security, onClick = onNavigateToPrivacy)
            ProfileMenuItem("Help & Support", Icons.AutoMirrored.Filled.Help, onClick = onNavigateToSupport)
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("Logout Account", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            Text("App Version 1.0.24", color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 15.sp)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: ImageVector, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

data class EducationDetail(
    val title: String,
    val boardOrUniversity: String,
    val year: String,
    val result: String,
    val rollNumber: String = "",
    val schoolOrCollege: String = "",
    val subjectsOrStream: String = "",
    val degree: String? = null,
    val startYear: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationalDetailsScreen(onBack: () -> Unit) {
    val educationList = remember {
        mutableStateListOf(
            EducationDetail("Class 10", "CBSE", "2018", "85%", "123456", "KV School", "Science, Maths"),
            EducationDetail("Class 12", "CBSE", "2020", "88%", "654321", "KV School", "PCM"),
            EducationDetail("Graduation", "AKTU", "2024", "8.2 CGPA", "REG123", "ABC College", "Computer Science", "B.Tech", "2020")
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Int?>(null) }

    var title by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var board by remember { mutableStateOf("") }
    var school by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var stream by remember { mutableStateOf("") }
    var startYear by remember { mutableStateOf("") }

    if (showAddDialog || showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                showEditDialog = null
            },
            title = { Text(if (showAddDialog) "Add Qualification" else "Edit Qualification") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isGrad = title.contains("Graduation", ignoreCase = true) || title.contains("Masters", ignoreCase = true)
                    
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Level (e.g. Class 10, B.Ed)") }, modifier = Modifier.fillMaxWidth())
                    
                    if (isGrad) {
                        OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree (e.g. B.Tech, B.Ed)") }, modifier = Modifier.fillMaxWidth())
                    }
                    
                    OutlinedTextField(value = board, onValueChange = { board = it }, label = { Text("Board / University") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School / College") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (isGrad) {
                            OutlinedTextField(value = startYear, onValueChange = { startYear = it }, label = { Text("Start Year") }, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(value = year, onValueChange = { year = it }, label = { Text("Passing Year") }, modifier = Modifier.weight(1f))
                    }
                    
                    OutlinedTextField(value = rollNo, onValueChange = { rollNo = it }, label = { Text("Roll / Reg Number") }, modifier = Modifier.fillMaxWidth())
                    
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = result, onValueChange = { result = it }, label = { Text("% / CGPA") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = stream, onValueChange = { stream = it }, label = { Text("Stream / Subjects") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newDetail = EducationDetail(title, board, year, result, rollNo, school, stream, if (degree.isBlank()) null else degree, startYear)
                    if (showAddDialog) {
                        educationList.add(newDetail)
                    } else {
                        educationList[showEditDialog!!] = newDetail
                    }
                    showAddDialog = false
                    showEditDialog = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    showEditDialog = null
                }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Educational Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        title = ""; degree = ""; board = ""; school = ""; year = ""; rollNo = ""; result = ""; stream = ""; startYear = ""
                        showAddDialog = true 
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add New")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            educationList.forEachIndexed { index, detail ->
                EducationSectionCard(detail.title) {
                    if (detail.degree != null) EducationInfoRow("Degree", detail.degree)
                    EducationInfoRow("Board/Univ", detail.boardOrUniversity)
                    EducationInfoRow("School/College", detail.schoolOrCollege)
                    EducationInfoRow("Roll Number", detail.rollNumber)
                    if (detail.startYear.isNotBlank()) EducationInfoRow("Start Year", detail.startYear)
                    EducationInfoRow("Passing Year", detail.year)
                    EducationInfoRow("Percentage/CGPA", detail.result)
                    EducationInfoRow("Stream/Subjects", detail.subjectsOrStream)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            title = detail.title
                            degree = detail.degree ?: ""
                            board = detail.boardOrUniversity
                            school = detail.schoolOrCollege
                            year = detail.year
                            rollNo = detail.rollNumber
                            result = detail.result
                            stream = detail.subjectsOrStream
                            startYear = detail.startYear
                            showEditDialog = index
                        }) { Text("Edit") }
                        TextButton(onClick = { educationList.removeAt(index) }) {
                            Text("Remove", color = Color.Red)
                        }
                    }
                }
            }
            
            Button(
                onClick = { 
                    title = ""; degree = ""; board = ""; school = ""; year = ""; rollNo = ""; result = ""; stream = ""; startYear = ""
                    showAddDialog = true 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add Qualification (B.Ed, Masters...)")
            }
        }
    }
}

@Composable
fun EducationSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2), fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun EducationInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
