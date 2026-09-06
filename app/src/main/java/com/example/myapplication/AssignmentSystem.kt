package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────────────────────
// Standardized Status Enum (15 statuses)
// ─────────────────────────────────────────────────────────────
enum class AppStatus(val code: String) {
    PAYMENT_PENDING("PAYMENT_PENDING"),
    PAYMENT_SUCCESSFUL("PAYMENT_SUCCESSFUL"),
    PENDING_PROCESSING("PENDING_PROCESSING"),
    ASSIGNED("ASSIGNED"),
    IN_PROGRESS("IN_PROGRESS"),
    WAITING_FOR_STUDENT("WAITING_FOR_STUDENT"),
    WAITING_FOR_PORTAL("WAITING_FOR_PORTAL"),
    DOCUMENTS_REQUIRED("DOCUMENTS_REQUIRED"),
    ISSUE_FOUND("ISSUE_FOUND"),
    RETRY_REQUIRED("RETRY_REQUIRED"),
    SUBMITTED("SUBMITTED"),
    SUBMISSION_FAILED("SUBMISSION_FAILED"),
    REFUND_INITIATED("REFUND_INITIATED"),
    REFUNDED("REFUNDED"),
    CANCELLED("CANCELLED");

    companion object {
        fun fromCode(code: String): AppStatus =
            values().find { it.code == code } ?: PENDING_PROCESSING
    }
}

// ─────────────────────────────────────────────────────────────
// Human-Readable Status Messages for the Student
// ─────────────────────────────────────────────────────────────
object StatusEngine {

    fun getStudentLabel(status: String): String = when (AppStatus.fromCode(status)) {
        AppStatus.PAYMENT_PENDING      -> "Waiting for Payment"
        AppStatus.PAYMENT_SUCCESSFUL   -> "Payment Confirmed ✓"
        AppStatus.PENDING_PROCESSING   -> "In Queue — Will be processed soon"
        AppStatus.ASSIGNED             -> "Assigned to Our Processing Team"
        AppStatus.IN_PROGRESS          -> "Our Team is Filling Your Form"
        AppStatus.WAITING_FOR_STUDENT  -> "Action Required — We need your OTP / verification"
        AppStatus.WAITING_FOR_PORTAL   -> "Official website is temporarily unavailable. Our team will retry your application."
        AppStatus.DOCUMENTS_REQUIRED   -> "Some documents are missing — please upload them"
        AppStatus.ISSUE_FOUND          -> "An issue was found — Our team is working to resolve it"
        AppStatus.RETRY_REQUIRED       -> "Retrying your application — please wait"
        AppStatus.SUBMITTED            -> "Application Submitted Successfully 🎉"
        AppStatus.SUBMISSION_FAILED    -> "Submission failed — Our team will retry or contact you"
        AppStatus.REFUND_INITIATED     -> "Refund Initiated — Will reach you in 5–7 business days"
        AppStatus.REFUNDED             -> "Refund Completed ✓"
        AppStatus.CANCELLED            -> "Application Cancelled"
    }

    fun getStatusColor(status: String): Color = when (AppStatus.fromCode(status)) {
        AppStatus.PAYMENT_PENDING      -> Color(0xFFF57F17)
        AppStatus.PAYMENT_SUCCESSFUL   -> Color(0xFF2E7D32)
        AppStatus.PENDING_PROCESSING   -> Color(0xFF1565C0)
        AppStatus.ASSIGNED             -> Color(0xFF6A1B9A)
        AppStatus.IN_PROGRESS          -> Color(0xFF0277BD)
        AppStatus.WAITING_FOR_STUDENT  -> Color(0xFF8E24AA)
        AppStatus.WAITING_FOR_PORTAL   -> Color(0xFFE65100)
        AppStatus.DOCUMENTS_REQUIRED   -> Color(0xFFE65100)
        AppStatus.ISSUE_FOUND          -> Color(0xFFC62828)
        AppStatus.RETRY_REQUIRED       -> Color(0xFFAD1457)
        AppStatus.SUBMITTED            -> Color(0xFF1B5E20)
        AppStatus.SUBMISSION_FAILED    -> Color(0xFFC62828)
        AppStatus.REFUND_INITIATED     -> Color(0xFF00695C)
        AppStatus.REFUNDED             -> Color(0xFF00695C)
        AppStatus.CANCELLED            -> Color(0xFF546E7A)
    }

