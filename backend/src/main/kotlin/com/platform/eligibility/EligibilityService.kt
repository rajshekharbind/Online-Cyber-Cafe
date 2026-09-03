package com.platform.eligibility

import com.platform.job.Job
import com.platform.profile.StudentProfile
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period

@Service
class EligibilityService {

    fun checkEligibility(profile: StudentProfile, job: Job): EligibilityResult {
        // Age check
        val age = profile.dob?.let { Period.between(it, LocalDate.now()).years }
        
        if (age != null) {
            if (job.minAge != null && age < job.minAge) {
                return EligibilityResult(EligibilityStatus.NOT_ELIGIBLE, "Below minimum age requirement")
            }
            if (job.maxAge != null && age > job.maxAge) {
                return EligibilityResult(EligibilityStatus.NOT_ELIGIBLE, "Above maximum age requirement")
            }
        }

        // Add more checks here (Education, Category, etc.)
        
        return EligibilityResult(EligibilityStatus.POTENTIALLY_ELIGIBLE, "Please verify official notification for specific criteria")
    }
}

data class EligibilityResult(
    val status: EligibilityStatus,
    val reason: String
)

enum class EligibilityStatus {
    ELIGIBLE, NOT_ELIGIBLE, POTENTIALLY_ELIGIBLE
}
