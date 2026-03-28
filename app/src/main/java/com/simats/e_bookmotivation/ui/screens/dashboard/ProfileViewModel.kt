package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ProfileDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<ProfileDetailResponse?>(null)
    val uiState: StateFlow<ProfileDetailResponse?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchProfileDetail()
    }

    fun fetchProfileDetail() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken()
                if (token == null) {
                    _error.value = "Session expired. Please log in again."
                    _isLoading.value = false
                    return@launch
                }
                val response = RetrofitClient.profileApi.getProfileDetail("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Failed to load profile: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = getAuthToken() ?: run {
                    _error.value = "Authentication error: Please log in again."
                    _isLoading.value = false
                    return@launch
                }
                val response = RetrofitClient.profileApi.deleteAccount("Bearer $token")
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message()
                    _error.value = "Deletion failed: $errorMsg"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
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
