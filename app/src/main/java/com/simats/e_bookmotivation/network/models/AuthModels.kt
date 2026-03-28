package com.simats.e_bookmotivation.network.models

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val refresh: String,
    val access: String,
    val is_staff: Boolean = false
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val phone: String,
    val is_staff: Boolean = false
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)

data class RegisterResponse(
    val user: UserDto,
    val refresh: String,
    val access: String
)

// Forgot Password OTP Models
data class ForgotPasswordRequest(
    val email: String
)

data class ForgotPasswordResponse(
    val message: String? = null,
    val error: String? = null
)

data class VerifyOTPRequest(
    val email: String,
    val otp: String
)

data class VerifyOTPResponse(
    val message: String? = null,
    val error: String? = null
)

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val new_password: String
)

data class ResetPasswordResponse(
    val message: String? = null,
    val error: String? = null
)

data class ChangePasswordRequest(
    val old_password: String,
    val new_password: String,
    val confirm_password: String
)

data class ChangePasswordResponse(
    val message: String? = null,
    val error: String? = null
)

