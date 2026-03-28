package com.simats.e_bookmotivation.network.models

import com.google.gson.annotations.SerializedName

data class AICoachInsightResponse(
    @SerializedName("observation") val observation: String,
    @SerializedName("bookTitle") val bookTitle: String,
    @SerializedName("insightText") val insightText: String,
    @SerializedName("actionSteps") val actionSteps: List<AICoachActionStep>
)

data class AICoachActionStep(
    @SerializedName("title") val title: String,
    @SerializedName("subtitle") val subtitle: String,
    @SerializedName("desc") val desc: String
)

data class AICoachChatRequest(
    val query: String
)

data class AICoachChatResponse(
    val query: String,
    val response: String,
    val currentBook: String
)

data class AICoachStrategyResponse(
    val title: String,
    val description: String,
    val weeks: List<AICoachWeek>
)

data class AICoachWeek(
    val week: Int,
    val title: String,
    val tasks: List<String>
)

data class AISummaryRequest(
    val content: String
)

data class AISummaryResponse(
    val summary: String
)
