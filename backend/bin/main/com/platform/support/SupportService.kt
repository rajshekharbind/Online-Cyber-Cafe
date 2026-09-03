package com.platform.support

import com.platform.application.ApplicationRepository
import com.platform.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SupportService(
    private val ticketRepository: SupportTicketRepository,
    private val messageRepository: SupportMessageRepository,
    private val applicationRepository: ApplicationRepository
) {
    @Transactional
    fun createTicket(student: User, applicationId: UUID?, subject: String, content: String): SupportTicket {
        val application = applicationId?.let { 
            applicationRepository.findById(it).orElse(null) 
        }
        
        val ticket = SupportTicket(
            student = student,
            application = application,
            subject = subject
        )
        val savedTicket = ticketRepository.save(ticket)
        
        val message = SupportMessage(
            ticket = savedTicket,
            sender = student,
            content = content
        )
        messageRepository.save(message)
        
        return savedTicket
    }

    fun getStudentTickets(student: User): List<SupportTicket> {
        return ticketRepository.findByStudentOrderByUpdatedAtDesc(student)
    }

    fun getTicketMessages(ticketId: UUID, user: User): List<SupportMessage> {
        val ticket = ticketRepository.findById(ticketId).orElseThrow { Exception("Ticket not found") }
        // Basic auth check
        if (ticket.student.id != user.id && user.role.name != "ADMIN" && user.role.name != "EMPLOYEE") {
            throw Exception("Unauthorized")
        }
        return messageRepository.findByTicketOrderBySentAtAsc(ticket)
    }
}
