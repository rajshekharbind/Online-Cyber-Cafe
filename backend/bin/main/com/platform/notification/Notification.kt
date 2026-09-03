package com.platform.notification

import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "notifications")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val title: String,
    val message: String,
    
    @Enumerated(EnumType.STRING)
    val type: NotificationType,

    var isRead: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationType {
    APPLICATION_STATUS, JOB_ALERT, PAYMENT, SYSTEM
}
