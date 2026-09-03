package com.platform.auth

import com.platform.user.Role

data class RegisterRequest(
    val email: String,
    val password: String,
    val role: Role
)

data class AuthenticationRequest(
    val email: String,
    val password: String
)

data class AuthenticationResponse(
    val token: String
)
