package com.simats.e_bookmotivation.network.models

data class ProgressResponse(
    val id: Int = 0,
    val user: Int? = null,
    val current_streak: Int = 0,
    val active_goal_title: String = "Set a Goal",
    val active_goal_subtitle: String = "",
    val active_goal_type: String = "BOOKS",
    val active_goal_unit: String = "books",
    val active_goal_books_completed: Int = 0,
    val active_goal_total_books: Int = 1,
    val total_books_read: Int = 0,
    val total_hours_read: Int = 0,
    val total_quotes_saved: Int = 0,
    val total_highlights_made: Int = 0,
    val total_notes_taken: Int = 0,
    val mood_before_reading: String = "",
    val mood_after_reading: String = "",
    val weekly_digest_text: String = "",
    val weekly_digest_date_range: String = "",
    val current_book_title: String? = null,
    val current_book_author: String? = null,
    val current_book_progress: Float = 0f,
    val current_book_is_premium: Boolean = false
)




