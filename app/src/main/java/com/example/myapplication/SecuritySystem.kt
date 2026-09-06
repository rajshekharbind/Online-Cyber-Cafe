package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

// ─────────────────────────────────────────────────────────
// 1. ROLE-BASED ACCESS CONTROL (RBAC)
// ─────────────────────────────────────────────────────────

enum class UserRole {
    STUDENT,
    EMPLOYEE,
    ADMIN,
    GUEST
}

data class Permission(val name: String)

object Permissions {
    // Student permissions
    val VIEW_OWN_PROFILE = Permission("view_own_profile")
    val SUBMIT_APPLICATION = Permission("submit_application")
    val VIEW_OWN_APPLICATIONS = Permission("view_own_applications")
    val UPLOAD_DOCUMENTS = Permission("upload_documents")
    val VIEW_JOBS = Permission("view_jobs")
    val SUBMIT_SUPPORT_TICKET = Permission("submit_support_ticket")
    val VIEW_OWN_NOTIFICATIONS = Permission("view_own_notifications")
    val MAKE_PAYMENT = Permission("make_payment")

    // Employee permissions (includes all student read perms for assigned apps)
    val PROCESS_APPLICATION = Permission("process_application")
    val VIEW_ASSIGNED_APPLICATIONS = Permission("view_assigned_applications")
    val UPDATE_APPLICATION_STATUS = Permission("update_application_status")
    val VIEW_STUDENT_DOCUMENTS = Permission("view_student_documents")
    val RESPOND_SUPPORT_TICKET = Permission("respond_support_ticket")
    val VIEW_NOTIFICATIONS_DASHBOARD = Permission("view_notifications_dashboard")

    // Admin-only permissions
    val MANAGE_USERS = Permission("manage_users")
    val MANAGE_EMPLOYEES = Permission("manage_employees")
    val VIEW_ALL_APPLICATIONS = Permission("view_all_applications")
    val MANAGE_JOBS = Permission("manage_jobs")
    val VIEW_AUDIT_LOGS = Permission("view_audit_logs")
    val MANAGE_SERVICE_CHARGES = Permission("manage_service_charges")
    val PROCESS_REFUNDS = Permission("process_refunds")
    val MANAGE_NOTIFICATIONS = Permission("manage_notifications")
    val VIEW_SYSTEM_REPORTS = Permission("view_system_reports")
    val MANAGE_SYSTEM_SETTINGS = Permission("manage_system_settings")
    val DELETE_USER = Permission("delete_user")
    val MANAGE_SUPPORT_TICKETS = Permission("manage_support_tickets")
}

val STUDENT_PERMISSIONS = setOf(
    Permissions.VIEW_OWN_PROFILE,
    Permissions.SUBMIT_APPLICATION,
    Permissions.VIEW_OWN_APPLICATIONS,
    Permissions.UPLOAD_DOCUMENTS,
    Permissions.VIEW_JOBS,
    Permissions.SUBMIT_SUPPORT_TICKET,
    Permissions.VIEW_OWN_NOTIFICATIONS,
    Permissions.MAKE_PAYMENT
)

val EMPLOYEE_PERMISSIONS = STUDENT_PERMISSIONS + setOf(
    Permissions.PROCESS_APPLICATION,
    Permissions.VIEW_ASSIGNED_APPLICATIONS,
    Permissions.UPDATE_APPLICATION_STATUS,
    Permissions.VIEW_STUDENT_DOCUMENTS,
    Permissions.RESPOND_SUPPORT_TICKET,
    Permissions.VIEW_NOTIFICATIONS_DASHBOARD
)

val ADMIN_PERMISSIONS = EMPLOYEE_PERMISSIONS + setOf(
    Permissions.MANAGE_USERS,
    Permissions.MANAGE_EMPLOYEES,
    Permissions.VIEW_ALL_APPLICATIONS,
    Permissions.MANAGE_JOBS,
    Permissions.VIEW_AUDIT_LOGS,
    Permissions.MANAGE_SERVICE_CHARGES,
    Permissions.PROCESS_REFUNDS,
    Permissions.MANAGE_NOTIFICATIONS,
    Permissions.VIEW_SYSTEM_REPORTS,
    Permissions.MANAGE_SYSTEM_SETTINGS,
    Permissions.DELETE_USER,
    Permissions.MANAGE_SUPPORT_TICKETS
)

