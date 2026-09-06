package com.example.myapplication

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════════════════════════════════════════════
// DATA ACCESS OBJECTS (DAOs)
// Each DAO provides suspend functions and Flow queries for its entity.
// Repositories call these; ViewModels observe the Flows.
// ═══════════════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────────────
// Student DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): StudentEntity?

    @Query("SELECT * FROM students WHERE studentId = :id LIMIT 1")
    suspend fun getById(id: String): StudentEntity?

    @Query("SELECT * FROM students ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM students WHERE createdAt >= :since")
    suspend fun countNewSince(since: Long): Int

    @Upsert
    suspend fun upsert(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    // Eligibility query for targeted job notifications
    @Query("""
        SELECT * FROM students
        WHERE (:branch = '' OR :branch = 'Any' OR ',' || :branch || ',' LIKE '%,' || branch || ',%')
        AND (cgpa = '' OR CAST(cgpa AS REAL) >= :minCgpa)
        AND (:graduationYear = '' OR :graduationYear = 'Any' OR ',' || :graduationYear || ',' LIKE '%,' || graduationYear || ',%')
    """)
    suspend fun findEligible(branch: String, minCgpa: Float, graduationYear: String): List<StudentEntity>

    @Query("""
        UPDATE students SET
            fullName = :fullName, mobile = :mobile, college = :college,
            branch = :branch, cgpa = :cgpa, profileCompletionPct = :pct,
            updatedAt = :now
        WHERE studentId = :id
    """)
    suspend fun updateProfile(
        id: String, fullName: String, mobile: String, college: String,
        branch: String, cgpa: String, pct: Int, now: Long = System.currentTimeMillis()
    )
}