    fun getTimelineLabel(status: String): String = when (AppStatus.fromCode(status)) {
        AppStatus.PAYMENT_PENDING      -> "Awaiting Payment"
        AppStatus.PAYMENT_SUCCESSFUL   -> "Payment Successful"
        AppStatus.PENDING_PROCESSING   -> "Application Created & Queued"
        AppStatus.ASSIGNED             -> "Assigned to Processing Team"
        AppStatus.IN_PROGRESS          -> "Application Processing Started"
        AppStatus.WAITING_FOR_STUDENT  -> "Waiting for Student OTP Verification"
        AppStatus.WAITING_FOR_PORTAL   -> "Portal Unavailable — Paused"
        AppStatus.DOCUMENTS_REQUIRED   -> "Documents Required from Student"
        AppStatus.ISSUE_FOUND          -> "Issue Detected"
        AppStatus.RETRY_REQUIRED       -> "Retry Initiated"
        AppStatus.SUBMITTED            -> "Application Submitted"
        AppStatus.SUBMISSION_FAILED    -> "Submission Failed"
        AppStatus.REFUND_INITIATED     -> "Refund Initiated"
        AppStatus.REFUNDED             -> "Refund Completed"
        AppStatus.CANCELLED            -> "Application Cancelled"
    }
}

// ─────────────────────────────────────────────────────────────
// Timeline Event Model
// ─────────────────────────────────────────────────────────────
data class TimelineEvent(
    val status: String,
    val label: String,
    val note: String = "",
    val timestamp: String = nowFormatted()
)

fun nowFormatted(): String {
    val fmt = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return fmt.format(Date())
}

fun todayDate(): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
fun currentTime(): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

// ─────────────────────────────────────────────────────────────
// Priority Calculation System (Req 48)
// ─────────────────────────────────────────────────────────────
enum class PriorityLevel(val label: String, val color: Color) {
    CRITICAL("Critical (< 24h)", Color(0xFFD32F2F)),
    HIGH("High (< 3 days)", Color(0xFFF57C00)),
    MEDIUM("Medium (3-7 days)", Color(0xFFFBC02D)),
    NORMAL("Normal (> 7 days)", Color(0xFF388E3C));
    
    companion object {
        fun fromLabel(label: String): PriorityLevel = 
            values().find { it.label.startsWith(label) } ?: NORMAL
    }
}

fun calculatePriority(deadlineStr: String): PriorityLevel {
    try {
        val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val deadlineDate = format.parse(deadlineStr) ?: return PriorityLevel.NORMAL
        
        val diffInMillies = deadlineDate.time - Date().time
        val diffInDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS)
        
        return when {
            diffInDays < 1 -> PriorityLevel.CRITICAL
            diffInDays < 3 -> PriorityLevel.HIGH
            diffInDays <= 7 -> PriorityLevel.MEDIUM
            else -> PriorityLevel.NORMAL
        }
    } catch (e: Exception) {
        return PriorityLevel.NORMAL
    }
}

// ─────────────────────────────────────────────────────────────
// Submission Record (Point 27 — filled after marking Submitted)
// ─────────────────────────────────────────────────────────────
data class SubmissionRecord(
    val applicationNumber: String = "",
    val registrationNumber: String = "",
    val submissionDate: String = todayDate(),
    val submissionTime: String = currentTime(),
    val portalReceiptUrl: String = "",      // path/URI
    val applicationPdfUrl: String = "",
    val paymentReceiptUrl: String = "",
    val employeeRemarks: String = ""
)

// ─────────────────────────────────────────────────────────────
// Extended ApplicationAssignment with timeline
// ─────────────────────────────────────────────────────────────
data class ApplicationAssignment(
    val appId: String,
    var assignedEmployee: String,
    var priority: String,      // High | Medium | Low
    var deadline: String,
    var studentName: String,
    var jobTitle: String,
    val organization: String = "",
    val officialFee: String = "₹0",
    val serviceFee: String = "₹0",
    val officialWebsite: String = "",
    val officialAppUrl: String = "",
    val appliedDate: String = todayDate(),
    var status: String = AppStatus.PENDING_PROCESSING.code,
    var applicationNumber: String = "",
    var remarks: String = "",
    var hasReceipt: Boolean = false,
    var submissionRecord: SubmissionRecord? = null,
    val timeline: MutableList<TimelineEvent> = mutableListOf()
)

// ─────────────────────────────────────────────────────────────
// Centralized Assignment Store
// ─────────────────────────────────────────────────────────────
object AssignmentStore {
    private val assignments = mutableStateListOf<ApplicationAssignment>()

