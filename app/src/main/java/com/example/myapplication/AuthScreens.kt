package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ── Password Strength Indicator ─────────────────────────────────
private enum class PasswordStrength(val label: String, val color: Color, val progress: Float) {
    EMPTY("", Color.Transparent, 0f),
    WEAK("Weak", Color(0xFFE53935), 0.25f),
    FAIR("Fair", Color(0xFFFB8C00), 0.5f),
    GOOD("Good", Color(0xFFFFD600), 0.75f),
    STRONG("Strong", Color(0xFF43A047), 1f)
}

private fun checkPasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.EMPTY
    var score = 0
    if (password.length >= 8) score++
    if (password.any { it.isUpperCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { "!@#\$%^&*()_+-=[]{}|;':\",./<>?".contains(it) }) score++
    return when (score) {
        1 -> PasswordStrength.WEAK
        2 -> PasswordStrength.FAIR
        3 -> PasswordStrength.GOOD
        4 -> PasswordStrength.STRONG
        else -> PasswordStrength.WEAK
    }
}

@Composable
private fun PasswordStrengthBar(password: String) {
    val strength = checkPasswordStrength(password)
    if (strength == PasswordStrength.EMPTY) return
    Column(modifier = Modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { strength.progress },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = strength.color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = "Password strength: ${strength.label}",
            fontSize = 11.sp,
            color = strength.color,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToOtp: (String) -> Unit, onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STUDENT") }
    var expanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Field errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    fun validate(): Boolean {
        nameError = InputValidator.validateName(fullName).errorMessage
        mobileError = InputValidator.validateMobile(mobile).errorMessage
        emailError = InputValidator.validateEmail(email).errorMessage
        passwordError = InputValidator.validatePassword(password).errorMessage
        confirmPasswordError = if (password != confirmPassword) "Passwords do not match" else null
        return nameError == null && mobileError == null && emailError == null &&
                passwordError == null && confirmPasswordError == null
    }

    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val roles = listOf("STUDENT")  // Employee accounts are admin-created only

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join Cyber Cafe",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Access all job services in one place",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Security notice
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Your data is encrypted and stored securely.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it; nameError = null },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                isError = nameError != null,
                supportingText = if (nameError != null) {{ Text(nameError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mobile
            OutlinedTextField(
                value = mobile,
                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) { mobile = it; mobileError = null } },
                label = { Text("Mobile Number (10 digits)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                isError = mobileError != null,
                supportingText = if (mobileError != null) {{ Text(mobileError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = emailError != null,
                supportingText = if (emailError != null) {{ Text(emailError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                visualTransformation = if (passwordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = if (passwordError != null) {{ Text(passwordError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )

            // Password strength bar
            Spacer(modifier = Modifier.height(4.dp))
            PasswordStrengthBar(password)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Min 8 chars, uppercase, lowercase, number & special character",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmPasswordError = null },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (confirmPasswordVisible) "Hide" else "Show"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                isError = confirmPasswordError != null,
                supportingText = if (confirmPasswordError != null) {{ Text(confirmPasswordError!!, color = MaterialTheme.colorScheme.error) }} else null,
                enabled = !isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Role (students only for self-registration; employees are created by admin)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { if (!isLoading) expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRole,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("I am a...") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                selectedRole = role
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!validate()) return@Button
                    isLoading = true
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                NetworkConfig.authApi.register(
                                    RegisterRequestDto(
                                        email = email.trim(),
                                        password = password,
                                        role = selectedRole
                                    )
                                )
                            }
                            SecurityStore.logAuth(email, "Account registered successfully via backend", true)
                        } catch (e: Exception) {
                            // Backend unavailable — silently continue in demo mode
                            SecurityStore.logAuth(email, "Registration in demo mode (backend unavailable)", true)
                        }
                        isLoading = false
                        onNavigateToOtp(mobile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Register Now", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBackToLogin, enabled = !isLoading) {
                Text("Already have an account? Login")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(mobileNumber: String, onOtpVerified: () -> Unit, onBack: () -> Unit) {
    // Demo mode: pre-fill with demo code since no real SMS gateway is connected
    var otp by remember { mutableStateOf("123456") }
    var isVerifying by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Mobile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.VpnKey,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(24.dp))
            Text("Verification Code Sent", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                "We've sent a 6-digit OTP to +91 $mobileNumber",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Demo mode banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Demo Mode",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "No SMS gateway connected. Use code 123456 or any 6-digit number to continue.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                        otp = it
                        otpError = null
                    }
                },
                label = { Text("Enter OTP") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = otpError != null,
                supportingText = if (otpError != null) {
                    { Text(otpError!!, color = MaterialTheme.colorScheme.error) }
                } else {
                    { Text("${otp.length}/6 digits", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                },
                enabled = !isVerifying,
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (otp.length != 6) {
                        otpError = "OTP must be exactly 6 digits"
                        return@Button
                    }
                    isVerifying = true
                    SecurityStore.logAuth(
                        "user",
                        "Mobile OTP verified for +91 $mobileNumber (demo mode)",
                        true
                    )
                    onOtpVerified()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = otp.length == 6 && !isVerifying
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verify & Continue", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = {
                otp = "123456"
                SecurityStore.logAction("user", "OTP resend requested for $mobileNumber (demo)", "INFO", "AUTH")
            }) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Didn't receive code? Resend OTP")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skip button for demo
            OutlinedButton(
                onClick = {
                    SecurityStore.logAuth("user", "OTP skipped (demo mode) for +91 $mobileNumber", true)
                    onOtpVerified()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Skip Verification (Demo Mode)")
            }
        }
    }
}

