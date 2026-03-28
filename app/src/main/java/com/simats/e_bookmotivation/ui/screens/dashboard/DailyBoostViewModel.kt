package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.DailyBoostResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DailyBoostViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<DailyBoostResponse?>(null)
    val uiState: StateFlow<DailyBoostResponse?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchDailyBoost()
    }

    fun fetchDailyBoost() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }
                
                val response = RetrofitClient.dashboardApi.getDailyBoost("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = response.body()
                    _error.value = null
                } else {
                    val errorBody = response.errorBody()?.string()
                    _error.value = "Failed: ${response.code()} ${response.message()}\n$errorBody"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveQuote(text: String, author: String, bookTitle: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    onResult(false, "Auth token not found.")
                    return@launch
                }

                val request = mapOf(
                    "quote" to text, 
                    "author" to author,
                    "book" to bookTitle
                )
                val response = RetrofitClient.subPageApi.addSavedQuote("Bearer $token", request)
                
                if (response.isSuccessful) {
                    onResult(true, "Quote saved successfully!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    onResult(false, "Failed: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "An unknown error occurred")
            }
        }
    }
}
