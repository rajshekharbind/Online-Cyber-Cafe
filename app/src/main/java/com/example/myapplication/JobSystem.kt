package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

object JobStore {
    val jobs = mutableStateListOf(
        StandardizedJob(
            id = "JOB-101",
            title = "SSC CGL 2024",
            organization = "SSC",
            description = "Combined Graduate Level Examination for various Group B & C posts.",
            department = "Central",
            type = "Government",
            location = "India",
            qualification = "Degree",
            branch = "Any",
            ageLimit = "18-32",
            categoryRules = "OBC/SC/ST as per Govt",
            experience = "Fresher",
            salary = "₹45k-1.5L",
            startDate = "24 Jun 2024",
            lastDate = "15 Sep 2024",
            officialFee = "₹100",
            serviceCharge = "₹50",
            jobUrl = "https://ssc.gov.in",
            notificationUrl = "https://ssc.gov.in/notif",
            importantInstructions = "Ensure all documents are clear before upload.",
            source = "Official SSC Portal",
            status = JobStatus.ACTIVE,
            lastVerified = "26 Aug 2026, 09:30 AM",
            isActive = true
        ),
        StandardizedJob(
            id = "JOB-106",
            title = "Specialist Officer",
            organization = "SBI",
            description = "Recruitment of Junior Associates and Specialist Officers in SBI.",
            department = "Banking",
            type = "Government",
            location = "India",
            qualification = "B.Tech",
            branch = "CSE/IT",
            ageLimit = "18-27",
            categoryRules = "Min 7.0 CGPA",
            experience = "1 Year",
            salary = "₹65k+",
            startDate = "15 Aug 2024",
            lastDate = "25 Aug 2024",
            officialFee = "₹750",
            serviceCharge = "₹50",
            jobUrl = "https://sbi.co.in",
            notificationUrl = "https://sbi.co.in/careers",
            importantInstructions = "Keep mobile ready for OTP during payment.",
            source = "SBI Careers",
            status = JobStatus.ACTIVE,
            lastVerified = "26 Aug 2026, 10:15 AM",
            isActive = true
        ),
        StandardizedJob(
            id = "JOB-107",
            title = "State Service",
            organization = "BPSC",
            description = "Bihar Public Service Commission - Combined Competitive Exam.",
            department = "Bihar Govt",
            type = "Government",
            location = "Bihar",
            qualification = "Degree",
            branch = "Any",
            ageLimit = "21-37",
            categoryRules = "Bihar Resident Only",
            experience = "Fresher",
            salary = "Level 9",
            startDate = "01 Aug 2024",
            lastDate = "30 Aug 2024",
            officialFee = "₹600",
            serviceCharge = "₹50",
            jobUrl = "https://bpsc.bih.nic.in",
            notificationUrl = "https://bpsc.bih.nic.in/notif",
            importantInstructions = "Offline signature scan required.",
            source = "BPSC Portal",
            status = JobStatus.ACTIVE,
            lastVerified = "25 Aug 2026, 11:00 AM",
            isActive = true
        )
    )

    fun addJob(job: StandardizedJob) {
        jobs.add(0, job)
    }

    fun updateJob(updatedJob: StandardizedJob) {
        val index = jobs.indexOfFirst { it.id == updatedJob.id }
        if (index != -1) {
            jobs[index] = updatedJob
        }
    }

    fun deleteJob(jobId: String) {
        jobs.removeAll { it.id == jobId }
    }

    fun markVerified(jobId: String) {
        val index = jobs.indexOfFirst { it.id == jobId }
        if (index != -1) {
            val job = jobs[index]
            jobs[index] = job.copy(lastVerified = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date()))
        }
    }
}