fun UserRole.getPermissions(): Set<Permission> = when (this) {
    UserRole.STUDENT -> STUDENT_PERMISSIONS
    UserRole.EMPLOYEE -> EMPLOYEE_PERMISSIONS
    UserRole.ADMIN -> ADMIN_PERMISSIONS
    UserRole.GUEST -> emptySet()
}

fun UserRole.hasPermission(permission: Permission): Boolean =
    this.getPermissions().contains(permission)

// ─────────────────────────────────────────────────────────
// 2. SESSION MANAGER (Secure Token & Role Storage)
// ─────────────────────────────────────────────────────────

object SessionManager {
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "user_role"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_LAST_ACTIVE = "last_active_ms"
    private const val SESSION_TIMEOUT_MS = 30 * 60 * 1000L // 30 minutes

    private var prefs: SharedPreferences? = null

    var currentRole by mutableStateOf(UserRole.GUEST)
        private set
    var currentEmail by mutableStateOf("")
        private set
    var isLoggedIn by mutableStateOf(false)
        private set
    var sessionToken by mutableStateOf("")
        private set

    fun init(context: Context) {
        prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "secure_session_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular prefs if encryption fails (e.g., emulator quirks)
            context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
        }
        restoreSession()
    }

    private fun restoreSession() {
        val p = prefs ?: return
        val token = p.getString(KEY_TOKEN, null) ?: return
        val lastActive = p.getLong(KEY_LAST_ACTIVE, 0L)
        val now = System.currentTimeMillis()

        if (now - lastActive > SESSION_TIMEOUT_MS) {
            // Session expired - clear everything
            clearSession()
            SecurityStore.logAction("SYSTEM", "Session expired for ${p.getString(KEY_EMAIL, "unknown")}", "WARNING")
            return
        }

        sessionToken = token
        currentRole = UserRole.valueOf(p.getString(KEY_ROLE, "GUEST") ?: "GUEST")
        currentEmail = p.getString(KEY_EMAIL, "") ?: ""
        isLoggedIn = token.isNotEmpty()
    }

    fun saveSession(token: String, role: UserRole, email: String) {
        sessionToken = token
        currentRole = role
        currentEmail = email
        isLoggedIn = true
        prefs?.edit()
            ?.putString(KEY_TOKEN, token)
            ?.putString(KEY_ROLE, role.name)
            ?.putString(KEY_EMAIL, email)
            ?.putLong(KEY_LAST_ACTIVE, System.currentTimeMillis())
            ?.apply()
        SecurityStore.logAction(email, "Logged in as ${role.name}", "INFO")
    }

    fun refreshActivity() {
        if (isLoggedIn) {
            prefs?.edit()?.putLong(KEY_LAST_ACTIVE, System.currentTimeMillis())?.apply()
        }
    }

    fun isSessionExpired(): Boolean {
        val lastActive = prefs?.getLong(KEY_LAST_ACTIVE, 0L) ?: return true
        return System.currentTimeMillis() - lastActive > SESSION_TIMEOUT_MS
    }

    fun clearSession() {
        val email = currentEmail
        prefs?.edit()?.clear()?.apply()
        sessionToken = ""
        currentRole = UserRole.GUEST
        currentEmail = ""
        isLoggedIn = false
        if (email.isNotEmpty()) {
            SecurityStore.logAction(email, "Logged out / Session cleared", "INFO")
        }
    }

    /** Determine role from email (for demo mode without backend) */
    fun roleFromEmail(email: String): UserRole = when {
        email.contains("admin", ignoreCase = true) -> UserRole.ADMIN
        email.contains("employee", ignoreCase = true) -> UserRole.EMPLOYEE
        email.isNotBlank() -> UserRole.STUDENT
        else -> UserRole.GUEST
    }
}

// ─────────────────────────────────────────────────────────
// 3. RATE LIMITER (Brute-force protection)
// ─────────────────────────────────────────────────────────

object RateLimiter {
    private const val MAX_ATTEMPTS = 5
    private const val LOCKOUT_DURATION_MS = 5 * 60 * 1000L // 5 minutes

    private val attemptCounts = mutableMapOf<String, Int>()
    private val lockoutTimes = mutableMapOf<String, Long>()

    fun recordFailedAttempt(key: String): Int {
        val currentCount = (attemptCounts[key] ?: 0) + 1
        attemptCounts[key] = currentCount
        if (currentCount >= MAX_ATTEMPTS) {
            lockoutTimes[key] = System.currentTimeMillis()
            SecurityStore.logAction("SYSTEM", "Account locked after $currentCount failed attempts: $key", "CRITICAL")
        }
        return currentCount
    }

