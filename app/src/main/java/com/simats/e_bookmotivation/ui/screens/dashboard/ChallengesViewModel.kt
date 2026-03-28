package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ChallengeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChallengesViewModel(application: Application) : AndroidViewModel(application) {

    private val _challenges = MutableStateFlow<List<ChallengeResponse>>(emptyList())
    val challenges: StateFlow<List<ChallengeResponse>> = _challenges.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchChallenges()
    }

    fun fetchChallenges() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.challengeApi.getChallenges("Bearer $token")
                if (response.isSuccessful) {
                    _challenges.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load challenges."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleChallengeCompletion(challengeId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.challengeApi.updateChallenge(
                    "Bearer $token",
                    challengeId,
                    mapOf("is_completed" to isCompleted)
                )
                if (response.isSuccessful) {
                    fetchChallenges() // Refresh list
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun getAuthToken(): String? {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
}
