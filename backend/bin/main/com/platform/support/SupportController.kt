package com.platform.support

import com.platform.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/support")
class SupportController(private val supportService: SupportService) {

    @PostMapping("/tickets")
    fun createTicket(
        @AuthenticationPrincipal user: User,
        @RequestBody request: CreateTicketRequest
    ): ResponseEntity<SupportTicket> {
        return ResponseEntity.ok(supportService.createTicket(user, request.applicationId, request.subject, request.content))
    }

    @GetMapping("/tickets")
    fun getMyTickets(@AuthenticationPrincipal user: User): ResponseEntity<List<SupportTicket>> {
        return ResponseEntity.ok(supportService.getStudentTickets(user))
    }

    @GetMapping("/tickets/{id}/messages")
    fun getMessages(
        @PathVariable id: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<SupportMessage>> {
        return ResponseEntity.ok(supportService.getTicketMessages(id, user))
    }
}

data class CreateTicketRequest(
    val applicationId: UUID?,
    val subject: String,
    val content: String
)
