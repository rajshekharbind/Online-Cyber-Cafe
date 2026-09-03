package com.platform.support

import com.platform.application.Application
import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "support_tickets")
class SupportTicket(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "student_id")
    val student: User,

    @ManyToOne
    @JoinColumn(name = "application_id")
    val application: Application? = null,

    val subject: String,
    
    @Enumerated(EnumType.STRING)
    var status: TicketStatus = TicketStatus.OPEN,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TicketStatus {
    OPEN, IN_PROGRESS, WAITING_FOR_STUDENT, RESOLVED, CLOSED
}

@Entity
@Table(name = "support_messages")
class SupportMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    val ticket: SupportTicket,

    @ManyToOne
    @JoinColumn(name = "sender_id")
    val sender: User,

    @Column(columnDefinition = "TEXT")
    val content: String,

    val sentAt: LocalDateTime = LocalDateTime.now()
)
