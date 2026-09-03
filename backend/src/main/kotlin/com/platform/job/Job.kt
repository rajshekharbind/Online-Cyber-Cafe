package com.platform.job

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "jobs")
class Job(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    val title: String,
    val organization: String,
    val department: String? = null,
    val isGovernment: Boolean = true,
    
    @Enumerated(EnumType.STRING)
    var status: JobStatus = JobStatus.ACTIVE,

    val qualificationRequired: String,
    val minAge: Int? = null,
    val maxAge: Int? = null,
    
    val officialFee: Double,
    val serviceFee: Double,
    
    val startDate: LocalDateTime,
    val deadline: LocalDateTime,
    
    val officialNotificationUrl: String? = null,
    val officialWebsiteUrl: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val eligibilityRulesJson: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var lastVerifiedAt: LocalDateTime = LocalDateTime.now()
)

enum class JobStatus {
    UPCOMING, ACTIVE, DEADLINE_APPROACHING, EXPIRED, CLOSED
}
