package com.example.myapplication

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context

// ═══════════════════════════════════════════════════════════════════════
// VIEWMODEL FACTORY
//
// Provides ViewModel instances with their Repository dependencies.
// Called from MainActivity to create ViewModels with the correct DB.
// ═══════════════════════════════════════════════════════════════════════

class AppViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val db = AppDatabase.getInstance(context)

    // Repositories
    private val jobRepo = JobRepository(db.jobDao())
    private val appRepo = ApplicationRepository(db.applicationDao(), db.timelineDao(), db.notificationDao())
    private val studentRepo = StudentRepository(db.studentDao(), db.activityHistoryDao())
    private val notifRepo = NotificationRepository(db.notificationDao())
    private val txRepo = TransactionRepository(db.transactionDao())
    private val ticketRepo = SupportTicketRepository(db.supportTicketDao(), db.ticketMessageDao())
    private val employeeRepo = EmployeeRepository(db.employeeDao(), db.employeePerformanceDao())
    private val docRepo = DocumentRepository(db.documentDao())
    private val auditRepo = AuditLogRepository(db.auditLogDao())

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JobViewModel::class.java) ->
                JobViewModel(jobRepo) as T

            modelClass.isAssignableFrom(ApplicationViewModel::class.java) ->
                ApplicationViewModel(appRepo) as T

            modelClass.isAssignableFrom(AdminViewModel::class.java) ->
                AdminViewModel(appRepo, studentRepo, employeeRepo, jobRepo, txRepo, ticketRepo) as T

            modelClass.isAssignableFrom(EmployeeViewModel::class.java) ->
                EmployeeViewModel(appRepo, employeeRepo) as T

            modelClass.isAssignableFrom(StudentProfileViewModel::class.java) ->
                StudentProfileViewModel(studentRepo, docRepo, notifRepo) as T

            modelClass.isAssignableFrom(NotificationViewModel::class.java) ->
                NotificationViewModel(notifRepo) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }

    // Expose repositories for direct use where needed
    fun getJobRepository() = jobRepo
    fun getApplicationRepository() = appRepo
    fun getStudentRepository() = studentRepo
    fun getNotificationRepository() = notifRepo
    fun getTransactionRepository() = txRepo
    fun getSupportTicketRepository() = ticketRepo
    fun getEmployeeRepository() = employeeRepo
    fun getDocumentRepository() = docRepo
    fun getAuditLogRepository() = auditRepo
}

// ═══════════════════════════════════════════════════════════════════════
// APP-LEVEL COMPOSITION LOCAL
// Access the factory from any Composable via: LocalAppViewModelFactory.current
// ═══════════════════════════════════════════════════════════════════════
val LocalAppViewModelFactory = compositionLocalOf<AppViewModelFactory> {
    error("AppViewModelFactory not provided. Wrap your app in CompositionLocalProvider.")
}
