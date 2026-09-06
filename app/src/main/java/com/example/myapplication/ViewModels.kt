package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ═══════════════════════════════════════════════════════════════════════
// UI STATE — Sealed class for all screen states (Req 27/29)
// ═══════════════════════════════════════════════════════════════════════
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    object Empty : UiState<Nothing>()
    data class Error(val message: String) : UiState<Nothing>()
}

// ═══════════════════════════════════════════════════════════════════════
// JOB VIEWMODEL — For Student Job Feed & Admin Job Management
// ═══════════════════════════════════════════════════════════════════════
class JobViewModel(private val repo: JobRepository) : ViewModel() {

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected filter
    private val _activeFilter = MutableStateFlow("All")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    // Job list reacts to search + filter changes
    val jobsUiState: StateFlow<UiState<List<JobEntity>>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            repo.searchJobs(query)
                .map { jobs ->
                    val filtered = when (_activeFilter.value) {
                        "New" -> jobs.filter {
                            it.createdAt >= System.currentTimeMillis() - 3 * 86400000L
                        }
                        "Closing Soon" -> jobs.sortedBy { it.applicationDeadline }.take(10)
                        else -> jobs
                    }
                    if (filtered.isEmpty()) UiState.Empty
                    else UiState.Success(filtered)
                }
                .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
                .onStart { emit(UiState.Loading) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // Admin: all jobs including drafts
    val allJobsState: StateFlow<UiState<List<JobEntity>>> = repo.getAllJobs()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
        .onStart { emit(UiState.Loading) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setFilter(f: String) { _activeFilter.value = f }

    fun saveJob(job: JobEntity) = viewModelScope.launch {
        repo.saveJob(job)
    }

    fun publishJob(jobId: String) = viewModelScope.launch {
        repo.publishJob(jobId)
    }

    fun closeJob(jobId: String) = viewModelScope.launch { repo.closeJob(jobId) }
    fun archiveJob(jobId: String) = viewModelScope.launch { repo.archiveJob(jobId) }
    fun deleteJob(job: JobEntity) = viewModelScope.launch { repo.deleteJob(job) }

    fun generateJobId() = repo.generateJobId()
}

// ═══════════════════════════════════════════════════════════════════════
// APPLICATION VIEWMODEL — For Students, Employees, Admins
// ═══════════════════════════════════════════════════════════════════════
class ApplicationViewModel(private val repo: ApplicationRepository) : ViewModel() {

    // All applications (Admin view)
    val allApplicationsState: StateFlow<UiState<List<ApplicationEntity>>> = repo.getAllApplications()
        .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
        .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
        .onStart { emit(UiState.Loading) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // Student-specific
    private val _studentId = MutableStateFlow("")
    val myApplicationsState: StateFlow<UiState<List<ApplicationEntity>>> = _studentId
        .filter { it.isNotBlank() }
        .flatMapLatest { id ->
            repo.getForStudent(id)
                .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
                .onStart { emit(UiState.Loading) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // Employee-specific
    private val _employeeId = MutableStateFlow("")
    val myAssignedState: StateFlow<UiState<List<ApplicationEntity>>> = _employeeId
        .filter { it.isNotBlank() }
        .flatMapLatest { id ->
            repo.getForEmployee(id)
                .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
                .onStart { emit(UiState.Loading) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    fun setStudentId(id: String) { _studentId.value = id }
    fun setEmployeeId(id: String) { _employeeId.value = id }

    fun getTimeline(appId: String): Flow<List<TimelineEventEntity>> = repo.getTimeline(appId)

    fun isDuplicate(studentId: String, jobId: String, onResult: (Boolean) -> Unit) =
        viewModelScope.launch {
            onResult(repo.isDuplicate(studentId, jobId))
        }

    fun createApplication(app: ApplicationEntity) = viewModelScope.launch {
        repo.createApplication(app)
    }

    fun updateStatus(appId: String, status: String, note: String = "") = viewModelScope.launch {
        repo.updateStatus(appId, status, note)
    }

    fun assignToEmployee(
        appId: String, employeeId: String, employeeName: String,
        priority: String, studentId: String, jobTitle: String
    ) = viewModelScope.launch {
        repo.assignToEmployee(appId, employeeId, employeeName, priority, studentId, jobTitle)
    }

    fun bulkAssign(
        appIds: List<String>,
        employees: List<EmployeeEntity>,
        onComplete: (Map<String, Int>) -> Unit
    ) = viewModelScope.launch {
        val result = repo.bulkAssign(appIds, employees)
        onComplete(result)
    }

    fun generateAppId() = repo.generateAppId()
}

// ═══════════════════════════════════════════════════════════════════════
// ADMIN VIEWMODEL — Analytics, user management, system oversight
// ═══════════════════════════════════════════════════════════════════════
class AdminViewModel(
    private val appRepo: ApplicationRepository,
    private val studentRepo: StudentRepository,
    private val employeeRepo: EmployeeRepository,
    private val jobRepo: JobRepository,
    private val txRepo: TransactionRepository,
    private val ticketRepo: SupportTicketRepository
) : ViewModel() {

    // Analytics state — all from actual DB queries
    private val _analytics = MutableStateFlow(AdminAnalytics())
    val analytics: StateFlow<AdminAnalytics> = _analytics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAnalytics() = viewModelScope.launch {
        _isLoading.value = true
        try {
            _analytics.value = AdminAnalytics(
                // Users
                totalStudents = studentRepo.countTotal(),
                newStudentsToday = studentRepo.countNew(),
                totalEmployees = employeeRepo.count(),

                // Applications
                totalApplications = appRepo.countTotal(),
                todayApplications = appRepo.countTodayNew(),
                pendingApplications = appRepo.countByStatus("PAYMENT_PENDING"),
                processingApplications = appRepo.countByStatus("IN_PROGRESS"),
                submittedApplications = appRepo.countByStatus("SUBMITTED"),
                failedApplications = appRepo.countByStatus("SUBMISSION_FAILED"),
                unassignedApplications = appRepo.countUnassigned(),
                criticalApplications = appRepo.countCritical(),

                // Jobs
                activeJobs = jobRepo.countActive(),
                expiredJobs = jobRepo.countExpired(),
                closedJobs = jobRepo.countClosed(),

                // Revenue
                totalRevenue = txRepo.totalRevenue(),
                todayRevenue = txRepo.revenueToday(),

                // Support
                openTickets = ticketRepo.countOpen()
            )
        } catch (e: Exception) {
            // Keep existing analytics on error
        } finally {
            _isLoading.value = false
        }
    }

    fun getAllEmployees() = employeeRepo.getAll()
    fun getAllApplications() = appRepo.getAllApplications()
    fun getAllJobs() = jobRepo.getAllJobs()
    fun getAllTickets() = ticketRepo.getAll()
}

data class AdminAnalytics(
    val totalStudents: Int = 0,
    val newStudentsToday: Int = 0,
    val totalEmployees: Int = 0,
    val totalApplications: Int = 0,
    val todayApplications: Int = 0,
    val pendingApplications: Int = 0,
    val processingApplications: Int = 0,
    val submittedApplications: Int = 0,
    val failedApplications: Int = 0,
    val unassignedApplications: Int = 0,
    val criticalApplications: Int = 0,
    val activeJobs: Int = 0,
    val expiredJobs: Int = 0,
    val closedJobs: Int = 0,
    val totalRevenue: Double = 0.0,
    val todayRevenue: Double = 0.0,
    val openTickets: Int = 0
)

// ═══════════════════════════════════════════════════════════════════════
// EMPLOYEE VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════
class EmployeeViewModel(
    private val appRepo: ApplicationRepository,
    private val employeeRepo: EmployeeRepository
) : ViewModel() {

    private val _employeeId = MutableStateFlow("")

    val assignedApplications: StateFlow<UiState<List<ApplicationEntity>>> = _employeeId
        .filter { it.isNotBlank() }
        .flatMapLatest { id ->
            appRepo.getForEmployee(id)
                .map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
                .onStart { emit(UiState.Loading) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    private val _performance = MutableStateFlow<EmployeePerformanceEntity?>(null)
    val performance: StateFlow<EmployeePerformanceEntity?> = _performance.asStateFlow()

    fun setEmployeeId(id: String) {
        _employeeId.value = id
        viewModelScope.launch {
            _performance.value = employeeRepo.getPerformance(id)
        }
    }

    fun getCurrentWorkload(employeeId: String, onResult: (Int) -> Unit) =
        viewModelScope.launch { onResult(appRepo.getWorkload(employeeId)) }

    fun updateApplicationStatus(appId: String, status: String, note: String = "") =
        viewModelScope.launch { appRepo.updateStatus(appId, status, note) }

    fun getTimeline(appId: String) = appRepo.getTimeline(appId)
}

// ═══════════════════════════════════════════════════════════════════════
// STUDENT PROFILE VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════
class StudentProfileViewModel(
    private val studentRepo: StudentRepository,
    private val docRepo: DocumentRepository,
    private val notifRepo: NotificationRepository
) : ViewModel() {

    private val _profile = MutableStateFlow<UiState<StudentEntity>>(UiState.Loading)
    val profile: StateFlow<UiState<StudentEntity>> = _profile.asStateFlow()

    private val _profileCompletionPct = MutableStateFlow(0)
    val profileCompletionPct: StateFlow<Int> = _profileCompletionPct.asStateFlow()

    private val _documents = MutableStateFlow<UiState<List<DocumentEntity>>>(UiState.Loading)
    val documents: StateFlow<UiState<List<DocumentEntity>>> = _documents.asStateFlow()

    val activityHistory: MutableStateFlow<List<ActivityHistoryEntity>> = MutableStateFlow(emptyList())

    private var currentStudentId = ""

    fun loadProfile(studentId: String) {
        currentStudentId = studentId
        viewModelScope.launch {
            _profile.value = UiState.Loading
            val student = studentRepo.getById(studentId)
            if (student != null) {
                _profile.value = UiState.Success(student)
                _profileCompletionPct.value = studentRepo.computeCompletion(student)
            } else {
                _profile.value = UiState.Empty
            }
        }
        viewModelScope.launch {
            docRepo.getForOwner(studentId).collect { docs ->
                _documents.value = if (docs.isEmpty()) UiState.Empty else UiState.Success(docs)
            }
        }
        viewModelScope.launch {
            studentRepo.getActivityHistory(studentId).collect {
                activityHistory.value = it
            }
        }
    }

    fun saveProfile(student: StudentEntity) = viewModelScope.launch {
        val withCompletion = student.copy(
            profileCompletionPct = studentRepo.computeCompletion(student)
        )
        studentRepo.saveStudent(withCompletion)
        _profile.value = UiState.Success(withCompletion)
        _profileCompletionPct.value = withCompletion.profileCompletionPct
    }

    fun uploadDocument(doc: DocumentEntity) = viewModelScope.launch {
        docRepo.save(doc)
        studentRepo.logActivity(doc.ownerId, "Document Uploaded", "${doc.documentType}: ${doc.fileName}")
    }

    fun deleteDocument(docId: String) = viewModelScope.launch {
        docRepo.softDelete(docId)
        studentRepo.logActivity(currentStudentId, "Document Deleted", "Document ID: $docId")
    }

    fun getUnreadNotificationCount(userId: String): Flow<Int> = notifRepo.getUnreadCount(userId)
}

// ═══════════════════════════════════════════════════════════════════════
// NOTIFICATION VIEWMODEL
// ═══════════════════════════════════════════════════════════════════════
class NotificationViewModel(private val repo: NotificationRepository) : ViewModel() {

    private val _userId = MutableStateFlow("")
    private val _category = MutableStateFlow("All")

    val notifications: StateFlow<UiState<List<NotificationEntity>>> = combine(
        _userId.filter { it.isNotBlank() },
        _category
    ) { userId, category -> Pair(userId, category) }
        .flatMapLatest { (userId, category) ->
            val flow = if (category == "All") repo.getForUser(userId)
                       else repo.getForUserByCategory(userId, category)
            flow.map { if (it.isEmpty()) UiState.Empty else UiState.Success(it) }
                .catch { e -> emit(UiState.Error(AppError.userMessage(e))) }
                .onStart { emit(UiState.Loading) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    val unreadCount: StateFlow<Int> = _userId
        .filter { it.isNotBlank() }
        .flatMapLatest { repo.getUnreadCount(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setUser(userId: String) { _userId.value = userId }
    fun setCategory(cat: String) { _category.value = cat }
    fun markRead(id: String) = viewModelScope.launch { repo.markRead(id) }
    fun markAllRead() = viewModelScope.launch { repo.markAllRead(_userId.value) }
}