    fun isLockedOut(key: String): Boolean {
        val lockTime = lockoutTimes[key] ?: return false
        val elapsed = System.currentTimeMillis() - lockTime
        if (elapsed >= LOCKOUT_DURATION_MS) {
            // Lockout expired
            lockoutTimes.remove(key)
            attemptCounts[key] = 0
            return false
        }
        return true
    }

    fun getRemainingLockoutSeconds(key: String): Long {
        val lockTime = lockoutTimes[key] ?: return 0L
        return maxOf(0L, (LOCKOUT_DURATION_MS - (System.currentTimeMillis() - lockTime)) / 1000)
    }

    fun recordSuccess(key: String) {
        attemptCounts.remove(key)
        lockoutTimes.remove(key)
    }

    fun getAttemptCount(key: String): Int = attemptCounts[key] ?: 0
}

// ─────────────────────────────────────────────────────────
// 4. INPUT VALIDATORS (Injection & Format Protection)
// ─────────────────────────────────────────────────────────

object InputValidator {
    private val EMAIL_REGEX = Regex("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")
    private val MOBILE_REGEX = Regex("^[6-9][0-9]{9}$")
    private val PASSWORD_UPPERCASE = Regex("[A-Z]")
    private val PASSWORD_LOWERCASE = Regex("[a-z]")
    private val PASSWORD_DIGIT = Regex("[0-9]")
    private val PASSWORD_SPECIAL = Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")
    // Dangerous SQL/NoSQL injection patterns
    private val INJECTION_PATTERNS = listOf(
        Regex("(?i)(select|insert|update|delete|drop|union|exec|execute|script|javascript|vbscript)"),
        Regex("['\";`\\\\]"),
        Regex("<[^>]*>") // HTML/XSS tags
    )

    fun validateEmail(email: String): ValidationResult {
        if (email.isBlank()) return ValidationResult.Error("Email cannot be empty")
        if (!EMAIL_REGEX.matches(email.trim())) return ValidationResult.Error("Invalid email format")
        if (containsInjection(email)) return ValidationResult.Error("Invalid characters in email")
        return ValidationResult.Success
    }

    fun validatePassword(password: String): ValidationResult {
        if (password.length < 8) return ValidationResult.Error("Password must be at least 8 characters")
        if (!PASSWORD_UPPERCASE.containsMatchIn(password)) return ValidationResult.Error("Must contain at least one uppercase letter")
        if (!PASSWORD_LOWERCASE.containsMatchIn(password)) return ValidationResult.Error("Must contain at least one lowercase letter")
        if (!PASSWORD_DIGIT.containsMatchIn(password)) return ValidationResult.Error("Must contain at least one number")
        if (!PASSWORD_SPECIAL.containsMatchIn(password)) return ValidationResult.Error("Must contain at least one special character")
        return ValidationResult.Success
    }

    fun validateMobile(mobile: String): ValidationResult {
        val cleaned = mobile.trim().replace(" ", "").replace("-", "")
        if (!MOBILE_REGEX.matches(cleaned)) return ValidationResult.Error("Enter a valid 10-digit Indian mobile number starting with 6-9")
        return ValidationResult.Success
    }

    fun validateName(name: String): ValidationResult {
        if (name.isBlank()) return ValidationResult.Error("Name cannot be empty")
        if (name.length < 2) return ValidationResult.Error("Name too short")
        if (name.length > 100) return ValidationResult.Error("Name too long")
        if (containsInjection(name)) return ValidationResult.Error("Invalid characters in name")
        return ValidationResult.Success
    }

    fun sanitizeInput(input: String): String =
        input.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")

    fun containsInjection(input: String): Boolean =
        INJECTION_PATTERNS.any { it.containsMatchIn(input) }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Success
    val errorMessage: String? get() = (this as? Error)?.message
}

// ─────────────────────────────────────────────────────────
// 5. FILE SECURITY VALIDATOR
// ─────────────────────────────────────────────────────────

object FileSecurityValidator {
    private const val MAX_FILE_SIZE_BYTES = 5 * 1024 * 1024L // 5 MB

    private val ALLOWED_MIME_TYPES = setOf(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/jpg"
    )

