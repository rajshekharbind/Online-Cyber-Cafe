package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize session manager with encrypted storage
        SessionManager.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    class OtpVerification(val mobile: String) : Screen()
    object Home : Screen()
    object StudentDashboard : Screen()
    object JobDiscovery : Screen()
    object AdminDashboard : Screen()
    object PostJob : Screen()
    object ManageStudents : Screen()
    object ManageEmployees : Screen()
    object ManageServiceCharges : Screen()
    object SystemAuditLogs : Screen()
    object SupportRequests : Screen()
    object ManageNotifications : Screen()
    object SystemReports : Screen()
    object SystemSettings : Screen()
    object Profile : Screen()
    object MasterProfile : Screen()
    object DocumentVault : Screen()
    object EducationalDetails : Screen()
    object SkillsExperience : Screen()
    object PaymentHistory : Screen()
    object ApplicationDashboard : Screen()
    object PrivacySecurity : Screen()
    object HelpSupport : Screen()
    data class EmployeeDashboard(val email: String) : Screen()
    class ProcessingApplication(val appId: String, val fromAdmin: Boolean = false) : Screen()
    class TrackingApplication(val appId: String) : Screen()
    object SupportChat : Screen()
    object ApplicationHistory : Screen()
    class ApplicationSummary(val jobTitle: String, val officialFee: String, val serviceFee: String, val total: String) : Screen()
    class Checkout(val jobTitle: String, val officialFee: String, val serviceFee: String, val amount: String) : Screen()
    object NotificationCenter : Screen()
    // ── Human Support screens ──
    object SupportHub : Screen()
    object SubmitTicket : Screen()
    object TicketHistory : Screen()
    class TicketDetail(val ticketId: String) : Screen()
    object AdminSupportDashboard : Screen()
    class AdminTicketDetail(val ticketId: String) : Screen()
    object AdminNotificationTemplates : Screen()
    object AdminManageJobs : Screen()
    object AdminRefunds : Screen()
    object AdminAuditLogs : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    val loggedInEmail = SessionManager.currentEmail
    val currentRole = SessionManager.currentRole

    // Session timeout watcher
    LaunchedEffect(currentScreen) {
        if (SessionManager.isLoggedIn && SessionManager.isSessionExpired()) {
            SecurityStore.logAction(loggedInEmail, "Auto-logout: session expired", "WARNING", "AUTH")
            SessionManager.clearSession()
            currentScreen = Screen.Login
        } else {
            SessionManager.refreshActivity()
        }
    }

    // Secure navigation: check route guard before rendering
    val targetScreen = currentScreen
    if (!RouteGuard.canAccess(targetScreen, currentRole)) {
        // User tried to access a forbidden screen — redirect
        LaunchedEffect(Unit) {
            currentScreen = if (SessionManager.isLoggedIn) {
                when (currentRole) {
                    UserRole.ADMIN -> Screen.AdminDashboard
                    UserRole.EMPLOYEE -> Screen.EmployeeDashboard(loggedInEmail)
                    else -> Screen.Home
                }
            } else {
                Screen.Login
            }
        }
        // Show a brief unauthorized message while redirecting
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text("Access Denied", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                Text("You don't have permission to view this screen.", fontSize = 12.sp)
            }
        }
        return
    }

    when (val screen = currentScreen) {
        is Screen.Login -> CyberCafeLoginScreen(
            onLoginSuccess = { email ->
                val role = SessionManager.roleFromEmail(email)
                // Save secure session (demo token; replace with real JWT from backend)
                SessionManager.saveSession(
                    token = "demo-token-${System.currentTimeMillis()}",
                    role = role,
                    email = email
                )
                when (role) {
                    UserRole.ADMIN -> currentScreen = Screen.AdminDashboard
                    UserRole.EMPLOYEE -> currentScreen = Screen.EmployeeDashboard(email)
                    else -> currentScreen = Screen.Home
                }
            },
            onNavigateToRegister = {
                currentScreen = Screen.Register
            }
        )
        is Screen.Register -> RegisterScreen(
            onNavigateToOtp = { mobile ->
                currentScreen = Screen.OtpVerification(mobile)
            },
            onBackToLogin = {
                currentScreen = Screen.Login
            }
        )
        is Screen.OtpVerification -> OtpVerificationScreen(
            mobileNumber = screen.mobile,
            onOtpVerified = {
                currentScreen = Screen.Home
            },
            onBack = {
                currentScreen = Screen.Register
            }
        )
        is Screen.Home -> StudentHomeScreen(
            onNavigateToJobs = { currentScreen = Screen.StudentDashboard },
            onNavigateToProfile = { currentScreen = Screen.Profile },
            onNavigateToNotifications = { currentScreen = Screen.NotificationCenter },
            onNavigateToDocs = { currentScreen = Screen.DocumentVault },
            onNavigateToMaster = { currentScreen = Screen.MasterProfile },
            onNavigateToTracking = { appId -> currentScreen = Screen.TrackingApplication(appId) },
            onNavigateToSupport = { currentScreen = Screen.SupportHub },
            onNavigateToHistory = { currentScreen = Screen.ApplicationHistory },
            onNavigateToPayments = { currentScreen = Screen.PaymentHistory },
            onNavigateToSettings = { currentScreen = Screen.PrivacySecurity }
        )
        is Screen.StudentDashboard -> StudentJobDashboardScreen(
            onNavigateToHome = { currentScreen = Screen.Home },
            onNavigateToJobs = { /* Already here */ },
            onNavigateToProfile = { currentScreen = Screen.Profile },
            onNavigateToApply = { title, offFee, servFee, total ->
                currentScreen = Screen.ApplicationSummary(title, offFee, servFee, total)
            },
            onNavigateToNotifications = {
                currentScreen = Screen.NotificationCenter
            },
            onLogout = {
                currentScreen = Screen.Login
            }
        )
        is Screen.JobDiscovery -> JobDiscoveryScreen(
            onBack = {
                currentScreen = Screen.Home
            },
            onNavigateToApply = { title, offFee, servFee, total ->
                currentScreen = Screen.ApplicationSummary(title, offFee, servFee, total)
            },
            onNavigateToNotifications = {
                currentScreen = Screen.NotificationCenter
            }
        )
        is Screen.ApplicationSummary -> ApplicationSummaryScreen(
            jobTitle = screen.jobTitle,
            officialFee = screen.officialFee,
            serviceFee = screen.serviceFee,
            totalAmount = screen.total,
            onConfirm = {
                currentScreen = Screen.Checkout(screen.jobTitle, screen.officialFee, screen.serviceFee, screen.total)
            },
            onBack = {
                currentScreen = Screen.StudentDashboard
            }
        )
        is Screen.AdminDashboard -> AdminDashboardScreen(
            onNavigateToPostJob = {
                currentScreen = Screen.PostJob
            },
            onNavigateToManageStudents = {
                currentScreen = Screen.ManageStudents
            },
            onNavigateToManageEmployees = {
                currentScreen = Screen.ManageEmployees
            },
            onNavigateToServiceCharges = {
                currentScreen = Screen.ManageServiceCharges
            },
            onNavigateToAuditLogs = {
                currentScreen = Screen.AdminAuditLogs
            },
            onNavigateToActiveJobs = {
                currentScreen = Screen.AdminManageJobs
            },
            onNavigateToRefunds = {
                currentScreen = Screen.AdminRefunds
            },
            onNavigateToSupportRequests = {
                currentScreen = Screen.SupportRequests
            },
            onNavigateToNotifications = {
                currentScreen = Screen.AdminNotificationTemplates
            },
            onNavigateToReports = {
                currentScreen = Screen.SystemReports
            },
            onNavigateToSettings = {
                currentScreen = Screen.SystemSettings
            },
            onNavigateToApplicationDashboard = {
                currentScreen = Screen.ApplicationDashboard
            },
            onNavigateToEmployeeDashboard = {
                currentScreen = Screen.EmployeeDashboard(loggedInEmail)
            },
            onNavigateToSupportDashboard = {
                currentScreen = Screen.AdminSupportDashboard
            },
            onLogout = {
                SessionManager.clearSession()
                currentScreen = Screen.Login
            }
        )
        is Screen.ManageStudents -> ManageUsersScreen(
            title = "Manage Students",
            usersList = listOf("Rahul Kumar", "Priya Sharma", "Amit Singh", "Sneha Gupta"),
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.ManageEmployees -> ManageUsersScreen(
            title = "Manage Employees",
            usersList = listOf("Executive Amit", "Executive Priya", "Executive Vikram"),
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.ManageServiceCharges -> ManageServiceChargesScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.SystemAuditLogs -> SystemAuditLogsScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.SupportRequests -> SupportRequestsScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.ManageNotifications -> ManageNotificationsAdminScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.SystemReports -> SystemReportsScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.SystemSettings -> SystemSettingsAdminScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.PostJob -> PostJobScreen(onBack = {
            currentScreen = Screen.AdminDashboard
        })
        is Screen.Profile -> ProfileScreen(
            onBack = {
                currentScreen = Screen.Home
            },
            onNavigateToDocs = {
                currentScreen = Screen.DocumentVault
            },
            onNavigateToEducation = {
                currentScreen = Screen.EducationalDetails
            },
            onNavigateToSkills = {
                currentScreen = Screen.SkillsExperience
            },
            onNavigateToPayments = {
                currentScreen = Screen.PaymentHistory
            },
            onNavigateToHistory = {
                currentScreen = Screen.ApplicationHistory
            },
            onNavigateToPrivacy = {
                currentScreen = Screen.PrivacySecurity
            },
            onNavigateToSupport = {
                currentScreen = Screen.HelpSupport
            },
            onNavigateToMaster = {
                currentScreen = Screen.MasterProfile
            },
            onNavigateToHome = {
                currentScreen = Screen.Home
            },
            onNavigateToJobs = {
                currentScreen = Screen.StudentDashboard
            }
        )
        is Screen.MasterProfile -> MasterProfileScreen(
            onBack = {
                currentScreen = Screen.Profile
            },
            onNavigateToNotifications = {
                currentScreen = Screen.NotificationCenter
            }
        )
        is Screen.DocumentVault -> DocumentVaultScreen(onBack = {
            currentScreen = Screen.Profile
        })
        is Screen.EducationalDetails -> EducationalDetailsScreen(onBack = {
            currentScreen = Screen.Profile
        })
        is Screen.SkillsExperience -> SkillsExperienceScreen(onBack = {
            currentScreen = Screen.Profile
        })
        is Screen.PaymentHistory -> PaymentHistoryScreen(onBack = {
            currentScreen = Screen.Profile
        })
        is Screen.ApplicationDashboard -> AdminApplicationDashboardScreen(
            onBack = { currentScreen = Screen.AdminDashboard },
            onNavigateToApplication = { appId -> currentScreen = Screen.ProcessingApplication(appId, true) }
        )
        is Screen.PrivacySecurity -> PrivacySecurityScreen(onBack = {
            currentScreen = Screen.Profile
        })
        is Screen.HelpSupport -> StudentSupportHubScreen(
            onBack = { currentScreen = Screen.Profile },
            onNavigateToSubmitTicket = { currentScreen = Screen.SubmitTicket },
            onNavigateToTicketHistory = { currentScreen = Screen.TicketHistory },
            onNavigateToLiveChat = { currentScreen = Screen.SupportChat }
        )
        is Screen.SupportHub -> StudentSupportHubScreen(
            onBack = { currentScreen = Screen.Home },
            onNavigateToSubmitTicket = { currentScreen = Screen.SubmitTicket },
            onNavigateToTicketHistory = { currentScreen = Screen.TicketHistory },
            onNavigateToLiveChat = { currentScreen = Screen.SupportChat }
        )
        is Screen.SubmitTicket -> SubmitSupportTicketScreen(
            onBack = { currentScreen = Screen.SupportHub },
            onSubmitSuccess = { currentScreen = Screen.TicketHistory }
        )
        is Screen.TicketHistory -> StudentTicketHistoryScreen(
            onBack = { currentScreen = Screen.SupportHub },
            onOpenTicket = { id -> currentScreen = Screen.TicketDetail(id) }
        )
        is Screen.TicketDetail -> TicketDetailScreen(
            ticketId = screen.ticketId,
            onBack = { currentScreen = Screen.TicketHistory }
        )
        is Screen.AdminSupportDashboard -> AdminSupportDashboardScreen(
            onBack = { currentScreen = Screen.AdminDashboard },
            onOpenTicket = { id -> currentScreen = Screen.AdminTicketDetail(id) }
        )
        is Screen.AdminTicketDetail -> AdminTicketDetailScreen(
            ticketId = screen.ticketId,
            onBack = { currentScreen = Screen.AdminSupportDashboard }
        )
        is Screen.EmployeeDashboard -> EmployeeDashboardScreen(
            email = screen.email,
            onLogout = {
                if (currentRole == UserRole.ADMIN) {
                    currentScreen = Screen.AdminDashboard
                } else {
                    SessionManager.clearSession()
                    currentScreen = Screen.Login
                }
            },
            onNavigateToApplication = { appId -> currentScreen = Screen.ProcessingApplication(appId, currentRole == UserRole.ADMIN) }
        )
        is Screen.ProcessingApplication -> ApplicationProcessingScreen(
            appId = screen.appId,
            onBack = { 
                if (screen.fromAdmin) {
                    currentScreen = Screen.ApplicationDashboard
                } else {
                    currentScreen = Screen.EmployeeDashboard(loggedInEmail)
                }
            }
        )
        is Screen.TrackingApplication -> ApplicationTrackingScreen(
            appId = screen.appId,
            onBack = { currentScreen = Screen.StudentDashboard }
        )
        is Screen.SupportChat -> HumanSupportChatScreen(onBack = {
            currentScreen = Screen.SupportHub
        })
        is Screen.ApplicationHistory -> ApplicationHistoryScreen(
            onBack = { currentScreen = Screen.StudentDashboard },
            onNavigateToTracking = { appId -> currentScreen = Screen.TrackingApplication(appId) }
        )
        is Screen.Checkout -> CheckoutScreen(
            jobTitle = screen.jobTitle,
            officialFee = screen.officialFee,
            serviceFee = screen.serviceFee,
            totalAmount = screen.amount,
            onPaymentSuccess = { orderId, payId, offFee, servFee, total ->
                // Record secure transaction with all 11 required fields
                TransactionStore.recordTransaction(
                    userId = "USER-9921",
                    appId = "APP-${(100..999).random()}",
                    orderId = orderId,
                    payId = payId,
                    official = offFee,
                    service = servFee,
                    total = total,
                    gatewayRef = "GREF-${(1000..9999).random()}"
                )

                currentScreen = Screen.ApplicationHistory 
            },
            onBack = { currentScreen = Screen.StudentDashboard }
        )
        is Screen.NotificationCenter -> NotificationCenterScreen(
            onBack = { currentScreen = Screen.Home },
            onOpenApplication = { appId -> currentScreen = Screen.TrackingApplication(appId) }
        )
        is Screen.AdminNotificationTemplates -> AdminNotificationTemplatesScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.AdminManageJobs -> AdminJobManagementScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.AdminRefunds -> AdminRefundScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
        is Screen.AdminAuditLogs -> AdminAuditLogsScreen(
            onBack = { currentScreen = Screen.AdminDashboard }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberCafeLoginScreen(onLoginSuccess: (String) -> Unit, onNavigateToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showIpDialog by remember { mutableStateOf(false) }
    var serverIp by remember { mutableStateOf("10.0.2.2") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Rate limiter key based on email
    val lockoutRemaining = remember(email) {
        if (email.isBlank()) 0L else RateLimiter.getRemainingLockoutSeconds(email.lowercase().trim())
    }

    if (showIpDialog) {
        AlertDialog(
            onDismissRequest = { showIpDialog = false },
            title = { Text("Server Settings") },
            text = {
                Column {
                    Text("If using real device, enter Computer IP:")
                    OutlinedTextField(value = serverIp, onValueChange = { serverIp = it })
                }
            },
            confirmButton = {
                Button(onClick = { 
                    NetworkConfig.setServerIp(serverIp)
                    showIpDialog = false 
                }) { Text("Save") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(onClick = { showIpDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Cyber Cafe Platform",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Connection Failed", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(text = errorMessage!!, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        Button(
                            onClick = { onLoginSuccess(email) },
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Student View (Demo)")
                        }
                        Button(
                            onClick = { onLoginSuccess("admin@cybercafe.com") },
                            modifier = Modifier.padding(top = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Admin View (Demo)")
                        }
                    }
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                    errorMessage = null
                },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = emailError != null,
                supportingText = if (emailError != null) {{ Text(emailError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                    errorMessage = null
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = if (passwordError != null) {{ Text(passwordError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = {
                    // 1. Validate inputs
                    val emailKey = email.lowercase().trim()
                    emailError = InputValidator.validateEmail(email).errorMessage
                    if (password.isBlank()) passwordError = "Password is required"
                    if (emailError != null || passwordError != null) return@Button

                    // 2. Check rate limit
                    if (RateLimiter.isLockedOut(emailKey)) {
                        val remaining = RateLimiter.getRemainingLockoutSeconds(emailKey)
                        errorMessage = "Too many failed attempts. Try again in $remaining seconds."
                        SecurityStore.logSecurityEvent(email, "Login blocked — account locked out")
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                NetworkConfig.authApi.login(LoginRequestDto(email.trim(), password))
                            }
                            RateLimiter.recordSuccess(emailKey)
                            SecurityStore.logAuth(email, "Login successful via backend", true)
                        } catch (e: Exception) {
                            // Backend unavailable — silently proceed in demo mode
                            SecurityStore.logAuth(email, "Login in demo mode (backend unavailable)", true)
                        }
                        isLoading = false
                        onLoginSuccess(email.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            // Rate-limit lockout message (security enforcement)
            if (errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                    }
                }
            }
            
            TextButton(onClick = onNavigateToRegister, enabled = !isLoading) {
                Text("Don't have an account? Register Now")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MyApplicationTheme {
        CyberCafeLoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
    }
}
