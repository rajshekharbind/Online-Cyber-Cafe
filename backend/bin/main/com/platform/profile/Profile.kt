package com.platform.profile

import com.platform.user.User
import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name = "student_profiles")
class StudentProfile(
    @Id
    val id: UUID? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    val user: User,

    var firstName: String? = null,
    var lastName: String? = null,
    var fatherName: String? = null,
    var motherName: String? = null,
    var dob: LocalDate? = null,
    var gender: String? = null,
    var nationality: String? = "Indian",
    var maritalStatus: String? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    var currentAddress: Address? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    var permanentAddress: Address? = null,

    var category: String? = null,
    var subCategory: String? = null,
    var isEws: Boolean = false,
    var isPwd: Boolean = false,
    var isExServiceman: Boolean = false
)

@Entity
@Table(name = "addresses")
class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,
    var villageTown: String? = null,
    var district: String? = null,
    var state: String? = null,
    var pinCode: String? = null
)

@Entity
@Table(name = "education_records")
class EducationRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne
    @JoinColumn(name = "profile_id")
    val profile: StudentProfile,

    var level: String? = null, // e.g., 10th, 12th, Graduation
    var boardUniversity: String? = null,
    var institution: String? = null,
    var passingYear: Int? = null,
    var rollNumber: String? = null,
    var percentage: Double? = null,
    var cgpa: Double? = null
)
