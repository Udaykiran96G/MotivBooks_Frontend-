package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.AICoachInsightResponse
import com.simats.e_bookmotivation.network.models.AICoachChatRequest
import com.simats.e_bookmotivation.network.models.AICoachChatResponse
import com.simats.e_bookmotivation.network.models.AICoachStrategyResponse
import com.simats.e_bookmotivation.network.models.AISummaryRequest
import com.simats.e_bookmotivation.network.models.AISummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AICoachApi {
    @GET("api/users/ai-coach/insight/")
    suspend fun getAICoachInsight(
        @Header("Authorization") token: String
    ): Response<AICoachInsightResponse>

    @POST("api/users/ai-coach/chat/")
    suspend fun postChat(
        @Header("Authorization") token: String,
        @Body request: AICoachChatRequest
    ): Response<AICoachChatResponse>

    @POST("api/users/ai-coach/strategy/")
    suspend fun generateStrategy(
        @Header("Authorization") token: String
    ): Response<AICoachStrategyResponse>

    @POST("api/users/ai-coach/summary/")
    suspend fun getAISummary(
        @Header("Authorization") token: String,
        @Body request: AISummaryRequest
    ): Response<AISummaryResponse>
}
