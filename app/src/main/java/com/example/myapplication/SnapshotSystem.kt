package com.example.myapplication

/**
 * Preservation of Historical Application Data (Req 40 & 41)
 * Master Profile ≠ Historical Application Data
 * 
 * When an application is created, store the information required for that application.
 * When submitted, preserve the final submitted state to prevent historical data corruption.
 */
data class ApplicationSnapshot(
    val studentName: String,
    val fatherName: String,
    val motherName: String,
    val dob: String,
    val gender: String,
    val mobileNumber: String,
    val email: String,
    val nationality: String,
    val maritalStatus: String,
    
    val currentAddress: String,
    val permanentAddress: String,
    val villageTown: String,
    val district: String,
    val state: String,
    val pinCode: String,
    
    val category: String,
    val subCategory: String,
    val ewsInfo: String,
    val pwdInfo: String,
    val exServiceman: String,

    val qualificationsAtThatTime: List<String>,
    val submittedAt: String
)

data class PersistentApplication(
    val appId: String,
    val jobTitle: String,
    val status: String,
    val snapshot: ApplicationSnapshot
)

object ApplicationStore {
    private val submittedApplications = mutableListOf<PersistentApplication>()

    fun saveApplication(
        appId: String, 
        jobTitle: String, 
        personalInfo: Map<String, String>,
        addressInfo: Map<String, String>,
        categoryInfo: Map<String, String>,
        qualifications: List<String>
    ) {
        val snapshot = ApplicationSnapshot(
            studentName = personalInfo["Full Name"] ?: "N/A",
            fatherName = personalInfo["Father's Name"] ?: "N/A",
            motherName = personalInfo["Mother's Name"] ?: "N/A",
            dob = personalInfo["DOB"] ?: "N/A",
            gender = personalInfo["Gender"] ?: "N/A",
            mobileNumber = personalInfo["Mobile Number"] ?: "N/A",
            email = personalInfo["Email"] ?: "N/A",
            nationality = personalInfo["Nationality"] ?: "N/A",
            maritalStatus = personalInfo["Marital Status"] ?: "N/A",
            
            currentAddress = addressInfo["Current Address"] ?: "N/A",
            permanentAddress = addressInfo["Permanent Address"] ?: "N/A",
            villageTown = addressInfo["Village/Town"] ?: "N/A",
            district = addressInfo["District"] ?: "N/A",
            state = addressInfo["State"] ?: "N/A",
            pinCode = addressInfo["PIN Code"] ?: "N/A",
            
            category = categoryInfo["Category"] ?: "N/A",
            subCategory = categoryInfo["Sub-category"] ?: "N/A",
            ewsInfo = categoryInfo["EWS Information"] ?: "N/A",
            pwdInfo = categoryInfo["PwD Information"] ?: "N/A",
            exServiceman = categoryInfo["Ex-serviceman"] ?: "N/A",
            
            qualificationsAtThatTime = qualifications.toList(), // Deep copy
            submittedAt = java.text.SimpleDateFormat("dd MMM yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())
        )
        
        submittedApplications.add(PersistentApplication(appId, jobTitle, "Submitted", snapshot))
        
        // Log the creation of the immutable snapshot
        SecurityStore.logAction(
            actor = personalInfo["Email"] ?: "student",
            action = "Application snapshot created for $appId ($jobTitle)",
            severity = "INFO",
            category = "SYSTEM"
        )
    }

    fun getApplications() = submittedApplications.toList()
    
    fun getSnapshotForApp(appId: String) = submittedApplications.find { it.appId == appId }?.snapshot
}
