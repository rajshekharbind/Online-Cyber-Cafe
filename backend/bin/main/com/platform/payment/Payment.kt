package com.platform.payment

import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "payments")
class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val amount: Double,
    val officialFee: Double,
    val serviceFee: Double,

    val razorpayOrderId: String,
    var razorpayPaymentId: String? = null,
    var razorpaySignature: String? = null,

    @Enumerated(EnumType.STRING)
    var status: PaymentStatus = PaymentStatus.INITIATED,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var verifiedAt: LocalDateTime? = null
)

enum class PaymentStatus {
    INITIATED, SUCCESSFUL, FAILED, REFUNDED
}
