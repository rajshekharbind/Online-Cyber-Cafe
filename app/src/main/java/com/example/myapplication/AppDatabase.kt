package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════
// ROOM DATABASE — AppDatabase
//
// This is the single source of truth for all persistent data.
// Replaces all mutableStateListOf() in-memory stores for data that
// needs to survive app restarts.
//
// Version history:
//   1 → Initial schema
// ═══════════════════════════════════════════════════════════════════════

@Database(
    entities = [
        StudentEntity::class,
        EmployeeEntity::class,
        JobEntity::class,
        ApplicationEntity::class,
        TimelineEventEntity::class,
        DocumentEntity::class,
        TransactionEntity::class,
        NotificationEntity::class,
        SupportTicketEntity::class,
        TicketMessageEntity::class,
        AuditLogEntity::class,
        ActivityHistoryEntity::class,
        EmployeePerformanceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun jobDao(): JobDao
    abstract fun applicationDao(): ApplicationDao
    abstract fun timelineDao(): TimelineDao
    abstract fun documentDao(): DocumentDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun supportTicketDao(): SupportTicketDao
    abstract fun ticketMessageDao(): TicketMessageDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun activityHistoryDao(): ActivityHistoryDao
    abstract fun employeePerformanceDao(): EmployeePerformanceDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "online_cyber_cafe.db"
                )
                .addCallback(SeedCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // ── Database Seed Callback ───────────────────────────────────────
    // Runs only on first creation. Seeds demo data so the app looks
    // populated from the start.
    private class SeedCallback(private val context: Context) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val database = getInstance(context)
                seedEmployees(database)
                seedJobs(database)
                seedStudents(database)
                seedApplications(database)
                seedTransactions(database)
                seedSupportTickets(database)
            }
        }

        private suspend fun seedEmployees(db: AppDatabase) {
            listOf(
                EmployeeEntity(
                    employeeId = "EMP-001",
                    name = "Executive Amit",
                    email = "employee@cybercafe.com",
                    mobile = "9876500001",
                    department = "Application Processing",
                    designation = "Senior Executive",
                    joiningDate = "01 Jan 2025",
                    specialization = "Government Jobs,Banking",
                    maxWorkload = 50
                ),
                EmployeeEntity(
                    employeeId = "EMP-002",
                    name = "Executive Priya",
                    email = "employee2@cybercafe.com",
                    mobile = "9876500002",
                    department = "Application Processing",
                    designation = "Executive",
                    joiningDate = "15 Mar 2025",
                    specialization = "Railway,SSC",
                    maxWorkload = 50
                ),
                EmployeeEntity(
                    employeeId = "EMP-003",
                    name = "Executive Rahul",
                    email = "employee3@cybercafe.com",
                    mobile = "9876500003",
                    department = "Application Processing",
                    designation = "Junior Executive",
                    joiningDate = "01 Jul 2025",
                    specialization = "Banking,PSU",
                    maxWorkload = 40
                )
            ).forEach { db.employeeDao().upsert(it) }

            listOf(
                EmployeePerformanceEntity(employeeId = "EMP-001", totalAssigned = 47, completed = 38, pending = 6, failed = 3, avgProcessingTimeMinutes = 65, successRate = 92.7f),
                EmployeePerformanceEntity(employeeId = "EMP-002", totalAssigned = 35, completed = 30, pending = 4, failed = 1, avgProcessingTimeMinutes = 58, successRate = 96.7f),
                EmployeePerformanceEntity(employeeId = "EMP-003", totalAssigned = 22, completed = 18, pending = 3, failed = 1, avgProcessingTimeMinutes = 72, successRate = 94.4f)
            ).forEach { db.employeePerformanceDao().upsert(it) }
        }

        private suspend fun seedJobs(db: AppDatabase) {
            val now = System.currentTimeMillis()
            listOf(
                JobEntity(
                    jobId = "JOB-2026-001",
                    title = "SSC CGL 2024",
                    organization = "Staff Selection Commission",
                    description = "Combined Graduate Level Examination for various Group B & C posts under Central Government.",
                    jobType = "Government",
                    location = "All India",
                    salary = "₹45,000 – ₹1,50,000",
                    experienceRequired = "Fresher",
                    requiredSkills = "General Knowledge,Reasoning,Mathematics",
                    eligibilityCriteria = "Any Graduate",
                    minCgpa = 0f,
                    eligibleBranches = "Any",
                    graduationYear = "2023,2024,2025,2026",
                    applicationStartDate = "01 Sep 2026",
                    applicationDeadline = "30 Sep 2026",
                    officialFee = "₹100",
                    serviceFee = "₹50",
                    totalFee = "₹150",
                    officialWebsiteUrl = "https://ssc.gov.in",
                    officialApplicationUrl = "https://ssc.gov.in/apply",
                    source = "SSC Official Portal",
                    status = "ACTIVE",
                    createdByAdminId = "admin@cybercafe.com",
                    createdAt = now - 5 * 86400000L,
                    publishedAt = now - 5 * 86400000L
                ),
                JobEntity(
                    jobId = "JOB-2026-002",
                    title = "IBPS PO XIV",
                    organization = "Institute of Banking Personnel Selection",
                    description = "Recruitment of Probationary Officers in Public Sector Banks.",
                    jobType = "Government",
                    location = "All India",
                    salary = "₹52,000 – ₹95,000",
                    experienceRequired = "Fresher",
                    requiredSkills = "Banking,Finance,Communication",
                    eligibilityCriteria = "Graduate with min 60%",
                    minCgpa = 6.0f,
                    eligibleBranches = "Any",
                    graduationYear = "2022,2023,2024,2025",
                    applicationStartDate = "15 Aug 2026",
                    applicationDeadline = "15 Sep 2026",
                    officialFee = "₹850",
                    serviceFee = "₹100",
                    totalFee = "₹950",
                    officialWebsiteUrl = "https://ibps.in",
                    officialApplicationUrl = "https://ibps.in/apply",
                    source = "IBPS Official",
                    status = "ACTIVE",
                    createdByAdminId = "admin@cybercafe.com",
                    createdAt = now - 10 * 86400000L,
                    publishedAt = now - 10 * 86400000L
                ),
                JobEntity(
                    jobId = "JOB-2026-003",
                    title = "RRB NTPC 2026",
                    organization = "Railway Recruitment Board",
                    description = "Non-Technical Popular Categories recruitment for Indian Railways.",
                    jobType = "Government",
                    location = "All India",
                    salary = "₹19,900 – ₹35,400",
                    experienceRequired = "Fresher",
                    requiredSkills = "General Science,Mathematics",
                    eligibilityCriteria = "12th Pass / Graduate",
                    minCgpa = 0f,
                    eligibleBranches = "Any",
                    graduationYear = "2024,2025,2026,2027",
                    applicationStartDate = "01 Oct 2026",
                    applicationDeadline = "31 Oct 2026",
                    officialFee = "₹500",
                    serviceFee = "₹50",
                    totalFee = "₹550",
                    officialWebsiteUrl = "https://rrbapply.gov.in",
                    officialApplicationUrl = "https://rrbapply.gov.in/apply",
                    source = "RRB Official Portal",
                    status = "ACTIVE",
                    createdByAdminId = "admin@cybercafe.com",
                    createdAt = now - 2 * 86400000L,
                    publishedAt = now - 2 * 86400000L
                ),
                JobEntity(
                    jobId = "JOB-2026-004",
                    title = "SBI Specialist Officers",
                    organization = "State Bank of India",
                    description = "Recruitment of Specialist Officers (IT, Law, CA) in SBI.",
                    jobType = "Government",
                    location = "All India",
                    salary = "₹63,840+",
                    experienceRequired = "1 Year",
                    requiredSkills = "Java,Python,Database,Networking",
                    eligibilityCriteria = "B.Tech/BE in CSE/IT/ECE with min 7.0 CGPA",
                    minCgpa = 7.0f,
                    eligibleBranches = "CSE,IT,ECE",
                    graduationYear = "2022,2023,2024,2025",
                    applicationStartDate = "10 Sep 2026",
                    applicationDeadline = "10 Oct 2026",
                    officialFee = "₹750",
                    serviceFee = "₹100",
                    totalFee = "₹850",
                    officialWebsiteUrl = "https://sbi.co.in",
                    officialApplicationUrl = "https://sbi.co.in/careers",
                    source = "SBI Careers Portal",
                    status = "ACTIVE",
                    createdByAdminId = "admin@cybercafe.com",
                    createdAt = now - 3 * 86400000L,
                    publishedAt = now - 3 * 86400000L
                ),
                JobEntity(
                    jobId = "JOB-TEST-SUBMITTED",
                    title = "Test Job (Submitted Status)",
                    organization = "Test Org",
                    description = "This job is set to SUBMITTED status for testing.",
                    jobType = "Private",
                    location = "Remote",
                    applicationDeadline = "31 Dec 2026",
                    status = "SUBMITTED"
                )
            ).forEach { db.jobDao().upsert(it) }
        }

        private suspend fun seedStudents(db: AppDatabase) {
            listOf(
                StudentEntity(
                    studentId = "STU-2026-001",
                    email = "student@test.com",
                    fullName = "Rahul Kumar",
                    mobile = "9876543210",
                    dateOfBirth = "15 Jul 2003",
                    gender = "Male",
                    college = "NIT Patna",
                    course = "B.Tech",
                    branch = "CSE",
                    graduationYear = "2026",
                    cgpa = "8.2",
                    skills = "Java,Android,Kotlin,SQL",
                    profileCompletionPct = 85
                ),
                StudentEntity(
                    studentId = "STU-2026-002",
                    email = "priya@test.com",
                    fullName = "Priya Sharma",
                    mobile = "9123456789",
                    dateOfBirth = "22 Mar 2002",
                    gender = "Female",
                    college = "BHU Varanasi",
                    course = "B.Com",
                    branch = "Commerce",
                    graduationYear = "2024",
                    cgpa = "7.5",
                    skills = "Accounting,Tally,MS Excel",
                    profileCompletionPct = 70
                )
            ).forEach { db.studentDao().upsert(it) }
        }

        private suspend fun seedApplications(db: AppDatabase) {
            val app1 = ApplicationEntity(
                appId = "APP-2026-000101",
                studentId = "STU-2026-001",
                jobId = "JOB-2026-001",
                jobTitle = "SSC CGL 2024",
                organization = "Staff Selection Commission",
                officialFee = "₹100",
                serviceFee = "₹50",
                totalAmount = "₹150",
                deadline = "30 Sep 2026",
                status = "IN_PROGRESS",
                assignedEmployeeId = "EMP-001",
                assignedEmployeeName = "Executive Amit",
                priority = "Normal",
                createdAt = System.currentTimeMillis() - 7 * 86400000L
            )
            db.applicationDao().upsert(app1)
            listOf(
                TimelineEventEntity(appId = "APP-2026-000101", status = "PAYMENT_PENDING", title = "Application Created"),
                TimelineEventEntity(appId = "APP-2026-000101", status = "PAYMENT_SUCCESSFUL", title = "Payment Successful", note = "₹150 received"),
                TimelineEventEntity(appId = "APP-2026-000101", status = "ASSIGNED", title = "Assigned to Executive Amit"),
                TimelineEventEntity(appId = "APP-2026-000101", status = "IN_PROGRESS", title = "Processing Started")
            ).forEach { db.timelineDao().insert(it) }

            val app2 = ApplicationEntity(
                appId = "APP-2026-000098",
                studentId = "STU-2026-002",
                jobId = "JOB-2026-002",
                jobTitle = "IBPS PO XIV",
                organization = "IBPS",
                officialFee = "₹850",
                serviceFee = "₹100",
                totalAmount = "₹950",
                deadline = "15 Sep 2026",
                status = "SUBMITTED",
                assignedEmployeeId = "EMP-002",
                assignedEmployeeName = "Executive Priya",
                priority = "High",
                applicationNumber = "IBPS2026-00234",
                createdAt = System.currentTimeMillis() - 14 * 86400000L
            )
            db.applicationDao().upsert(app2)
            listOf(
                TimelineEventEntity(appId = "APP-2026-000098", status = "PAYMENT_PENDING", title = "Application Created"),
                TimelineEventEntity(appId = "APP-2026-000098", status = "PAYMENT_SUCCESSFUL", title = "Payment Successful", note = "₹950 received"),
                TimelineEventEntity(appId = "APP-2026-000098", status = "ASSIGNED", title = "Assigned to Executive Priya"),
                TimelineEventEntity(appId = "APP-2026-000098", status = "IN_PROGRESS", title = "Processing Started"),
                TimelineEventEntity(appId = "APP-2026-000098", status = "SUBMITTED", title = "Submitted to Official Portal", note = "App No: IBPS2026-00234")
            ).forEach { db.timelineDao().insert(it) }

            val appTest = ApplicationEntity(
                appId = "APP-TEST-001",
                studentId = "STU-2026-001",
                jobId = "JOB-2026-003",
                jobTitle = "RRB NTPC 2026",
                organization = "Railway Recruitment Board",
                status = "SUBMITTED",
                applicationNumber = "TEST-SUB-12345",
                createdAt = System.currentTimeMillis() - 1 * 86400000L
            )
            db.applicationDao().upsert(appTest)
            db.timelineDao().insert(TimelineEventEntity(appId = "APP-TEST-001", status = "SUBMITTED", title = "Final Submission Complete"))
        }

        private suspend fun seedTransactions(db: AppDatabase) {
            listOf(
                TransactionEntity(
                    paymentId = "PAY-2026-001",
                    orderId = "ORD-2026-001",
                    studentId = "STU-2026-001",
                    applicationId = "APP-2026-000101",
                    officialFee = "₹100",
                    serviceFee = "₹50",
                    totalAmount = "₹150",
                    status = "SUCCESSFUL",
                    gatewayReference = "GREF-9001"
                ),
                TransactionEntity(
                    paymentId = "PAY-2026-002",
                    orderId = "ORD-2026-002",
                    studentId = "STU-2026-002",
                    applicationId = "APP-2026-000098",
                    officialFee = "₹850",
                    serviceFee = "₹100",
                    totalAmount = "₹950",
                    status = "SUCCESSFUL",
                    gatewayReference = "GREF-9002"
                )
            ).forEach { db.transactionDao().upsert(it) }
        }

        private suspend fun seedSupportTickets(db: AppDatabase) {
            val t1 = SupportTicketEntity(
                ticketId = "TKT-2026-001",
                studentId = "STU-2026-001",
                studentName = "Rahul Kumar",
                studentPhone = "9876543210",
                applicationId = "APP-2026-000101",
                category = "Application Issue",
                subject = "Status not updating",
                description = "My application status has not changed for 3 days.",
                status = "In Progress",
                assignedTo = "Executive Amit"
            )
            db.supportTicketDao().upsert(t1)
            listOf(
                TicketMessageEntity(ticketId = "TKT-2026-001", sender = "Student", message = "My application status has not changed for 3 days."),
                TicketMessageEntity(ticketId = "TKT-2026-001", sender = "Support", message = "We are checking this. You will hear from us within 24 hours.")
            ).forEach { db.ticketMessageDao().insert(it) }
        }
    }
}
