package com.platform.support

import com.platform.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SupportTicketRepository : JpaRepository<SupportTicket, UUID> {
    fun findByStudentOrderByUpdatedAtDesc(student: User): List<SupportTicket>
}

interface SupportMessageRepository : JpaRepository<SupportMessage, UUID> {
    fun findByTicketOrderBySentAtAsc(ticket: SupportTicket): List<SupportMessage>
}
