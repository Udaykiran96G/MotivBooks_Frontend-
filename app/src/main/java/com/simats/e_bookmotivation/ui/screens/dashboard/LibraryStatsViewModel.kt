package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryStatsState(
    val streakDays: Int = 0,
    val booksRead: Int = 0,
    val quotesSaved: Int = 0,
    val goalProgress: Int = 0,
    val weeklyImprovement: Int = 0,
    val totalReadingTime: String = "0h 0m",
    val pagesRead: Int = 0,
    val notesTaken: Int = 0,
    val dailyProgress: List<Double> = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
)

class LibraryStatsViewModel(application: Application) : AndroidViewModel(application) {

    private val _stats = MutableStateFlow(LibraryStatsState())
    val stats: StateFlow<LibraryStatsState> = _stats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchStats()
    }

    fun fetchStats() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.progressApi.getGrowthStats("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _stats.value = LibraryStatsState(
                        streakDays = body.streakDays,
                        booksRead = body.booksRead,
                        quotesSaved = body.quotesSaved,
                        goalProgress = body.goalProgress,
                        weeklyImprovement = body.weeklyImprovement,
                        totalReadingTime = body.totalReadingTime,
                        pagesRead = body.pagesRead,
                        notesTaken = body.notesTaken,
                        dailyProgress = body.dailyProgress
                    )
                    _error.value = null
                } else {
                    _error.value = "Failed to load stats."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getAuthToken(): String? {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
}
