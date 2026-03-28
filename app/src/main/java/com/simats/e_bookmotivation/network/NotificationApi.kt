package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationApi {
    @GET("api/users/notifications/")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationResponse>>

    @POST("api/users/notifications/")
    suspend fun markAllAsRead(
        @Header("Authorization") token: String
    ): Response<Map<String, String>>
}
