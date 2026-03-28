package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.ProgressResponse
import com.simats.e_bookmotivation.network.models.GrowthStatsResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Header
import retrofit2.http.POST

interface ProgressApi {

    @GET("api/users/progress/")
    suspend fun getProgress(@Header("Authorization") token: String): Response<ProgressResponse>

    @PUT("api/users/progress/")
    suspend fun updateProgress(
        @Header("Authorization") token: String, 
        @Body progress: ProgressResponse
    ): Response<ProgressResponse>

    @GET("api/users/profile/growth-stats/")
    suspend fun getGrowthStats(@Header("Authorization") token: String): Response<GrowthStatsResponse>
    
    @POST("api/users/books/track-progress/")
    suspend fun updateReadingProgress(
        @Header("Authorization") token: String,
        @Body progressData: Map<String, Int>
    ): Response<Map<String, Int>>

}

