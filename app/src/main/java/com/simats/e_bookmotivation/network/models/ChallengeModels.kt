package com.simats.e_bookmotivation.network.models

import com.google.gson.annotations.SerializedName

data class ChallengeResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("reward_xp") val rewardXp: Int,
    @SerializedName("is_completed") val isCompleted: Boolean
)

data class ChallengeUpdateResponse(
    @SerializedName("status") val status: String,
    @SerializedName("is_completed") val isCompleted: Boolean
)
