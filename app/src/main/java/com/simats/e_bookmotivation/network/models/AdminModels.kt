package com.simats.e_bookmotivation.network.models

import com.google.gson.annotations.SerializedName

data class AdminBookRequest(
    val title: String,
    val author: String,
    val description: String = "",
    val category: String = "TRENDING",
    val genre: String = "",
    @SerializedName("cover_url") val coverUrl: String = "",
    @SerializedName("is_premium") val isPremium: Boolean = false
)

data class AdminBookResponse(
    val id: Int,
    val title: String,
    val author: String,
    val description: String?,
    val category: String,
    val genre: String?,
    @SerializedName("cover_url") val coverUrl: String?,
    @SerializedName("is_premium") val isPremium: Boolean
)

data class AdminChapterRequest(
    val title: String,
    val content: String,
    val order: Int
)

data class AdminChapterResponse(
    val id: Int,
    val title: String,
    val content: String,
    val order: Int
)

data class AdminDeleteResponse(
    val message: String
)
