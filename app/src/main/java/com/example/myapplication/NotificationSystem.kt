package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────
// Notification Types (12 as per spec Point 30)
// ─────────────────────────────────────────────────────────────
enum class NotificationType(
    val displayName: String,
    val color: Long,
    val emoji: String
) {
    REGISTRATION_SUCCESSFUL("Registration Successful",     0xFF2E7D32, "🎉"),
    PROFILE_INCOMPLETE("Profile Incomplete",               0xFFF57F17, "⚠️"),
    NEW_JOB_ALERT("New Matching Job",                      0xFF1565C0, "💼"),
    JOB_DEADLINE_APPROACHING("Deadline Approaching",       0xFFD32F2F, "⏰"),
    PAYMENT_SUCCESSFUL("Payment Successful",               0xFF2E7D32, "✅"),
    APPLICATION_ASSIGNED("Application Assigned",           0xFF6A1B9A, "📋"),
    PROCESSING_STARTED("Processing Started",               0xFF0277BD, "⚙️"),
    STUDENT_ACTION_REQUIRED("Action Required",             0xFFE65100, "🔔"),
    APPLICATION_SUBMITTED("Application Submitted",         0xFF1B5E20, "🎊"),
    APPLICATION_FAILED("Application Failed",               0xFFC62828, "❌"),
    REFUND_INITIATED("Refund Initiated",                   0xFF00695C, "💰"),
    DEADLINE_REMINDER("Application Deadline Reminder",    0xFFAD1457, "📅")
}

// ─────────────────────────────────────────────────────────────
// Notification Channel
// ─────────────────────────────────────────────────────────────
enum class NotificationChannel { IN_APP, EMAIL, SMS, WHATSAPP }

// ─────────────────────────────────────────────────────────────
// AppNotification model
// ─────────────────────────────────────────────────────────────
data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val body: String,
    val timestamp: String = nowFormatted(),
    val appId: String = "",
    var isRead: Boolean = false,
    val channels: List<NotificationChannel> = listOf(NotificationChannel.IN_APP)
)

// ─────────────────────────────────────────────────────────────
// Notification Template (for Admin to configure)
// ─────────────────────────────────────────────────────────────
data class NotificationTemplate(
    val type: NotificationType,
    var titleTemplate: String,
    var bodyTemplate: String,
    var enabledChannels: MutableList<NotificationChannel> = mutableListOf(NotificationChannel.IN_APP, NotificationChannel.SMS)
)

// ─────────────────────────────────────────────────────────────
// Centralized Notification Store
// ─────────────────────────────────────────────────────────────
object NotificationStore {
    private val _notifications = mutableStateListOf<AppNotification>()
    val notifications: List<AppNotification> get() = _notifications

    // Default templates (Admin-configurable)
    val templates = mutableStateListOf(
        NotificationTemplate(NotificationType.REGISTRATION_SUCCESSFUL,
            "Welcome to Cyber Café!", "Your registration is complete. Start exploring government jobs now."),
        NotificationTemplate(NotificationType.PROFILE_INCOMPLETE,
            "Complete Your Profile", "Your profile is {percentage}% complete. Add missing details to get better job matches."),
        NotificationTemplate(NotificationType.NEW_JOB_ALERT,
            "New Job: {jobTitle}", "A new government job matching your profile is now open. Deadline: {deadline}."),
        NotificationTemplate(NotificationType.JOB_DEADLINE_APPROACHING,
            "⏰ Deadline in {days} days", "Your {jobTitle} application deadline is approaching. Apply now!"),
        NotificationTemplate(NotificationType.PAYMENT_SUCCESSFUL,
            "Payment Confirmed ✓", "₹{amount} received for {jobTitle}. Your application will begin processing soon."),
        NotificationTemplate(NotificationType.APPLICATION_ASSIGNED,
            "Application Assigned", "Your {jobTitle} application has been assigned to {employeeName}."),
        NotificationTemplate(NotificationType.PROCESSING_STARTED,
            "Processing Started ⚙️", "Our team has started filling your {jobTitle} application form."),
        NotificationTemplate(NotificationType.STUDENT_ACTION_REQUIRED,
            "🔔 Action Required", "Our executive needs your help to proceed with your {jobTitle} application. Please check the app."),
        NotificationTemplate(NotificationType.APPLICATION_SUBMITTED,
            "Application Submitted 🎊", "Your {jobTitle} application has been submitted! Application No: {appNo}."),
        NotificationTemplate(NotificationType.APPLICATION_FAILED,
            "Application Failed ❌", "We encountered an issue submitting your {jobTitle} application. Our team will contact you."),
        NotificationTemplate(NotificationType.REFUND_INITIATED,
            "Refund Initiated 💰", "A refund of ₹{amount} has been initiated and will reach your account in 5–7 business days."),
        NotificationTemplate(NotificationType.DEADLINE_REMINDER,
            "Deadline Reminder 📅", "The deadline for {jobTitle} is {date}. Don't miss out!")
    )

