package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Payment Statuses as per business requirements
 */
enum class PaymentStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    INITIATED("Payment Initiated", androidx.compose.ui.graphics.Color(0xFF2196F3)),
    SUCCESSFUL("Payment Successful", androidx.compose.ui.graphics.Color(0xFF4CAF50)),
    FAILED("Payment Failed", androidx.compose.ui.graphics.Color(0xFFD32F2F)),
    PENDING("Payment Pending", androidx.compose.ui.graphics.Color(0xFFFFA000)),
    REFUND_INITIATED("Refund Initiated", androidx.compose.ui.graphics.Color(0xFF9C27B0)),
    REFUNDED("Refunded", androidx.compose.ui.graphics.Color(0xFF757575))
}

/**
 * Detailed Transaction Record
 * Stores all 11 required data points
 */
data class TransactionRecord(
    val userId: String,
    val applicationId: String,
    val paymentId: String,
    val orderId: String,
    val totalAmount: String,
    val officialFee: String,
    val serviceFee: String,
    var status: PaymentStatus,
    val timestamp: String,
    val gatewayReference: String,
    val refundStatus: String = "N/A"
)

object TransactionStore {
    private val transactions = mutableStateListOf<TransactionRecord>()

    init {
        // Seeding initial history as per user's image
        recordTransaction(
            userId = "USER-9921",
            appId = "APP-244",
            orderId = "ORD-955877",
            payId = "PAY-244123",
            official = "₹40",
            service = "₹10",
            total = "₹50",
            gatewayRef = "GREF-9882",
            timestamp = "29 Aug 2026, 11:14"
        )
        recordTransaction(
            userId = "USER-9921",
            appId = "APP-177",
            orderId = "ORD-507057",
            payId = "PAY-177456",
            official = "₹850",
            service = "₹100",
            total = "₹950",
            gatewayRef = "GREF-5070",
            timestamp = "29 Aug 2026, 11:15"
        )
    }

    fun recordTransaction(
        userId: String,
        appId: String,
        orderId: String,
        payId: String,
        official: String,
        service: String,
        total: String,
        gatewayRef: String,
        status: PaymentStatus = PaymentStatus.SUCCESSFUL,
        timestamp: String? = null
    ) {
        val record = TransactionRecord(
            userId = userId,
            applicationId = appId,
            paymentId = payId,
            orderId = orderId,
            totalAmount = total,
            officialFee = official,
            serviceFee = service,
            status = status,
            timestamp = timestamp ?: java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.getDefault()).format(java.util.Date()),
            gatewayReference = gatewayRef
        )
        transactions.add(0, record) // Add to top
    }

    fun getTransactions() = transactions

    fun refundTransaction(paymentId: String, reason: String) {
        val index = transactions.indexOfFirst { it.paymentId == paymentId }
        if (index != -1) {
            val record = transactions[index]
            transactions[index] = record.copy(status = PaymentStatus.REFUND_INITIATED, refundStatus = reason)
            
            // Trigger a notification
            NotificationStore.addNotification(
                type = NotificationType.REFUND_INITIATED,
                title = "Refund Initiated",
                body = "Your refund of ${record.totalAmount} for Application ${record.applicationId} has been initiated due to: $reason.",
                appId = record.applicationId
            )
        }
    }
}

/**
 * Dynamic Wallet Store to calculate total payments across the app
 */
object WalletStore {
    val totalSpent: Double
        get() = TransactionStore.getTransactions()
            .filter { it.status == PaymentStatus.SUCCESSFUL }
            .sumOf { it.totalAmount.replace(Regex("[^0-9.]"), "").toDoubleOrNull() ?: 0.0 }
}
