package com.example.myapplication

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════════════════════
// REPOSITORIES — Phase 2
//
// Repositories abstract the data source from the ViewModel.
// They talk to Room DAOs (and will talk to Retrofit APIs when the
// backend is available). ViewModels never touch DAOs directly.
// ═══════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────
// Job Repository
// ─────────────────────────────────────────────────────────────────────
class JobRepository(private val dao: JobDao) {

    fun getAllJobs(): Flow<List<JobEntity>> = dao.getAllFlow()
    fun getActiveJobs(): Flow<List<JobEntity>> = dao.getActiveFlow()
    fun searchJobs(query: String): Flow<List<JobEntity>> =
        if (query.isBlank()) dao.getActiveFlow() else dao.searchActive(query)
    fun getClosingSoon(): Flow<List<JobEntity>> = dao.getClosingSoon()

    suspend fun getById(jobId: String): JobEntity? = dao.getById(jobId)

    suspend fun saveJob(job: JobEntity) = dao.upsert(job)

    suspend fun publishJob(jobId: String) {
        dao.updateStatus(jobId, "ACTIVE")
    }

    suspend fun unpublishJob(jobId: String) = dao.updateStatus(jobId, "DRAFT")
    suspend fun closeJob(jobId: String) = dao.updateStatus(jobId, "CLOSED")
    suspend fun archiveJob(jobId: String) = dao.updateStatus(jobId, "ARCHIVED")
    suspend fun deleteJob(job: JobEntity) = dao.delete(job)

    // Analytics
    suspend fun countActive(): Int = dao.countActive()
    suspend fun countExpired(): Int = dao.countExpired()
    suspend fun countClosed(): Int = dao.countClosed()

    // Generate sequential Job ID
    fun generateJobId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 10000).toString().padStart(4, '0')
        return "JOB-$year-$seq"
    }
}

