package com.platform.audit

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "audit_logs")
class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    val actorId: UUID,
    val actorEmail: String,
    val action: String,
    val targetType: String,
    val targetId: String? = null,
    
    @Column(columnDefinition = "TEXT")
    val metadata: String? = null,
    
    val timestamp: LocalDateTime = LocalDateTime.now()
)
