package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.SavedQuoteResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SavedQuotesViewModel(application: Application) : AndroidViewModel(application) {
    private val _quotesData = MutableStateFlow<List<SavedQuoteResponse>>(emptyList())
    val quotesData: StateFlow<List<SavedQuoteResponse>> = _quotesData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchSavedQuotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val response = RetrofitClient.subPageApi.getSavedQuotes("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        _quotesData.value = response.body()!!
                    } else {
                        _error.value = "Failed to load quotes: ${response.code()}"
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

    fun deleteQuote(quoteId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val response = RetrofitClient.subPageApi.deleteSavedQuote("Bearer $token", quoteId)
                    if (response.isSuccessful) {
                        // Remove from local list
                        _quotesData.value = _quotesData.value.filter { it.id != quoteId }
                        onResult(true, "REMOVE FROM SAVED")
                    } else {
                        onResult(false, "Failed to delete: ${response.code()}")
                    }
                } else {
                    onResult(false, "No authentication token found")
                }
            } catch (e: Exception) {
                onResult(false, "Network error: ${e.message}")
            }
        }
    }
}