    init {
        // Pre-seed demo notifications
        _notifications.addAll(listOf(
            AppNotification("N-001", NotificationType.APPLICATION_SUBMITTED,
                "Application Submitted 🎊",
                "Your RRB NTPC 2026 application has been submitted! App No: RRB2026-00487.",
                "20 Aug, 11:32 AM", "APP-098", isRead = true),
            AppNotification("N-002", NotificationType.PAYMENT_SUCCESSFUL,
                "Payment Confirmed ✓",
                "₹650 received for RRB NTPC 2026. Your application is queued for processing.",
                "20 Aug, 09:05 AM", "APP-098", isRead = true),
            AppNotification("N-003", NotificationType.APPLICATION_ASSIGNED,
                "Application Assigned",
                "Your SSC CGL 2024 application has been assigned to Executive Amit.",
                "26 Aug, 10:18 AM", "APP-101", isRead = false),
            AppNotification("N-004", NotificationType.STUDENT_ACTION_REQUIRED,
                "🔔 Action Required",
                "Our executive needs your OTP to proceed with your IBPS PO XIV application. Please check the app.",
                "25 Aug, 04:22 PM", "APP-105", isRead = false),
            AppNotification("N-005", NotificationType.NEW_JOB_ALERT,
                "New Job: UPSC CSE 2026",
                "A new UPSC Civil Services exam matching your profile is now open. Last Date: 15 Sep 2026.",
                "28 Aug, 09:00 AM", "", isRead = false),
            AppNotification("N-006", NotificationType.JOB_DEADLINE_APPROACHING,
                "⏰ Deadline in 5 days",
                "Your IBPS PO XIV application deadline is 30 Aug 2026. Make sure everything is in order.",
                "25 Aug, 10:00 AM", "APP-105", isRead = false)
        ))
    }

    fun addNotification(
        type: NotificationType,
        title: String,
        body: String,
        appId: String = "",
        channels: List<NotificationChannel> = listOf(NotificationChannel.IN_APP)
    ) {
        _notifications.add(0, AppNotification(
            id = "N-${System.currentTimeMillis()}",
            type = type, title = title, body = body,
            appId = appId, channels = channels
        ))
    }

    fun markRead(notificationId: String) {
        _notifications.find { it.id == notificationId }?.isRead = true
    }

    fun markAllRead() {
        _notifications.forEach { it.isRead = true }
    }

    val unreadCount: Int get() = _notifications.count { !it.isRead }

    fun updateTemplate(type: NotificationType, titleTemplate: String, bodyTemplate: String, channels: List<NotificationChannel>) {
        templates.find { it.type == type }?.let {
            it.titleTemplate = titleTemplate
            it.bodyTemplate = bodyTemplate
            it.enabledChannels.clear()
            it.enabledChannels.addAll(channels)
        }
    }
}
