package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ReadingAnalyticsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReadingAnalyticsViewModel(application: Application) : AndroidViewModel(application) {
    private val _analyticsData = MutableStateFlow<ReadingAnalyticsResponse?>(null)
    val analyticsData: StateFlow<ReadingAnalyticsResponse?> = _analyticsData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchReadingAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val response = RetrofitClient.subPageApi.getReadingAnalytics("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        _analyticsData.value = response.body()
                    } else {
                        _error.value = "Failed to load reading analytics: ${response.code()}"
                    }
                } else {
                    _error.value = "No authentication token found"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