// ─────────────────────────────────────────────────────────────────────
// Application Repository
// ─────────────────────────────────────────────────────────────────────
class ApplicationRepository(
    private val appDao: ApplicationDao,
    private val timelineDao: TimelineDao,
    private val notifDao: NotificationDao
) {
    fun getAllApplications(): Flow<List<ApplicationEntity>> = appDao.getAllFlow()
    fun getForStudent(studentId: String): Flow<List<ApplicationEntity>> = appDao.getForStudent(studentId)
    fun getForEmployee(employeeId: String): Flow<List<ApplicationEntity>> = appDao.getForEmployee(employeeId)
    fun getTimeline(appId: String): Flow<List<TimelineEventEntity>> = timelineDao.getForApp(appId)

    suspend fun getById(appId: String): ApplicationEntity? = appDao.getById(appId)

    /** Req 10: Check for duplicate before creating */
    suspend fun isDuplicate(studentId: String, jobId: String): Boolean =
        appDao.findDuplicate(studentId, jobId) != null

    suspend fun createApplication(app: ApplicationEntity) {
        appDao.upsert(app)
        addTimelineEvent(app.appId, "PAYMENT_PENDING", "Application Created & Queued")
    }

    suspend fun updateStatus(appId: String, status: String, note: String = "") {
        appDao.updateStatus(appId, status)
        addTimelineEvent(appId, status, statusTitle(status), note)
    }

    suspend fun assignToEmployee(
        appId: String, employeeId: String, employeeName: String,
        priority: String, studentId: String, jobTitle: String
    ) {
        appDao.assignEmployee(appId, employeeId, employeeName, priority)
        addTimelineEvent(appId, "ASSIGNED", "Assigned to $employeeName")
        // Notify student
        notifDao.insert(NotificationEntity(
            notificationId = "NOTIF-${System.currentTimeMillis()}",
            userId = studentId,
            title = "Application Assigned",
            body = "Your $jobTitle application has been assigned to $employeeName and will be processed soon.",
            type = "APPLICATION_ASSIGNED",
            category = "APPLICATIONS",
            relatedEntityId = appId
        ))
    }

    /** Req 13: Bulk assignment with workload balancing */
    suspend fun bulkAssign(appIds: List<String>, employees: List<EmployeeEntity>): Map<String, Int> {
        if (employees.isEmpty()) return emptyMap()
        val workloadMap = employees.associate { emp ->
            emp.employeeId to appDao.getWorkloadForEmployee(emp.employeeId)
        }.toMutableMap()

        val assigned = mutableMapOf<String, Int>() // employeeId → count assigned this batch

        for (appId in appIds) {
            val app = appDao.getById(appId) ?: continue
            // Find the employee with the lowest current workload who is below max
            val bestEmployee = employees
                .filter { (workloadMap[it.employeeId] ?: 0) < it.maxWorkload }
                .minByOrNull { workloadMap[it.employeeId] ?: 0 }
                ?: continue

            val priority = calculatePriorityLabel(app.deadline)
            appDao.assignEmployee(appId, bestEmployee.employeeId, bestEmployee.name, priority)
            addTimelineEvent(appId, "ASSIGNED", "Bulk assigned to ${bestEmployee.name}")
            workloadMap[bestEmployee.employeeId] = (workloadMap[bestEmployee.employeeId] ?: 0) + 1
            assigned[bestEmployee.employeeId] = (assigned[bestEmployee.employeeId] ?: 0) + 1
        }
        return assigned
    }

    private fun calculatePriorityLabel(deadline: String): String {
        return try {
            val fmt = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val dl = fmt.parse(deadline) ?: return "Normal"
            val days = (dl.time - Date().time) / 86400000L
            when {
                days < 1 -> "Critical"
                days < 3 -> "High"
                days <= 7 -> "Medium"
                else -> "Normal"
            }
        } catch (e: Exception) { "Normal" }
    }

    suspend fun addTimelineEvent(appId: String, status: String, title: String, note: String = "") {
        timelineDao.insert(TimelineEventEntity(appId = appId, status = status, title = title, note = note))
    }

    // Analytics (real DB counts — Req 25)
    suspend fun countTotal(): Int = appDao.countTotal()
    suspend fun countByStatus(status: String): Int = appDao.countByStatus(status)
    suspend fun countTodayNew(): Int = appDao.countNewSince(startOfToday())
    suspend fun countUnassigned(): Int = appDao.countUnassigned()
    suspend fun countCritical(): Int = appDao.countCritical()
    suspend fun getWorkload(employeeId: String): Int = appDao.getWorkloadForEmployee(employeeId)

    suspend fun getWaitingForPortal(): List<ApplicationEntity> = appDao.getWaitingForPortal()

    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun statusTitle(status: String): String = when (status) {
        "PAYMENT_PENDING" -> "Awaiting Payment"
        "PAYMENT_SUCCESSFUL" -> "Payment Confirmed ✓"
        "ASSIGNED" -> "Assigned to Processing Team"
        "IN_PROGRESS" -> "Application Processing Started"
        "WAITING_FOR_STUDENT" -> "Action Required from Student"
        "WAITING_FOR_PORTAL" -> "Official Portal Temporarily Unavailable"
        "RETRY_REQUIRED" -> "Retrying Submission"
        "SUBMITTED" -> "Application Submitted Successfully 🎉"
        "SUBMISSION_FAILED" -> "Submission Failed — Team Retrying"
        "REFUND_INITIATED" -> "Refund Initiated"
        "REFUNDED" -> "Refund Completed ✓"
        "CANCELLED" -> "Application Cancelled"
        else -> status
    }

    // Generate APP-2026-XXXXXX style IDs (Req 9)
    fun generateAppId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 1000000).toString().padStart(6, '0')
        return "APP-$year-$seq"
    }
}

