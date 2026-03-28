package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.SubscriptionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<SubscriptionResponse?>(null)
    val uiState: StateFlow<SubscriptionResponse?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchSubscription()
    }

    fun fetchSubscription() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.profileApi.getSubscription("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.value = response.body()
                }
            } catch (e: Exception) {
                _error.value = e.message
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
