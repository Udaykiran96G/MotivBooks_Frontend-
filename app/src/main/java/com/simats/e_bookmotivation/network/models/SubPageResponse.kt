package com.simats.e_bookmotivation.network.models

data class GoalDetailsResponse(
    val id: Int = 0,
    val deadline: String? = null,
    val reflections_written: Int = 0,
    val challenges_done: Int = 0
)

data class ReadingAnalyticsResponse(
    val id: Int = 0,
    val weekly_improvement_percentage: Int = 0,
    val daily_average_minutes: Int = 0,
    val longest_session_minutes: Int = 0,
    val mon_progress: Float = 0f,
    val tue_progress: Float = 0f,
    val wed_progress: Float = 0f,
    val thu_progress: Float = 0f,
    val fri_progress: Float = 0f,
    val sat_progress: Float = 0f,
    val sun_progress: Float = 0f
)

data class SavedQuoteResponse(
    val id: Int = 0,
    val quote: String = "",
    val author: String = "",
    val book: String = "",
    val date_saved: String = ""
)

data class MoodGraphResponse(
    val mood_values: List<Float> = emptyList(),
    val days: List<String> = emptyList(),
    val has_data: Boolean = false
)
