package com.simats.e_bookmotivation.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class ReadingPreferences(
    val fontSize: Int = 18,
    val theme: String = "Light", // "Light", "Sepia", "Dark"
    val language: String = "English",
    val autoSaveHighlights: Boolean = true
)

val LocalReadingPreferences = compositionLocalOf<MutableState<ReadingPreferences>> {
    error("No ReadingPreferences provided")
}
