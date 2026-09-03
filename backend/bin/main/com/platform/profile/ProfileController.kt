package com.platform.profile

import com.platform.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/profile")
class ProfileController(private val profileService: ProfileService) {

    @GetMapping
    fun getMyProfile(@AuthenticationPrincipal user: User): ResponseEntity<StudentProfile> {
        return ResponseEntity.ok(profileService.getProfile(user.id!!))
    }

    @GetMapping("/completion")
    fun getCompletion(@AuthenticationPrincipal user: User): ResponseEntity<Map<String, Int>> {
        val profile = profileService.getProfile(user.id!!)
        return ResponseEntity.ok(mapOf("percentage" to profileService.calculateCompletion(profile)))
    }
}
