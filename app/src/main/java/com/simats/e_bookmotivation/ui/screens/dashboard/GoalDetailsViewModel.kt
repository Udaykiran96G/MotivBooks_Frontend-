package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.GoalDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalDetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val _goalDetailsData = MutableStateFlow<GoalDetailsResponse?>(null)
    val goalDetailsData: StateFlow<GoalDetailsResponse?> = _goalDetailsData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchGoalDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val response = RetrofitClient.subPageApi.getGoalDetails("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        _goalDetailsData.value = response.body()
                    } else {
                        _error.value = "Failed to load goal details: ${response.code()}"
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
