package com.platform.job

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface JobRepository : JpaRepository<Job, UUID> {
    fun findByStatus(status: JobStatus): List<Job>
    
    @Query("SELECT j FROM Job j WHERE j.deadline > CURRENT_TIMESTAMP AND j.status = 'ACTIVE'")
    fun findActiveJobs(): List<Job>
}
