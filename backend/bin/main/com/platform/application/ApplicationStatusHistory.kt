package com.platform.application

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "application_status_history")
class ApplicationStatusHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "application_id")
    val application: Application,

    @Enumerated(EnumType.STRING)
    val status: ApplicationStatus,

    val remarks: String? = null,
    
    @Column(name = "changed_by_id")
    val changedById: UUID? = null, // ID of the Employee or Admin who changed the status

    val changedAt: LocalDateTime = LocalDateTime.now()
)
