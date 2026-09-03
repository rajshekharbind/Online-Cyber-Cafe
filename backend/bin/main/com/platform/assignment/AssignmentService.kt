package com.platform.assignment

import com.platform.application.ApplicationRepository
import com.platform.application.ApplicationStatus
import com.platform.application.ApplicationStatusService
import com.platform.user.UserRepository
import com.platform.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val applicationRepository: ApplicationRepository,
    private val userRepository: UserRepository,
    private val statusService: ApplicationStatusService
) {
    @Transactional
    fun assignApplication(applicationId: UUID, employeeId: UUID, admin: User): Assignment {
        val application = applicationRepository.findById(applicationId).orElseThrow { Exception("Application not found") }
        val employee = userRepository.findById(employeeId).orElseThrow { Exception("Employee not found") }

        val assignment = Assignment(
            application = application,
            employee = employee,
            assignedBy = admin
        )

        statusService.updateStatus(applicationId, ApplicationStatus.ASSIGNED, "Assigned to ${employee.email}", admin.id)
        
        return assignmentRepository.save(assignment)
    }

    fun getEmployeeTasks(employee: User): List<Assignment> {
        return assignmentRepository.findByEmployee(employee)
    }
}
