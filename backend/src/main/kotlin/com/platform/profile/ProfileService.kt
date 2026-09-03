package com.platform.profile

import com.platform.user.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(
    private val profileRepository: StudentProfileRepository,
    private val userRepository: UserRepository
) {
    fun getProfile(userId: UUID): StudentProfile {
        return profileRepository.findById(userId).orElseThrow { Exception("Profile not found") }
    }

    fun calculateCompletion(profile: StudentProfile): Int {
        val fields = listOf(
            profile.firstName, profile.lastName, profile.fatherName, profile.motherName,
            profile.dob, profile.gender, profile.category
        )
        val completedFields = fields.count { it != null }
        val personalWeight = (completedFields.toDouble() / fields.size) * 40

        val addressWeight = if (profile.currentAddress != null) 20 else 0
        
        // Simplified logic: actual implementation would check education records and docs
        return (personalWeight + addressWeight + 20).toInt().coerceAtMost(100)
    }
}
