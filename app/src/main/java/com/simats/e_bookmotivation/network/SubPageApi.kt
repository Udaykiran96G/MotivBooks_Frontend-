package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.GoalDetailsResponse
import com.simats.e_bookmotivation.network.models.ReadingAnalyticsResponse
import com.simats.e_bookmotivation.network.models.SavedQuoteResponse
import com.simats.e_bookmotivation.network.models.MoodGraphResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface SubPageApi {
    @GET("api/users/progress/goal-details/")
    suspend fun getGoalDetails(
        @Header("Authorization") token: String
    ): Response<GoalDetailsResponse>

    @PUT("api/users/progress/goal-details/")
    suspend fun updateGoalDetails(
        @Header("Authorization") token: String,
        @Body request: GoalDetailsResponse
    ): Response<GoalDetailsResponse>

    @GET("api/users/progress/reading-analytics/")
    suspend fun getReadingAnalytics(
        @Header("Authorization") token: String
    ): Response<ReadingAnalyticsResponse>

    @PUT("api/users/progress/reading-analytics/")
    suspend fun updateReadingAnalytics(
        @Header("Authorization") token: String,
        @Body request: ReadingAnalyticsResponse
    ): Response<ReadingAnalyticsResponse>

    @GET("api/users/progress/saved-quotes/")
    suspend fun getSavedQuotes(
        @Header("Authorization") token: String
    ): Response<List<SavedQuoteResponse>>

    @POST("api/users/progress/saved-quotes/")
    suspend fun addSavedQuote(
        @Header("Authorization") token: String,
        @Body request: Map<String, String>
    ): Response<SavedQuoteResponse>

    @DELETE("api/users/progress/saved-quotes/{id}/")
    suspend fun deleteSavedQuote(
        @Header("Authorization") token: String,
        @Path("id") quoteId: Int
    ): Response<Unit>

    @POST("api/users/books/track-open/")
    suspend fun trackBookOpen(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<Map<String, @JvmSuppressWildcards Any>>

    @GET("api/users/progress/mood-graph/")
    suspend fun getMoodGraph(
        @Header("Authorization") token: String
    ): Response<MoodGraphResponse>
}
