package com.simats.e_bookmotivation.network.models

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("cover_url") val coverUrl: String?,
    @SerializedName("category") val category: String,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("description") val description: String?,
    @SerializedName("genre") val genre: String?
)

data class UserBookResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("book_details") val bookDetails: BookResponse,
    @SerializedName("status") val status: String,
    @SerializedName("date_completed") val dateCompleted: String?,
    @SerializedName("time_spent_minutes") val timeSpentMinutes: Int
)

data class ChapterResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("book") val bookId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("order") val order: Int
)