    init {
        assignments.add(ApplicationAssignment(
            appId = "APP-101", assignedEmployee = "Executive Amit", priority = "High",
            deadline = "15 Sep 2026", studentName = "Rahul Kumar", jobTitle = "SSC CGL 2024",
            organization = "Staff Selection Commission", officialFee = "₹100", serviceFee = "₹150",
            officialWebsite = "https://ssc.nic.in", officialAppUrl = "https://ssc.nic.in/apply",
            appliedDate = "26 Aug 2026", status = AppStatus.IN_PROGRESS.code,
            timeline = mutableListOf(
                TimelineEvent(AppStatus.PENDING_PROCESSING.code, "Application Created & Queued", "", "26 Aug, 10:15 AM"),
                TimelineEvent(AppStatus.PAYMENT_SUCCESSFUL.code, "Payment Successful", "₹150 received", "26 Aug, 10:16 AM"),
                TimelineEvent(AppStatus.ASSIGNED.code, "Assigned to Processing Team", "Executive Amit", "26 Aug, 10:18 AM"),
                TimelineEvent(AppStatus.IN_PROGRESS.code, "Application Processing Started", "", "26 Aug, 11:05 AM")
            )
        ))
        assignments.add(ApplicationAssignment(
            appId = "APP-105", assignedEmployee = "Executive Priya", priority = "High",
            deadline = "30 Aug 2026", studentName = "Priya Sharma", jobTitle = "IBPS PO XIV",
            organization = "Institute of Banking Personnel Selection", officialFee = "₹850", serviceFee = "₹200",
            officialWebsite = "https://ibps.in", officialAppUrl = "https://ibps.in/apply",
            appliedDate = "25 Aug 2026", status = AppStatus.WAITING_FOR_STUDENT.code,
            timeline = mutableListOf(
                TimelineEvent(AppStatus.PENDING_PROCESSING.code, "Application Created & Queued", "", "25 Aug, 03:10 PM"),
                TimelineEvent(AppStatus.PAYMENT_SUCCESSFUL.code, "Payment Successful", "₹200 received", "25 Aug, 03:11 PM"),
                TimelineEvent(AppStatus.ASSIGNED.code, "Assigned to Processing Team", "Executive Priya", "25 Aug, 03:15 PM"),
                TimelineEvent(AppStatus.IN_PROGRESS.code, "Application Processing Started", "", "25 Aug, 04:00 PM"),
                TimelineEvent(AppStatus.WAITING_FOR_STUDENT.code, "Waiting for Student OTP Verification", "", "25 Aug, 04:22 PM")
            )
        ))
        assignments.add(ApplicationAssignment(
            appId = "APP-109", assignedEmployee = "Executive Amit", priority = "Medium",
            deadline = "05 Oct 2026", studentName = "Amit Singh", jobTitle = "Railway Tech",
            organization = "Railway Recruitment Board", officialFee = "₹500", serviceFee = "₹150",
            officialWebsite = "https://rrbapply.gov.in", officialAppUrl = "https://rrbapply.gov.in/apply",
            appliedDate = "26 Aug 2026", status = AppStatus.ASSIGNED.code,
            timeline = mutableListOf(
                TimelineEvent(AppStatus.PENDING_PROCESSING.code, "Application Created & Queued", "", "26 Aug, 09:00 AM"),
                TimelineEvent(AppStatus.PAYMENT_SUCCESSFUL.code, "Payment Successful", "₹150 received", "26 Aug, 09:01 AM"),
                TimelineEvent(AppStatus.ASSIGNED.code, "Assigned to Processing Team", "Executive Amit", "26 Aug, 09:30 AM")
            )
        ))
        // Fully submitted demo application with complete SubmissionRecord
        assignments.add(ApplicationAssignment(
            appId = "APP-098", assignedEmployee = "Executive Priya", priority = "High",
            deadline = "20 Aug 2026", studentName = "Demo Student", jobTitle = "RRB NTPC 2026",
            organization = "Railway Recruitment Board", officialFee = "₹500", serviceFee = "₹150",
            officialWebsite = "https://rrbapply.gov.in", officialAppUrl = "https://rrbntpc.gov.in/apply",
            appliedDate = "20 Aug 2026", status = AppStatus.SUBMITTED.code,
            applicationNumber = "RRB2026-00487",
            submissionRecord = SubmissionRecord(
                applicationNumber = "RRB2026-00487",
                registrationNumber = "REG-9921-B",
                submissionDate = "20 Aug 2026", submissionTime = "11:32 AM",
                employeeRemarks = "Application submitted successfully. Form preview verified before final submit."
            ),
            timeline = mutableListOf(
                TimelineEvent(AppStatus.PENDING_PROCESSING.code, "Application Created & Queued", "", "20 Aug, 09:00 AM"),
                TimelineEvent(AppStatus.PAYMENT_SUCCESSFUL.code, "Payment Successful", "₹650 received", "20 Aug, 09:05 AM"),
                TimelineEvent(AppStatus.ASSIGNED.code, "Assigned to Processing Team", "Executive Priya", "20 Aug, 09:15 AM"),
                TimelineEvent(AppStatus.IN_PROGRESS.code, "Application Processing Started", "", "20 Aug, 11:00 AM"),
                TimelineEvent(AppStatus.SUBMITTED.code, "Application Submitted", "App No: RRB2026-00487", "20 Aug, 11:32 AM"),
                TimelineEvent(AppStatus.SUBMITTED.code, "Application Receipt Uploaded", "", "20 Aug, 11:34 AM")
            )
        ))
    }


