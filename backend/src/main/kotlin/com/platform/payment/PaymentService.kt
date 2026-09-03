package com.platform.payment

import com.platform.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {
    @Transactional
    fun initiatePayment(user: User, officialFee: Double, serviceFee: Double): Payment {
        val totalAmount = officialFee + serviceFee
        // In real app, call Razorpay API to create order
        val razorpayOrderId = "order_" + UUID.randomUUID().toString().substring(0, 8)

        val payment = Payment(
            user = user,
            amount = totalAmount,
            officialFee = officialFee,
            serviceFee = serviceFee,
            razorpayOrderId = razorpayOrderId,
            status = PaymentStatus.INITIATED
        )

        return paymentRepository.save(payment)
    }

    @Transactional
    fun verifyPayment(orderId: String, paymentId: String, signature: String) {
        val payment = paymentRepository.findByRazorpayOrderId(orderId) ?: throw Exception("Payment record not found")
        
        // In real app, verify signature using HmacSHA256
        payment.razorpayPaymentId = paymentId
        payment.razorpaySignature = signature
        payment.status = PaymentStatus.SUCCESSFUL
        payment.verifiedAt = java.time.LocalDateTime.now()

        paymentRepository.save(payment)
    }
}