// ─────────────────────────────────────────────────────────────────────
// Employee DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY name ASC")
    fun getAllFlow(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): EmployeeEntity?

    @Query("SELECT * FROM employees WHERE employeeId = :id LIMIT 1")
    suspend fun getById(id: String): EmployeeEntity?

    @Query("SELECT COUNT(*) FROM employees")
    suspend fun count(): Int

    @Upsert
    suspend fun upsert(employee: EmployeeEntity)

    @Delete
    suspend fun delete(employee: EmployeeEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Job DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE status = 'ACTIVE' ORDER BY applicationDeadline ASC")
    fun getActiveFlow(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE jobId = :id LIMIT 1")
    suspend fun getById(id: String): JobEntity?

    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'ACTIVE'")
    suspend fun countActive(): Int

    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'EXPIRED'")
    suspend fun countExpired(): Int

    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'CLOSED'")
    suspend fun countClosed(): Int

    @Query("SELECT COUNT(*) FROM jobs WHERE createdAt >= :since")
    suspend fun countNewSince(since: Long): Int

    // Full-text search: title, org, location, skills
    @Query("""
        SELECT * FROM jobs WHERE status = 'ACTIVE' AND (
            title LIKE '%' || :query || '%'
            OR organization LIKE '%' || :query || '%'
            OR location LIKE '%' || :query || '%'
            OR requiredSkills LIKE '%' || :query || '%'
        ) ORDER BY applicationDeadline ASC
    """)
    fun searchActive(query: String): Flow<List<JobEntity>>

    // Closing soon: deadline within next 3 days
    @Query("""
        SELECT * FROM jobs WHERE status = 'ACTIVE'
        AND applicationDeadline != ''
        ORDER BY applicationDeadline ASC
        LIMIT 20
    """)
    fun getClosingSoon(): Flow<List<JobEntity>>

    @Upsert
    suspend fun upsert(job: JobEntity)

    @Query("UPDATE jobs SET status = :status, updatedAt = :now WHERE jobId = :jobId")
    suspend fun updateStatus(jobId: String, status: String, now: Long = System.currentTimeMillis())

    @Delete
    suspend fun delete(job: JobEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Application DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface ApplicationDao {
    @Query("SELECT * FROM applications ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getForStudent(studentId: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE assignedEmployeeId = :employeeId ORDER BY priority DESC, createdAt ASC")
    fun getForEmployee(employeeId: String): Flow<List<ApplicationEntity>>

    @Query("SELECT * FROM applications WHERE appId = :appId LIMIT 1")
    suspend fun getById(appId: String): ApplicationEntity?

    // Duplicate check: has student already applied for this job?
    @Query("SELECT * FROM applications WHERE studentId = :studentId AND jobId = :jobId LIMIT 1")
    suspend fun findDuplicate(studentId: String, jobId: String): ApplicationEntity?

    // Analytics queries (all return actual DB counts — no hardcoding!)
    @Query("SELECT COUNT(*) FROM applications")
    suspend fun countTotal(): Int

    @Query("SELECT COUNT(*) FROM applications WHERE status = :status")
    suspend fun countByStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM applications WHERE createdAt >= :since")
    suspend fun countNewSince(since: Long): Int

    @Query("SELECT COUNT(*) FROM applications WHERE assignedEmployeeId = ''")
    suspend fun countUnassigned(): Int

    @Query("SELECT COUNT(*) FROM applications WHERE priority = 'Critical' AND status NOT IN ('SUBMITTED','CANCELLED','REFUNDED')")
    suspend fun countCritical(): Int

    // Employee workload
    @Query("SELECT COUNT(*) FROM applications WHERE assignedEmployeeId = :employeeId AND status NOT IN ('SUBMITTED','CANCELLED','REFUNDED','REFUND_INITIATED')")
    suspend fun getWorkloadForEmployee(employeeId: String): Int

    @Upsert
    suspend fun upsert(app: ApplicationEntity)

    @Query("""
        UPDATE applications SET status = :status, updatedAt = :now
        WHERE appId = :appId
    """)
    suspend fun updateStatus(appId: String, status: String, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE applications SET
            assignedEmployeeId = :employeeId,
            assignedEmployeeName = :employeeName,
            status = 'ASSIGNED',
            priority = :priority,
            updatedAt = :now
        WHERE appId = :appId
    """)
    suspend fun assignEmployee(
        appId: String, employeeId: String, employeeName: String,
        priority: String, now: Long = System.currentTimeMillis()
    )

    @Query("SELECT * FROM applications WHERE status = 'WAITING_FOR_PORTAL'")
    suspend fun getWaitingForPortal(): List<ApplicationEntity>

    @Delete
    suspend fun delete(app: ApplicationEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Timeline DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface TimelineDao {
    @Query("SELECT * FROM application_timeline WHERE appId = :appId ORDER BY timestamp ASC")
    fun getForApp(appId: String): Flow<List<TimelineEventEntity>>

    @Query("SELECT * FROM application_timeline WHERE appId = :appId ORDER BY timestamp ASC")
    suspend fun getForAppOnce(appId: String): List<TimelineEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: TimelineEventEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Document DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE ownerId = :ownerId AND isDeleted = 0 ORDER BY uploadedAt DESC")
    fun getForOwner(ownerId: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE applicationId = :appId AND isDeleted = 0")
    suspend fun getForApplication(appId: String): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE documentId = :id LIMIT 1")
    suspend fun getById(id: String): DocumentEntity?

    @Upsert
    suspend fun upsert(doc: DocumentEntity)

    @Query("UPDATE documents SET isDeleted = 1, updatedAt = :now WHERE documentId = :id")
    suspend fun softDelete(id: String, now: Long = System.currentTimeMillis())

    @Query("UPDATE documents SET verificationStatus = :status, verificationRemarks = :remarks, verifiedBy = :by, updatedAt = :now WHERE documentId = :id")
    suspend fun updateVerification(id: String, status: String, remarks: String, by: String, now: Long = System.currentTimeMillis())
}

// ─────────────────────────────────────────────────────────────────────
// Transaction DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getForStudent(studentId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE applicationId = :appId LIMIT 1")
    suspend fun getForApplication(appId: String): TransactionEntity?

    @Query("SELECT SUM(CAST(REPLACE(REPLACE(totalAmount,'₹',''),',','') AS REAL)) FROM transactions WHERE status = 'SUCCESSFUL'")
    suspend fun totalRevenue(): Double?

    @Query("SELECT SUM(CAST(REPLACE(REPLACE(totalAmount,'₹',''),',','') AS REAL)) FROM transactions WHERE status = 'SUCCESSFUL' AND createdAt >= :since")
    suspend fun revenueForPeriod(since: Long): Double?

    @Upsert
    suspend fun upsert(transaction: TransactionEntity)

    @Query("UPDATE transactions SET status = :status WHERE paymentId = :id")
    suspend fun updateStatus(id: String, status: String)
}

// ─────────────────────────────────────────────────────────────────────
// Notification DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getForUser(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE userId = :userId AND category = :category ORDER BY createdAt DESC")
    fun getForUserByCategory(userId: String, category: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE notificationId = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllRead(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId AND createdAt < :cutoff")
    suspend fun deleteOlderThan(userId: String, cutoff: Long)
}

// ─────────────────────────────────────────────────────────────────────
// Support Ticket DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE studentId = :studentId ORDER BY createdAt DESC")
    fun getForStudent(studentId: String): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE ticketId = :id LIMIT 1")
    suspend fun getById(id: String): SupportTicketEntity?

    @Query("SELECT COUNT(*) FROM support_tickets WHERE status = 'Open'")
    suspend fun countOpen(): Int

    @Upsert
    suspend fun upsert(ticket: SupportTicketEntity)

    @Query("UPDATE support_tickets SET status = :status, assignedTo = :assignedTo, responseMessage = :response, updatedAt = :now WHERE ticketId = :id")
    suspend fun updateTicket(id: String, status: String, assignedTo: String, response: String, now: Long = System.currentTimeMillis())
}

// ─────────────────────────────────────────────────────────────────────
// Ticket Message DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface TicketMessageDao {
    @Query("SELECT * FROM ticket_messages WHERE ticketId = :ticketId ORDER BY createdAt ASC")
    fun getForTicket(ticketId: String): Flow<List<TicketMessageEntity>>

    @Insert
    suspend fun insert(message: TicketMessageEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Audit Log DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 500")
    fun getRecentFlow(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE actor = :actor ORDER BY timestamp DESC LIMIT 100")
    suspend fun getForActor(actor: String): List<AuditLogEntity>

    @Insert
    suspend fun insert(log: AuditLogEntity)

    // Data retention: delete logs older than 1 year
    @Query("DELETE FROM audit_logs WHERE timestamp < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}

// ─────────────────────────────────────────────────────────────────────
// Activity History DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface ActivityHistoryDao {
    @Query("SELECT * FROM activity_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getForUser(userId: String): Flow<List<ActivityHistoryEntity>>

    @Insert
    suspend fun insert(entry: ActivityHistoryEntity)
}

// ─────────────────────────────────────────────────────────────────────
// Employee Performance DAO
// ─────────────────────────────────────────────────────────────────────
@Dao
interface EmployeePerformanceDao {
    @Query("SELECT * FROM employee_performance WHERE employeeId = :id LIMIT 1")
    suspend fun getForEmployee(id: String): EmployeePerformanceEntity?

    @Upsert
    suspend fun upsert(perf: EmployeePerformanceEntity)
}