// ─────────────────────────────────────────────────────────────────────
// Student Repository
// ─────────────────────────────────────────────────────────────────────
class StudentRepository(
    private val dao: StudentDao,
    private val activityDao: ActivityHistoryDao
) {
    fun getAllStudents(): Flow<List<StudentEntity>> = dao.getAllFlow()

    suspend fun getByEmail(email: String): StudentEntity? = dao.getByEmail(email)
    suspend fun getById(id: String): StudentEntity? = dao.getById(id)

    suspend fun saveStudent(student: StudentEntity) {
        dao.upsert(student)
        activityDao.insert(ActivityHistoryEntity(
            userId = student.studentId,
            action = "Profile Updated",
            detail = "Profile saved/updated"
        ))
    }

    suspend fun countTotal(): Int = dao.count()
    suspend fun countNew(): Int = dao.countNewSince(startOfToday())

    suspend fun findEligible(eligibleBranches: String, minCgpa: Float, graduationYear: String) =
        dao.findEligible(eligibleBranches, minCgpa, graduationYear)

    fun getActivityHistory(userId: String): Flow<List<ActivityHistoryEntity>> =
        activityDao.getForUser(userId)

    suspend fun logActivity(userId: String, action: String, detail: String = "") {
        activityDao.insert(ActivityHistoryEntity(userId = userId, action = action, detail = detail))
    }

    // Compute profile completion percentage
    fun computeCompletion(student: StudentEntity): Int {
        val fields = listOf(
            student.fullName, student.mobile, student.dateOfBirth, student.gender,
            student.address, student.college, student.course, student.branch,
            student.graduationYear, student.cgpa, student.skills,
            student.profilePhotoUrl, student.resumeUrl, student.signatureUrl
        )
        val filled = fields.count { it.isNotBlank() }
        return ((filled.toFloat() / fields.size) * 100).toInt()
    }

    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    // Generate STU-YEAR-XXXXXX
    fun generateStudentId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 1000000).toString().padStart(6, '0')
        return "STU-$year-$seq"
    }
}

// ─────────────────────────────────────────────────────────────────────
// Notification Repository
// ─────────────────────────────────────────────────────────────────────
class NotificationRepository(private val dao: NotificationDao) {

    fun getForUser(userId: String): Flow<List<NotificationEntity>> = dao.getForUser(userId)
    fun getForUserByCategory(userId: String, category: String): Flow<List<NotificationEntity>> =
        dao.getForUserByCategory(userId, category)
    fun getUnreadCount(userId: String): Flow<Int> = dao.getUnreadCount(userId)

    suspend fun send(
        userId: String, title: String, body: String,
        type: String, category: String = "SYSTEM", relatedId: String = ""
    ) {
        dao.insert(NotificationEntity(
            notificationId = "NOTIF-${System.currentTimeMillis()}-${(1000..9999).random()}",
            userId = userId,
            title = title,
            body = body,
            type = type,
            category = category,
            relatedEntityId = relatedId
        ))
    }

    /** Req 6: Send targeted notifications to eligible students */
    suspend fun sendJobNotificationToEligible(
        job: JobEntity,
        eligibleStudents: List<StudentEntity>
    ) {
        for (student in eligibleStudents) {
            dao.insert(NotificationEntity(
                notificationId = "NOTIF-JOB-${job.jobId}-${student.studentId}",
                userId = student.studentId,
                title = "New Job: ${job.title}",
                body = "${job.organization} is hiring! Apply before ${job.applicationDeadline}. Fee: ${job.totalFee}",
                type = "NEW_JOB_ALERT",
                category = "JOBS",
                relatedEntityId = job.jobId,
                deepLink = "jobs/${job.jobId}"
            ))
        }
    }

    suspend fun markRead(id: String) = dao.markRead(id)
    suspend fun markAllRead(userId: String) = dao.markAllRead(userId)
}

// ─────────────────────────────────────────────────────────────────────
// Transaction Repository
// ─────────────────────────────────────────────────────────────────────
class TransactionRepository(private val dao: TransactionDao) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> = dao.getAllFlow()
    fun getForStudent(studentId: String): Flow<List<TransactionEntity>> = dao.getForStudent(studentId)
    suspend fun getForApplication(appId: String): TransactionEntity? = dao.getForApplication(appId)
    suspend fun record(transaction: TransactionEntity) = dao.upsert(transaction)
    suspend fun totalRevenue(): Double = dao.totalRevenue() ?: 0.0
    suspend fun revenueToday(): Double = dao.revenueForPeriod(startOfToday()) ?: 0.0
    suspend fun refund(paymentId: String) = dao.updateStatus(paymentId, "REFUNDED")

    fun generatePaymentId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 1000000).toString().padStart(6, '0')
        return "PAY-$year-$seq"
    }

    private fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

