package com.platform.analytics

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/analytics")
class AdminAnalyticsController(private val analyticsService: AnalyticsService) {

    @GetMapping("/summary")
    fun getSummary(): ResponseEntity<AdminStatsDto> {
        // In real app, add @PreAuthorize("hasRole('ADMIN')")
        return ResponseEntity.ok(analyticsService.getAdminStats())
    }
}
