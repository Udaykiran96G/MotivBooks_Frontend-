package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.GrowthStatsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GrowthStatsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<GrowthStatsResponse?>(null)
    val uiState: StateFlow<GrowthStatsResponse?> = _uiState.asStateFlow()

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
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }
                
                val response = RetrofitClient.profileApi.getGrowthStats("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Failed to load growth stats: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
