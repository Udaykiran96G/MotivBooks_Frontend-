package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("api/users/login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/users/admin/register/")
    suspend fun adminRegister(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/users/admin/login/")
    suspend fun adminLogin(@Body request: LoginRequest): Response<LoginResponse>


    @POST("api/users/forgot-password/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("api/users/verify-otp/")
    suspend fun verifyOtp(@Body request: VerifyOTPRequest): Response<VerifyOTPResponse>

    @POST("api/users/reset-password/")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>

    @POST("api/users/change-password/")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ChangePasswordResponse>

    @POST("api/users/login/send-otp/")
    suspend fun sendLoginOtp(@Body request: Map<String, String>): Response<Map<String, String>>

    @POST("api/users/login/verify-otp/")
    suspend fun loginWithOtp(@Body request: Map<String, String>): Response<LoginResponse>

}
