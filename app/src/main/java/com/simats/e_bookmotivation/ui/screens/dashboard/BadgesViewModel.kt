package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.BadgeStatusResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BadgesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<List<BadgeStatusResponse>>(emptyList())
    val uiState: StateFlow<List<BadgeStatusResponse>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchBadges()
    }

    fun fetchBadges() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }
                
                val response = RetrofitClient.dashboardApi.getBadges("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = response.body()!!
                    _error.value = null
                } else {
                    _error.value = "Failed to load badges."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
