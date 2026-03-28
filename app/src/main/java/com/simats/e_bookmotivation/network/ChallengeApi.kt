package com.simats.e_bookmotivation.network

import com.simats.e_bookmotivation.network.models.ChallengeResponse
import com.simats.e_bookmotivation.network.models.ChallengeUpdateResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ChallengeApi {
    @GET("api/users/challenges/")
    suspend fun getChallenges(
        @Header("Authorization") token: String
    ): Response<List<ChallengeResponse>>

    @POST("api/users/challenges/{id}/update/")
    suspend fun updateChallenge(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): Response<ChallengeUpdateResponse>
}