    private val ALLOWED_EXTENSIONS = setOf("pdf", "jpg", "jpeg", "png")

    // Magic bytes for file type verification (first bytes of file)
    private val FILE_SIGNATURES = mapOf(
        "pdf" to byteArrayOf(0x25, 0x50, 0x44, 0x46),  // %PDF
        "jpg" to byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte()),  // JPEG
        "png" to byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)   // PNG
    )

    fun validateFile(
        fileName: String,
        fileSizeBytes: Long,
        mimeType: String?,
        fileBytes: ByteArray? = null
    ): ValidationResult {
        // 1. Size check
        if (fileSizeBytes > MAX_FILE_SIZE_BYTES) {
            return ValidationResult.Error("File too large. Maximum size is 5MB. Your file: ${fileSizeBytes / (1024 * 1024)}MB")
        }
        if (fileSizeBytes == 0L) {
            return ValidationResult.Error("File is empty")
        }

        // 2. Extension check
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext !in ALLOWED_EXTENSIONS) {
            return ValidationResult.Error("File type not allowed. Only PDF, JPG, PNG files are accepted.")
        }

        // 3. MIME type check
        if (mimeType != null && mimeType !in ALLOWED_MIME_TYPES) {
            return ValidationResult.Error("Invalid file format: $mimeType")
        }

        // 4. File header/signature check (magic bytes) - prevents extension spoofing
        if (fileBytes != null && fileBytes.size >= 4) {
            val isValidSignature = FILE_SIGNATURES.any { (_, sig) ->
                fileBytes.take(sig.size).toByteArray().contentEquals(sig)
            }
            if (!isValidSignature) {
                SecurityStore.logAction(
                    SessionManager.currentEmail,
                    "Suspicious file upload attempt: $fileName (signature mismatch)",
                    "CRITICAL"
                )
                return ValidationResult.Error("File content does not match its extension. Upload rejected.")
            }
        }

        SecurityStore.logAction(SessionManager.currentEmail, "File validated: $fileName (${fileSizeBytes / 1024}KB)", "INFO")
        return ValidationResult.Success
    }
}

// ─────────────────────────────────────────────────────────
// 6. AUDIT LOG SYSTEM (Expanded & Categorized)
// ─────────────────────────────────────────────────────────

data class AuditLogEntry(
    val timestamp: String,
    val actor: String,
    val action: String,
    val severity: String = "INFO", // INFO, WARNING, CRITICAL
    val category: String = "GENERAL", // AUTH, ACCESS, DATA, PAYMENT, SYSTEM, SECURITY
    val ipAddress: String = "local"
)

object SecurityStore {
    val auditLogs = mutableStateListOf<AuditLogEntry>()
    private const val MAX_LOG_ENTRIES = 500

    fun logAction(
        actor: String,
        action: String,
        severity: String = "INFO",
        category: String = "GENERAL"
    ) {
        val time = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm:ss a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val entry = AuditLogEntry(
            timestamp = time,
            actor = actor.ifBlank { "ANONYMOUS" },
            action = action,
            severity = severity,
            category = category
        )

        // Prevent log injection - sanitize the action text
        val safeEntry = entry.copy(action = InputValidator.sanitizeInput(action))
        auditLogs.add(0, safeEntry)

        // Keep log size bounded
        if (auditLogs.size > MAX_LOG_ENTRIES) {
            auditLogs.removeAt(auditLogs.lastIndex)
        }
    }

    fun logAuth(actor: String, action: String, success: Boolean) {
        logAction(actor, action, if (success) "INFO" else "WARNING", "AUTH")
    }

    fun logDataAccess(actor: String, resource: String) {
        logAction(actor, "Accessed: $resource", "INFO", "ACCESS")
    }

    fun logPayment(actor: String, details: String) {
        logAction(actor, details, "INFO", "PAYMENT")
    }

    // Req 63: Application Consent
    data class ConsentRecord(
        val timestamp: String,
        val termsVersion: String,
        val userId: String,
        val applicationId: String
    )

    private val consents = mutableStateListOf<ConsentRecord>()