// ─────────────────────────────────────────────────────────────────────
// Support Ticket Repository
// ─────────────────────────────────────────────────────────────────────
class SupportTicketRepository(
    private val dao: SupportTicketDao,
    private val messageDao: TicketMessageDao
) {
    fun getAll(): Flow<List<SupportTicketEntity>> = dao.getAllFlow()
    fun getForStudent(studentId: String): Flow<List<SupportTicketEntity>> = dao.getForStudent(studentId)
    fun getMessages(ticketId: String): Flow<List<TicketMessageEntity>> = messageDao.getForTicket(ticketId)
    suspend fun getById(id: String): SupportTicketEntity? = dao.getById(id)
    suspend fun create(ticket: SupportTicketEntity) = dao.upsert(ticket)
    suspend fun update(id: String, status: String, assignedTo: String, response: String) =
        dao.updateTicket(id, status, assignedTo, response)
    suspend fun addMessage(message: TicketMessageEntity) = messageDao.insert(message)
    suspend fun countOpen(): Int = dao.countOpen()

    fun generateTicketId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 100000).toString().padStart(5, '0')
        return "TKT-$year-$seq"
    }
}

// ─────────────────────────────────────────────────────────────────────
// Document Repository
// ─────────────────────────────────────────────────────────────────────
class DocumentRepository(private val dao: DocumentDao) {
    fun getForOwner(ownerId: String): Flow<List<DocumentEntity>> = dao.getForOwner(ownerId)
    suspend fun getForApplication(appId: String): List<DocumentEntity> = dao.getForApplication(appId)
    suspend fun save(doc: DocumentEntity) = dao.upsert(doc)
    suspend fun softDelete(id: String) = dao.softDelete(id)
    suspend fun verify(id: String, status: String, remarks: String, by: String) =
        dao.updateVerification(id, status, remarks, by)

    fun generateDocumentId(): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val seq = (System.currentTimeMillis() % 100000).toString().padStart(5, '0')
        return "DOC-$year-$seq"
    }
}

// ─────────────────────────────────────────────────────────────────────
// Employee Repository
// ─────────────────────────────────────────────────────────────────────
class EmployeeRepository(
    private val dao: EmployeeDao,
    private val perfDao: EmployeePerformanceDao
) {
    fun getAll(): Flow<List<EmployeeEntity>> = dao.getAllFlow()
    suspend fun getByEmail(email: String): EmployeeEntity? = dao.getByEmail(email)
    suspend fun getById(id: String): EmployeeEntity? = dao.getById(id)
    suspend fun save(employee: EmployeeEntity) = dao.upsert(employee)
    suspend fun count(): Int = dao.count()
    suspend fun getPerformance(employeeId: String): EmployeePerformanceEntity? = perfDao.getForEmployee(employeeId)
    suspend fun updatePerformance(perf: EmployeePerformanceEntity) = perfDao.upsert(perf)
}

// ─────────────────────────────────────────────────────────────────────
// Audit Log Repository
// ─────────────────────────────────────────────────────────────────────
class AuditLogRepository(private val dao: AuditLogDao) {
    fun getRecent(): Flow<List<AuditLogEntity>> = dao.getRecentFlow()
    suspend fun log(actor: String, action: String, severity: String = "INFO", category: String = "SYSTEM") {
        dao.insert(AuditLogEntity(actor = actor, action = action, severity = severity, category = category))
    }
    suspend fun purgeOldLogs() {
        // Req 64: Purge logs older than 1 year
        val oneYearAgo = System.currentTimeMillis() - 365L * 24 * 3600 * 1000
        dao.deleteOlderThan(oneYearAgo)
    }
}
