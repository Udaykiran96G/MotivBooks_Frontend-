package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {
    @GET("api/users/profile/")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @PATCH("api/users/profile/")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ): Response<UserProfileResponse>

    @GET("api/users/profile/preferences/")
    suspend fun getPreferences(@Header("Authorization") token: String): Response<ReadingPreferenceResponse>

    @PATCH("api/users/profile/preferences/")
    suspend fun updatePreferences(
        @Header("Authorization") token: String,
        @Body prefs: ReadingPreferenceResponse
    ): Response<ReadingPreferenceResponse>

    @GET("api/users/profile/subscription/")
    suspend fun getSubscription(@Header("Authorization") token: String): Response<SubscriptionResponse>

    @GET("api/users/profile/growth-stats/")
    suspend fun getGrowthStats(@Header("Authorization") token: String): Response<GrowthStatsResponse>

    @GET("api/users/profile/detail/")
    suspend fun getProfileDetail(@Header("Authorization") token: String): Response<ProfileDetailResponse>

    @DELETE("api/users/profile/delete/")
    suspend fun deleteAccount(@Header("Authorization") token: String): Response<Unit>
}
