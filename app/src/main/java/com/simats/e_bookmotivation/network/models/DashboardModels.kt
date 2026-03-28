package com.simats.e_bookmotivation.network.models

data class DashboardResponse(
    val date: String,
    val userName: String,
    val streakDays: Int,
    val goalTitle: String,
    val goalSubtitle: String,
    val goalType: String = "BOOKS",
    val goalUnit: String = "books",
    val goalBooksRead: Int,
    val goalTotalBooks: Int,
    val total_notes_taken: Int = 0,
    val currentBook: CurrentBookModel?,
    val topBooks: List<BookSummaryModel>,
    val monthBooks: List<BookSummaryModel>,
    val trendingBooks: List<BookSummaryModel>,
    val communityInspiration: CommunityModel,
    val aiRecommendation: AiPickModel,
    val badges: List<UserBadgeResponse>,
    val daily_boost: DailyBoostResponse? = null,
    val unreadNotificationCount: Int = 0
)

data class CurrentBookModel(
    val id: Int,
    val title: String,
    val author: String,
    val progress: Float,
    val isPremium: Boolean,
    val currentChapter: Int = 1,
    val totalChapters: Int = 0
)

data class BookSummaryModel(
    val id: Int,
    val title: String,
    val author: String,
    val coverUrl: String
)

data class CommunityModel(
    val avatarUrls: List<String>,
    val message: String
)

data class AiPickModel(
    val title: String,
    val description: String,
    val sparkleIcon: String
)

data class DailyBoostResponse(
    val id: Int,
    val date: String,
    val insight_title: String,
    val quote_text: String,
    val quote_author: String,
    val article_title: String,
    val article_preview: String,
    val ai_reflection: String,
    val user: Int? = null
)

data class UserBadgeResponse(
    val id: Int,
    val title: String,
    val date_earned: String,
    val icon_name: String,
    val tint_color: String,
    val bg_color: String
)

data class BadgeStatusResponse(
    val id: String,
    val title: String,
    val description: String,
    val icon_name: String,
    val tint_color: String,
    val bg_color: String,
    val unlocked: Boolean,
    val progress: Float,
    val current_value: Int,
    val target_value: Int
)

data class JournalWrapperResponse(
    val entries: List<JournalEntryResponse>,
    val today_prompt: String,
    val today_date: String
)

data class JournalEntryResponse(
    val id: Int,
    val title: String,
    val content: String,
    val date_created: String,
    val mood: String = "okay",
    val prompt: String = ""
)

data class JournalEntryRequest(
    val title: String,
    val content: String,
    val mood: String = "okay",
    val prompt: String = ""
)
