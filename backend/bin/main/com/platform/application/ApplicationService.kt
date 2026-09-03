package com.platform.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.platform.job.JobRepository
import com.platform.profile.ProfileService
import com.platform.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ApplicationService(
    private val applicationRepository: ApplicationRepository,
    private val jobRepository: JobRepository,
    private val profileService: ProfileService,
    private val objectMapper: ObjectMapper
) {
    @Transactional
    fun createApplication(user: User, jobId: UUID): Application {
        val job = jobRepository.findById(jobId).orElseThrow { Exception("Job not found") }
        val profile = profileService.getProfile(user.id!!)
        
        // Check for duplicate active application
        val existing = applicationRepository.findByStudentAndJobAndStatusNot(user, job, ApplicationStatus.CANCELLED)
        if (existing.isNotEmpty()) {
            throw Exception("You already have an active application for this job")
        }

        // Create Snapshot
        val profileJson = objectMapper.writeValueAsString(profile)
        val snapshot = ApplicationSnapshot(
            profileDataJson = profileJson,
            documentsDataJson = "[]" // In real implementation, fetch and serialize document metadata
        )

        val application = Application(
            student = user,
            job = job,
            snapshot = snapshot,
            status = ApplicationStatus.PAYMENT_PENDING
        )

        return applicationRepository.save(application)
    }
}
