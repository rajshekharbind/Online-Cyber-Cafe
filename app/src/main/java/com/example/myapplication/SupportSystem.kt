package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

// ─────────────────────────────────────────────
// Data Models
// ─────────────────────────────────────────────

data class SupportTicket(
    val ticketId: String,
    val studentName: String,
    val studentPhone: String,
    val category: String,
    val subject: String,
    val description: String,
    val submittedAt: String,
    val applicationId: String? = null,    // Req 49: Optional link to specific application
    var status: String = "Open",          // Req 49: Open | In Progress | Waiting for Student | Resolved | Closed
    var assignedTo: String = "Unassigned",
    var responseMessage: String = "",
    val messages: MutableList<TicketMessage> = mutableListOf()
)

data class TicketMessage(
    val sender: String,   // "Student" | "Support"
    val text: String,
    val time: String
)

// ─────────────────────────────────────────────
// Central Store  (in-memory, observable)
// ─────────────────────────────────────────────
object SupportTicketStore {

    private val _tickets = mutableStateListOf(
        SupportTicket(
            ticketId = "TKT-001",
            studentName = "Rahul Kumar",
            studentPhone = "9876543210",
            category = "Payment Issue",
            subject = "Deduction without application",
            description = "Money was deducted from my account but the application was not submitted.",
            submittedAt = "30 Aug 2026 • 09:15 AM",
            status = "In Progress",
            assignedTo = "Executive Amit",
            messages = mutableListOf(
                TicketMessage("Student", "Money was deducted but form not submitted.", "09:15 AM"),
                TicketMessage("Support", "We are checking this issue. Please hold.", "09:30 AM")
            )
        ),
        SupportTicket(
            ticketId = "TKT-002",
            studentName = "Priya Sharma",
            studentPhone = "9123456789",
            category = "Document Problem",
            subject = "Aadhaar upload failing",
            description = "Every time I try to upload my Aadhaar card it gives an error.",
            submittedAt = "29 Aug 2026 • 03:40 PM",
            status = "Open",
            assignedTo = "Unassigned",
            messages = mutableListOf(
                TicketMessage("Student", "Aadhaar upload always fails on step 2.", "03:40 PM")
            )
        ),
        SupportTicket(
            ticketId = "TKT-003",
            studentName = "Amit Singh",
            studentPhone = "9988776655",
            category = "OTP Not Received",
            subject = "OTP not received during form filling",
            description = "The portal is asking for OTP but I am not receiving it on my phone.",
            submittedAt = "28 Aug 2026 • 11:00 AM",
            status = "Resolved",
            assignedTo = "Executive Priya",
            responseMessage = "OTP issue resolved. Form submitted successfully.",
            messages = mutableListOf(
                TicketMessage("Student", "Not getting OTP from portal.", "11:00 AM"),
                TicketMessage("Support", "Please try after 5 minutes and use Resend OTP.", "11:10 AM"),
                TicketMessage("Student", "Got it now, thanks!", "11:20 AM"),
                TicketMessage("Support", "Great! Form submitted.", "11:25 AM")
            )
        )
    )

    fun getTickets(): List<SupportTicket> = _tickets

    fun getTicketById(ticketId: String) = _tickets.find { it.ticketId == ticketId }

    fun submitTicket(ticket: SupportTicket) {
        _tickets.add(0, ticket)
    }

    fun updateTicket(ticketId: String, status: String? = null, assignedTo: String? = null, response: String? = null) {
        _tickets.find { it.ticketId == ticketId }?.let { t ->
            if (status != null) t.status = status
            if (assignedTo != null) t.assignedTo = assignedTo
            if (response != null) t.responseMessage = response
        }
    }

    fun addMessage(ticketId: String, message: TicketMessage) {
        _tickets.find { it.ticketId == ticketId }?.messages?.add(message)
    }

    fun generateTicketId(): String = "TKT-${(100..999).random()}"
}
