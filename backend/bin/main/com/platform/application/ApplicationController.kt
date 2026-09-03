package com.platform.application

import com.platform.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/applications")
class ApplicationController(
    private val applicationRepository: ApplicationRepository,
    private val historyRepository: ApplicationStatusHistoryRepository
) {
    @GetMapping
    fun getMyApplications(@AuthenticationPrincipal user: User): ResponseEntity<List<Application>> {
        return ResponseEntity.ok(applicationRepository.findByStudentOrderByCreatedAtDesc(user))
    }

    @GetMapping("/{id}")
    fun getApplicationDetail(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApplicationDetailDto> {
        val application = applicationRepository.findById(id).orElseThrow { Exception("Application not found") }
        
        // Authorization check
        if (application.student.id != user.id) {
            throw Exception("Unauthorized")
        }

        val history = historyRepository.findByApplicationOrderByChangedAtDesc(application)
        
        return ResponseEntity.ok(ApplicationDetailDto(application, history))
    }
}

data class ApplicationDetailDto(
    val application: Application,
    val history: List<ApplicationStatusHistory>
)
