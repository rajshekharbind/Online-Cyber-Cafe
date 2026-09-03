package com.platform.assignment

import com.platform.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/employee")
class EmployeeController(
    private val assignmentService: AssignmentService
) {
    @GetMapping("/tasks")
    fun getMyTasks(@AuthenticationPrincipal user: User): ResponseEntity<List<Assignment>> {
        // In a real app, verify that user has EMPLOYEE role
        return ResponseEntity.ok(assignmentService.getEmployeeTasks(user))
    }
}
