package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ProfileUpdateRequest
import com.simats.e_bookmotivation.network.models.UserProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UserProfileResponse?>(null)
    val uiState: StateFlow<UserProfileResponse?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _updateSuccess = MutableStateFlow(false)
    val updateSuccess: StateFlow<Boolean> = _updateSuccess.asStateFlow()

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.profileApi.getProfile("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, phone: String, dob: String, location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val request = ProfileUpdateRequest(name, phone, dob, location)
                val response = RetrofitClient.profileApi.updateProfile("Bearer $token", request)
                if (response.isSuccessful) {
                    _uiState.value = response.body()
                    _updateSuccess.value = true
                    _error.value = null
                } else {
                    _error.value = "Update failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetUpdateSuccess() {
        _updateSuccess.value = false
    }

    private fun getAuthToken(): String? {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)
        if (token == null) {
            _error.value = "Auth token not found."
        }
        return token
    }
}