    fun getAssignments() = assignments

    fun assignApplication(
        appId: String, 
        employee: String = "Unassigned", 
        priority: String = "Normal", 
        deadline: String = "Unknown",
        studentName: String = "Student",
        jobTitle: String = "Job"
    ) {
        // Req 48: Override priority with auto-calculated value from deadline
        val autoPriority = when (calculatePriority(deadline)) {
            PriorityLevel.CRITICAL -> "Critical"
            PriorityLevel.HIGH     -> "High"
            PriorityLevel.MEDIUM   -> "Medium"
            PriorityLevel.NORMAL   -> "Normal"
        }
        val effectivePriority = autoPriority

        val existing = assignments.find { it.appId == appId }
        if (existing != null) {
            existing.assignedEmployee = employee
            existing.priority = effectivePriority
            existing.deadline = deadline
            existing.studentName = studentName
            existing.jobTitle = jobTitle
            addTimelineEvent(appId, AppStatus.ASSIGNED.code, "Reassigned to $employee")
        } else {
            val a = ApplicationAssignment(appId, employee, effectivePriority, deadline, studentName, jobTitle,
                status = AppStatus.ASSIGNED.code)
            a.timeline.add(TimelineEvent(AppStatus.ASSIGNED.code, "Assigned to Processing Team", employee))
            assignments.add(a)
        }
    }

    /** Req 48 — Called by BackgroundWorkerManager to refresh all priorities */
    fun updateAutoPriorities() {
        for (app in assignments) {
            val newLabel = when (calculatePriority(app.deadline)) {
                PriorityLevel.CRITICAL -> "Critical"
                PriorityLevel.HIGH     -> "High"
                PriorityLevel.MEDIUM   -> "Medium"
                PriorityLevel.NORMAL   -> "Normal"
            }
            app.priority = newLabel
        }
    }

    fun getAssignmentsForEmployee(employeeName: String) =
        assignments.filter { it.assignedEmployee == employeeName }

    fun updateAssignment(
        appId: String,
        status: String? = null,
        appNo: String? = null,
        remarks: String? = null,
        hasReceipt: Boolean? = null,
        note: String = "",
        submissionRecord: SubmissionRecord? = null
    ) {
        assignments.find { it.appId == appId }?.let { a ->
            if (status != null) {
                a.status = status
                val label = StatusEngine.getTimelineLabel(status)
                a.timeline.add(TimelineEvent(status, label, note))
            }
            if (appNo != null) a.applicationNumber = appNo
            if (remarks != null) a.remarks = remarks
            if (hasReceipt != null) {
                a.hasReceipt = hasReceipt
                if (hasReceipt) {
                    a.timeline.add(TimelineEvent(a.status, "Application Receipt Uploaded", ""))
                }
            }
            if (submissionRecord != null) {
                a.submissionRecord = submissionRecord
                if (submissionRecord.applicationNumber.isNotBlank()) a.applicationNumber = submissionRecord.applicationNumber
                if (submissionRecord.employeeRemarks.isNotBlank()) a.remarks = submissionRecord.employeeRemarks
                a.timeline.add(TimelineEvent(AppStatus.SUBMITTED.code, "Submission Details Recorded by Executive", submissionRecord.applicationNumber))
                // Fire notification
                NotificationStore.addNotification(
                    type = NotificationType.APPLICATION_SUBMITTED,
                    title = "Application Submitted!",
                    body = "Your ${a.jobTitle} application has been submitted. App No: ${submissionRecord.applicationNumber}",
                    appId = appId
                )
            }
        }
    }

    fun addTimelineEvent(appId: String, status: String, note: String = "") {
        assignments.find { it.appId == appId }?.timeline?.add(
            TimelineEvent(status, StatusEngine.getTimelineLabel(status), note)
        )
    }

    fun getTimelineForApp(appId: String): List<TimelineEvent> =
        assignments.find { it.appId == appId }?.timeline ?: emptyList()

    fun getStatsForEmployee(employeeName: String): EmployeeStats {
        return overrideStats[employeeName] ?: EmployeeStats(
            total = 47, pending = 18, inProgress = 12, waiting = 5, submitted = 10, failed = 2
        )
    }

    private val overrideStats = mutableStateMapOf<String, EmployeeStats>()

    fun updateEmployeeStats(employeeName: String, newStats: EmployeeStats) {
        overrideStats[employeeName] = newStats
    }

    fun getSnapshotForApp(appId: String): ApplicationSnapshot? = null
}

data class EmployeeStats(
    val total: Int,
    val pending: Int,
    val inProgress: Int,
    val waiting: Int,
    val submitted: Int,
    val failed: Int
)

