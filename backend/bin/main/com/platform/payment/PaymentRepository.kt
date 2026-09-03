package com.platform.payment

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PaymentRepository : JpaRepository<Payment, UUID> {
    fun findByRazorpayOrderId(orderId: String): Payment?
}
