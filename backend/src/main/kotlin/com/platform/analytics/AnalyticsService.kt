package com.platform.analytics

import com.platform.application.ApplicationRepository
import com.platform.application.ApplicationStatus
import com.platform.payment.PaymentRepository
import com.platform.payment.PaymentStatus
import com.platform.user.UserRepository
import org.springframework.stereotype.Service

@Service
class AnalyticsService(
    private val userRepository: UserRepository,
    private val applicationRepository: ApplicationRepository,
    private val paymentRepository: PaymentRepository
) {
    fun getAdminStats(): AdminStatsDto {
        val totalStudents = userRepository.count()
        val applications = applicationRepository.findAll()
        val payments = paymentRepository.findAll()

        val revenue = payments.filter { it.status == PaymentStatus.SUCCESSFUL }
            .sumOf { it.serviceFee }
        
        val statusCounts = applications.groupingBy { it.status }.eachCount()

        return AdminStatsDto(
            totalStudents = totalStudents,
            totalApplications = applications.size.toLong(),
            totalRevenue = revenue,
            pendingCount = statusCounts[ApplicationStatus.PENDING_PROCESSING] ?: 0,
            submittedCount = statusCounts[ApplicationStatus.SUBMITTED] ?: 0,
            failedCount = statusCounts[ApplicationStatus.FAILED] ?: 0
        )
    }
}

data class AdminStatsDto(
    val totalStudents: Long,
    val totalApplications: Long,
    val totalRevenue: Double,
    val pendingCount: Int,
    val submittedCount: Int,
    val failedCount: Int
)
