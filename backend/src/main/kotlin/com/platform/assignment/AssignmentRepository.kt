package com.platform.assignment

import com.platform.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AssignmentRepository : JpaRepository<Assignment, UUID> {
    fun findByEmployee(employee: User): List<Assignment>
}