    fun logConsent(userId: String, applicationId: String, termsVersion: String = "v1.0") {
        val time = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm:ss a",
            java.util.Locale.getDefault()
        ).format(java.util.Date())
        consents.add(ConsentRecord(time, termsVersion, userId, applicationId))
        logAction(userId, "Gave explicit consent for application $applicationId", "INFO", "LEGAL")
    }

    fun getConsentForApp(applicationId: String): ConsentRecord? {
        return consents.find { it.applicationId == applicationId }
    }

    fun logSecurityEvent(actor: String, event: String) {
        logAction(actor, event, "CRITICAL", "SECURITY")
    }

    fun getLogsByCategory(category: String): List<AuditLogEntry> =
        auditLogs.filter { it.category == category }

    fun getLogsBySeverity(severity: String): List<AuditLogEntry> =
        auditLogs.filter { it.severity == severity }

    fun getCriticalLogs(): List<AuditLogEntry> =
        auditLogs.filter { it.severity == "CRITICAL" }

    fun clearLogs() {
        logAction("ADMIN", "Audit logs cleared", "WARNING", "SYSTEM")
        // Note: in production, archive logs before clearing
        auditLogs.clear()
    }
}

// ─────────────────────────────────────────────────────────
// 7. SECURE ROUTE GUARD (RBAC Navigation Protection)
// ─────────────────────────────────────────────────────────

object RouteGuard {
    /**
     * Check if the current user has permission to access a screen.
     * Logs unauthorized access attempts.
     */
    fun canAccess(screen: Screen, role: UserRole): Boolean {
        val allowed = when (screen) {
            // Student screens
            is Screen.Home -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.StudentDashboard -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.JobDiscovery -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.Profile -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.MasterProfile -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.DocumentVault -> role in listOf(UserRole.STUDENT, UserRole.ADMIN, UserRole.EMPLOYEE)
            is Screen.EducationalDetails -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.SkillsExperience -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.PaymentHistory -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.ApplicationDashboard -> role in listOf(UserRole.ADMIN, UserRole.EMPLOYEE)
            is Screen.PrivacySecurity -> role != UserRole.GUEST
            is Screen.HelpSupport -> role != UserRole.GUEST
            is Screen.SupportHub -> role != UserRole.GUEST
            is Screen.SubmitTicket -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.TicketHistory -> role != UserRole.GUEST
            is Screen.TicketDetail -> role != UserRole.GUEST
            is Screen.NotificationCenter -> role != UserRole.GUEST
            is Screen.ApplicationHistory -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.ApplicationSummary -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.Checkout -> role in listOf(UserRole.STUDENT, UserRole.ADMIN)
            is Screen.TrackingApplication -> role != UserRole.GUEST
            is Screen.SupportChat -> role != UserRole.GUEST

            // Employee screens
            is Screen.EmployeeDashboard -> role in listOf(UserRole.EMPLOYEE, UserRole.ADMIN)
            is Screen.ProcessingApplication -> role in listOf(UserRole.EMPLOYEE, UserRole.ADMIN)

            // Admin-only screens
            is Screen.AdminDashboard -> role == UserRole.ADMIN
            is Screen.PostJob -> role == UserRole.ADMIN
            is Screen.ManageStudents -> role == UserRole.ADMIN
            is Screen.ManageEmployees -> role == UserRole.ADMIN
            is Screen.ManageServiceCharges -> role == UserRole.ADMIN
            is Screen.SystemAuditLogs -> role == UserRole.ADMIN
            is Screen.SupportRequests -> role in listOf(UserRole.ADMIN, UserRole.EMPLOYEE)
            is Screen.ManageNotifications -> role == UserRole.ADMIN
            is Screen.SystemReports -> role == UserRole.ADMIN
            is Screen.SystemSettings -> role == UserRole.ADMIN
            is Screen.AdminSupportDashboard -> role in listOf(UserRole.ADMIN, UserRole.EMPLOYEE)
            is Screen.AdminTicketDetail -> role in listOf(UserRole.ADMIN, UserRole.EMPLOYEE)
            is Screen.AdminNotificationTemplates -> role == UserRole.ADMIN
            is Screen.AdminManageJobs -> role == UserRole.ADMIN
            is Screen.AdminRefunds -> role == UserRole.ADMIN
            is Screen.AdminAuditLogs -> role == UserRole.ADMIN

            // Public screens
            is Screen.Login, is Screen.Register, is Screen.OtpVerification -> true
        }

        if (!allowed) {
            SecurityStore.logSecurityEvent(
                SessionManager.currentEmail.ifBlank { "ANONYMOUS" },
                "UNAUTHORIZED ACCESS ATTEMPT: ${screen::class.simpleName} by role ${role.name}"
            )
        }
        return allowed
    }
}
