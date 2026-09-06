package com.example.myapplication

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════
// DATABASE ENTITIES
// Each entity maps directly to a SQL table in Room.
// All in-memory mutableStateListOf() stores are replaced by these.
// ═══════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────
// 1. Student Profile Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val studentId: String,                  // e.g. "STU-2026-001"
    val email: String,
    val fullName: String = "",
    val mobile: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val pinCode: String = "",
    val college: String = "",
    val course: String = "",
    val branch: String = "",
    val currentSemester: String = "",
    val graduationYear: String = "",
    val cgpa: String = "",
    val skills: String = "",                            // CSV: "Java,Kotlin,Android"
    val projects: String = "",                          // JSON string
    val internships: String = "",
    val certifications: String = "",
    val profilePhotoUrl: String = "",
    val resumeUrl: String = "",
    val signatureUrl: String = "",
    val profileCompletionPct: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 2. Employee Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey val employeeId: String,                 // e.g. "EMP-001"
    val name: String,
    val email: String,
    val mobile: String = "",
    val department: String = "",
    val designation: String = "",
    val joiningDate: String = "",
    val specialization: String = "",                    // CSV of skills
    val profilePhotoUrl: String = "",
    val isAvailable: Boolean = true,
    val maxWorkload: Int = 50,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 3. Job Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val jobId: String,                      // e.g. "JOB-2026-001"
    val title: String,
    val organization: String,
    val description: String = "",
    val jobType: String = "Government",                 // Government | Private | PSU
    val location: String = "",
    val workMode: String = "Office",                    // Office | Remote | Hybrid
    val salary: String = "",
    val experienceRequired: String = "Fresher",
    val requiredSkills: String = "",                    // CSV
    val eligibilityCriteria: String = "",
    val minCgpa: Float = 0f,
    val eligibleBranches: String = "",                  // CSV: "CSE,IT,ECE"
    val graduationYear: String = "",                    // CSV: "2025,2026,2027"
    val applicationStartDate: String = "",
    val applicationDeadline: String,
    val requiredDocuments: String = "",                 // CSV
    val officialFee: String = "₹0",
    val serviceFee: String = "₹50",
    val totalFee: String = "₹50",
    val officialWebsiteUrl: String = "",
    val officialApplicationUrl: String = "",
    val notificationPdfUrl: String = "",
    val source: String = "",
    val status: String = "DRAFT",                       // DRAFT|ACTIVE|CLOSED|EXPIRED|ARCHIVED
    val createdByAdminId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val publishedAt: Long? = null
)

// ─────────────────────────────────────────────────────────────────────
// 4. Application Entity (full lifecycle)
// ─────────────────────────────────────────────────────────────────────
@Entity(
    tableName = "applications",
    indices = [Index(value = ["studentId", "jobId"], unique = true)] // Req 10: DB-enforced duplicate block
)
data class ApplicationEntity(
    @PrimaryKey val appId: String,                      // APP-2026-000123
    val studentId: String,
    val jobId: String,
    val jobTitle: String,
    val organization: String = "",
    val officialFee: String = "₹0",
    val serviceFee: String = "₹50",
    val totalAmount: String = "₹50",
    val deadline: String = "",
    var status: String = "PAYMENT_PENDING",
    var assignedEmployeeId: String = "",
    var assignedEmployeeName: String = "",
    var priority: String = "Normal",                    // Critical|High|Medium|Normal
    var applicationNumber: String = "",
    var remarks: String = "",
    var hasReceipt: Boolean = false,
    val consentGiven: Boolean = false,
    val consentTimestamp: Long? = null,
    val termsVersion: String = "v1.0",
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 5. Application Timeline Event
// ─────────────────────────────────────────────────────────────────────
@Entity(
    tableName = "application_timeline",
    foreignKeys = [ForeignKey(
        entity = ApplicationEntity::class,
        parentColumns = ["appId"],
        childColumns = ["appId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("appId")]
)
data class TimelineEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val appId: String,
    val status: String,
    val title: String,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 6. Document Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey val documentId: String,                 // DOC-2026-001
    val ownerId: String,                                // studentId
    val applicationId: String? = null,
    val documentType: String,                           // RESUME|PHOTO|SIGNATURE|MARKSHEET|etc.
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val storageUrl: String,
    val verificationStatus: String = "PENDING",         // PENDING|VERIFIED|REJECTED|EXPIRED
    val verificationRemarks: String = "",
    val uploadedBy: String = "",
    val verifiedBy: String = "",
    val version: Int = 1,
    val isDeleted: Boolean = false,
    val uploadedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 7. Transaction / Payment Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val paymentId: String,                  // PAY-2026-001
    val orderId: String,
    val studentId: String,
    val applicationId: String,
    val officialFee: String,
    val serviceFee: String,
    val totalAmount: String,
    val status: String = "SUCCESSFUL",                  // PENDING|SUCCESSFUL|FAILED|REFUNDED
    val gatewayReference: String = "",
    val refundReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 8. Notification Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String,                                   // NotificationType name
    val category: String = "SYSTEM",                   // JOBS|APPLICATIONS|PAYMENTS|DOCUMENTS|SUPPORT|SYSTEM|SECURITY
    val relatedEntityId: String = "",                   // appId, jobId, ticketId etc.
    val deepLink: String = "",
    var isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 9. Support Ticket Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val ticketId: String,
    val studentId: String,
    val studentName: String,
    val studentPhone: String,
    val applicationId: String? = null,
    val category: String,
    val subject: String,
    val description: String,
    var status: String = "Open",                       // Open|In Progress|Waiting for Student|Resolved|Closed
    var assignedTo: String = "Unassigned",
    var responseMessage: String = "",
    val priority: String = "Normal",
    val createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 10. Support Ticket Message Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(
    tableName = "ticket_messages",
    foreignKeys = [ForeignKey(
        entity = SupportTicketEntity::class,
        parentColumns = ["ticketId"],
        childColumns = ["ticketId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("ticketId")]
)
data class TicketMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ticketId: String,
    val sender: String,                                 // "Student" | "Support" | "Admin"
    val message: String,
    val createdAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 11. Audit Log Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actor: String,
    val action: String,
    val severity: String = "INFO",                     // INFO|WARNING|CRITICAL
    val category: String = "SYSTEM",
    val timestamp: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 12. Employee Performance Snapshot
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "employee_performance")
data class EmployeePerformanceEntity(
    @PrimaryKey val employeeId: String,
    val totalAssigned: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val failed: Int = 0,
    val avgProcessingTimeMinutes: Int = 0,
    val successRate: Float = 0f,
    val updatedAt: Long = System.currentTimeMillis()
)

// ─────────────────────────────────────────────────────────────────────
// 13. Activity / Profile History Entity
// ─────────────────────────────────────────────────────────────────────
@Entity(tableName = "activity_history")
data class ActivityHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val action: String,                                 // e.g. "Resume Uploaded", "Profile Updated"
    val detail: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
