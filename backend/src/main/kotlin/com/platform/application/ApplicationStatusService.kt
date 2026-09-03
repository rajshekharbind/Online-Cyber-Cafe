package com.platform.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationStatusService(
    private val applicationRepository: ApplicationRepository,
    private val historyRepository: ApplicationStatusHistoryRepository
) {
    @Transactional
    fun updateStatus(applicationId: UUID, newStatus: ApplicationStatus, remarks: String? = null, actorId: UUID? = null): Application {
        val application = applicationRepository.findById(applicationId).orElseThrow { Exception("Application not found") }
        
        application.status = newStatus
        application.updatedAt = java.time.LocalDateTime.now()
        
        val history = ApplicationStatusHistory(
            application = application,
            status = newStatus,
            remarks = remarks,
            changedById = actorId
        )
        
        historyRepository.save(history)
        return applicationRepository.save(application)
    }
}
