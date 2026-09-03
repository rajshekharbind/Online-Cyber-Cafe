package com.platform.application

import com.platform.job.Job
import com.platform.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ApplicationRepository : JpaRepository<Application, UUID> {
    fun findByStudentAndJobAndStatusNot(student: User, job: Job, status: ApplicationStatus): List<Application>
    fun findByStudentOrderByCreatedAtDesc(student: User): List<Application>
}
