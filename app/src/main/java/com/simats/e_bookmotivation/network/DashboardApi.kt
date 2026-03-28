package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.DashboardResponse
import com.simats.e_bookmotivation.network.models.DailyBoostResponse
import com.simats.e_bookmotivation.network.models.UserBadgeResponse
import com.simats.e_bookmotivation.network.models.BadgeStatusResponse
import com.simats.e_bookmotivation.network.models.JournalEntryResponse
import com.simats.e_bookmotivation.network.models.JournalEntryRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface DashboardApi {
    @GET("api/users/dashboard/")
    suspend fun getDashboard(@Header("Authorization") token: String): Response<DashboardResponse>

    @GET("api/users/dashboard/daily-boost/")
    suspend fun getDailyBoost(@Header("Authorization") token: String): Response<DailyBoostResponse>

    @GET("api/users/dashboard/badges/")
    suspend fun getBadges(@Header("Authorization") token: String): Response<List<BadgeStatusResponse>>

    @GET("api/users/dashboard/journal/")
    suspend fun getJournalEntries(@Header("Authorization") token: String): Response<com.simats.e_bookmotivation.network.models.JournalWrapperResponse>

    @POST("api/users/dashboard/journal/")
    suspend fun createJournalEntry(
        @Header("Authorization") token: String, 
        @Body request: JournalEntryRequest
    ): Response<JournalEntryResponse>
}
