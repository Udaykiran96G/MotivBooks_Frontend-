package com.simats.e_bookmotivation.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun updateGoal(
        newGoalTitle: String,
        newGoalSubtitle: String,
        totalBooks: Int,
        goalType: String,
        goalUnit: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    _isLoading.value = false
                    return@launch
                }

                // GET current progress to preserve existing fields
                val getResponse = RetrofitClient.progressApi.getProgress("Bearer $token")
                if (!getResponse.isSuccessful || getResponse.body() == null) {
                    _error.value = "Failed to fetch current progress: ${getResponse.code()}"
                    _isLoading.value = false
                    return@launch
                }
                val currentProgress = getResponse.body()!!

                // Reset books_completed to 0 only if it's a different goal title
                val newBooksCompleted = if (currentProgress.active_goal_title != newGoalTitle) 0
                                        else currentProgress.active_goal_books_completed

                val updatedProgress = currentProgress.copy(
                    active_goal_title = newGoalTitle,
                    active_goal_subtitle = newGoalSubtitle,
                    active_goal_total_books = totalBooks,
                    active_goal_type = goalType,
                    active_goal_unit = goalUnit,
                    active_goal_books_completed = newBooksCompleted
                )

                val putResponse = RetrofitClient.progressApi.updateProgress("Bearer $token", updatedProgress)
                if (putResponse.isSuccessful) {
                    _successMessage.value = "Goal set: $newGoalTitle"
                    onSuccess()
                } else {
                    val errorBody = putResponse.errorBody()?.string()
                    _error.value = "Failed to update goal: ${putResponse.code()} $errorBody"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
