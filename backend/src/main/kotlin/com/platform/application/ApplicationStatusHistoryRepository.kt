package com.platform.application

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ApplicationStatusHistoryRepository : JpaRepository<ApplicationStatusHistory, UUID> {
    fun findByApplicationOrderByChangedAtDesc(application: Application): List<ApplicationStatusHistory>
}
