package com.platform.profile

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface StudentProfileRepository : JpaRepository<StudentProfile, UUID>
