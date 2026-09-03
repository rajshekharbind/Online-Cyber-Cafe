package com.platform.profile

import java.time.LocalDate

data class ProfileUpdateRequest(
    val firstName: String?,
    val lastName: String?,
    val fatherName: String?,
    val motherName: String?,
    val dob: LocalDate?,
    val gender: String?,
    val nationality: String?,
    val maritalStatus: String?,
    val currentAddress: AddressDto?,
    val permanentAddress: AddressDto?,
    val category: String?,
    val subCategory: String?,
    val isEws: Boolean,
    val isPwd: Boolean,
    val isExServiceman: Boolean
)

data class AddressDto(
    val villageTown: String?,
    val district: String?,
    val state: String?,
    val pinCode: String?
)
