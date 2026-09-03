package com.platform.assignment

import com.platform.application.Application
import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "assignments")
class Assignment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @OneToOne
    @JoinColumn(name = "application_id")
    val application: Application,

    @ManyToOne
    @JoinColumn(name = "employee_id")
    val employee: User,

    val assignedAt: LocalDateTime = LocalDateTime.now(),
    
    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    val assignedBy: User
)
