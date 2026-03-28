package com.simats.e_bookmotivation.ui.screens.dashboard

import androidx.compose.runtime.Immutable

@Immutable
data class DashboardUiState(
    val date: String = "Loading...",
    val userName: String = "...",
    val streakDays: Int = 0,
    val goalTitle: String = "...",
    val goalSubtitle: String = "...",
    val goalUnit: String = "books",
    val goalBooksRead: Int = 0,
    val goalTotalBooks: Int = 0,
    val currentBook: CurrentBookState? = null,
    val topBooks: List<BookSummary> = emptyList(),
    val monthBooks: List<BookSummary> = emptyList(),
    val trendingBooks: List<BookSummary> = emptyList(),
    val communityInspiration: CommunityState = CommunityState(emptyList(), "Start reading to see community activity"),
    val aiRecommendation: AiPickState = AiPickState("Welcome!", "Start reading to get personalized recommendations.", "✨"),
    val badges: List<BadgeUiState> = emptyList(),
    val dailyBoost: com.simats.e_bookmotivation.network.models.DailyBoostResponse? = null,
    val totalNotesTaken: Int = 0
)

@Immutable
data class BadgeUiState(
    val title: String,
    val dateEarned: String,
    val iconName: String,
    val tintColor: String,
    val bgColor: String
)

@Immutable
data class CurrentBookState(
    val id: Int = 1,
    val title: String = "",
    val author: String = "",
    val progress: Float = 0f,
    val isPremium: Boolean = false,
    val currentChapter: Int = 1,
    val totalChapters: Int = 0
)

@Immutable
data class CommunityState(
    val avatarUrls: List<String> = listOf("url1", "url2", "url3"),
    val message: String = "Join 1,240 others reading today"
)

@Immutable
data class AiPickState(
    val title: String = "Mindset",
    val description: String = "Based on your interest in growth and habits.",
    val sparkleIcon: String = "✨"
)

@Immutable
data class BookSummary(
    val id: Int,
    val title: String,
    val author: String,
    val coverUrl: String
)
