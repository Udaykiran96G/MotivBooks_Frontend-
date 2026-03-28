package com.simats.e_bookmotivation.network.models

data class UserProfileResponse(
    val name: String,
    val email: String,
    val phone: String?,
    val dob: String?,
    val location: String?,
    val avatar_url: String?,
    val member_since: String
)

data class ReadingPreferenceResponse(
    val font_size: Int,
    val theme: String,
    val language: String,
    val auto_save_highlights: Boolean,
    val interests: String = "",
    val improvement_goals: String = "",
    val reading_style: String = ""
)

data class SubscriptionResponse(
    val plan_type: String,
    val status: String,
    val expiry_date: String?
)

data class GrowthStatsResponse(
    val streakDays: Int,
    val booksRead: Int,
    val quotesSaved: Int,
    val goalProgress: Int,
    val weeklyImprovement: Int,
    val totalReadingTime: String,
    val pagesRead: Int,
    val notesTaken: Int,
    val dailyProgress: List<Double>

)

data class ProfileDetailResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: UserProfileResponse,
    val preferences: ReadingPreferenceResponse,
    val subscription: SubscriptionResponse,
    val progress: DashboardProgressModel, // Reusing if possible or define here
    val analytics: ReadingAnalyticsModel
)

data class DashboardProgressModel(
    val current_streak: Int,
    val active_goal_title: String,
    val active_goal_books_completed: Int,
    val active_goal_total_books: Int,
    val total_books_read: Int,
    val total_quotes_saved: Int,
    val total_notes_taken: Int = 0
)

data class ReadingAnalyticsModel(
    val weekly_improvement_percentage: Int,
    val daily_average_minutes: Int,
    val pages_read: Int
)

data class ProfileUpdateRequest(
    val name: String? = null,
    val phone: String? = null,
    val dob: String? = null,
    val location: String? = null
)
