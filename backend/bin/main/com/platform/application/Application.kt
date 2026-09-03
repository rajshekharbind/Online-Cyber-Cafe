package com.platform.application

import com.platform.job.Job
import com.platform.payment.Payment
import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "applications")
class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "student_id")
    val student: User,

    @ManyToOne
    @JoinColumn(name = "job_id")
    val job: Job,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "snapshot_id")
    val snapshot: ApplicationSnapshot,

    @OneToOne
    @JoinColumn(name = "payment_id")
    var payment: Payment? = null,

    @Enumerated(EnumType.STRING)
    var status: ApplicationStatus = ApplicationStatus.PAYMENT_PENDING,

    var applicationNumber: String? = null,
    var registrationNumber: String? = null,
    
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ApplicationStatus {
    PAYMENT_PENDING,
    PAYMENT_SUCCESSFUL,
    PENDING_PROCESSING,
    ASSIGNED,
    IN_PROGRESS,
    SUBMITTED,
    FAILED,
    CANCELLED
}

@Entity
@Table(name = "application_snapshots")
class ApplicationSnapshot(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(columnDefinition = "TEXT")
    val profileDataJson: String,

    @Column(columnDefinition = "TEXT")
    val documentsDataJson: String,

    val snappedAt: LocalDateTime = LocalDateTime.now()
)
