package com.platform.job

import com.platform.eligibility.EligibilityResult
import com.platform.eligibility.EligibilityService
import com.platform.profile.ProfileService
import com.platform.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val jobRepository: JobRepository,
    private val eligibilityService: EligibilityService,
    private val profileService: ProfileService
) {

    @GetMapping
    fun getAllJobs(): ResponseEntity<List<Job>> = ResponseEntity.ok(jobRepository.findActiveJobs())

    @GetMapping("/{id}")
    fun getJobDetail(@PathVariable id: UUID): ResponseEntity<Job> {
        val job = jobRepository.findById(id).orElseThrow { Exception("Job not found") }
        return ResponseEntity.ok(job)
    }

    @GetMapping("/{id}/check-eligibility")
    fun checkEligibility(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<EligibilityResult> {
        val job = jobRepository.findById(id).orElseThrow { Exception("Job not found") }
        val profile = profileService.getProfile(user.id!!)
        return ResponseEntity.ok(eligibilityService.checkEligibility(profile, job))
    }
}
