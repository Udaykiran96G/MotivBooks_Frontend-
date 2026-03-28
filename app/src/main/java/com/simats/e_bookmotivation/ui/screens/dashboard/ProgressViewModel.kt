package com.simats.e_bookmotivation.ui.screens.dashboard

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ProgressResponse
import com.simats.e_bookmotivation.network.models.MoodGraphResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val _progressData = MutableStateFlow<ProgressResponse?>(null)
    val progressData: StateFlow<ProgressResponse?> = _progressData.asStateFlow()

    private val _moodData = MutableStateFlow<MoodGraphResponse?>(null)
    val moodData: StateFlow<MoodGraphResponse?> = _moodData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchProgress(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token == null) {
            _error.value = "Authentication token not found."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.progressApi.getProgress("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _progressData.value = response.body()
                } else {
                    _error.value = "Failed to fetch progress: ${response.code()} ${response.message()}"
                    Log.e("ProgressViewModel", "Error: ${response.errorBody()?.string()}")
                }

                // Fetch mood graph data from journal entries
                val moodResponse = RetrofitClient.subPageApi.getMoodGraph("Bearer $token")
                if (moodResponse.isSuccessful && moodResponse.body() != null) {
                    _moodData.value = moodResponse.body()
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
                Log.e("ProgressViewModel", "Exception", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
