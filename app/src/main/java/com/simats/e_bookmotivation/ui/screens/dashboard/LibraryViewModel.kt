package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.NetworkUtils
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.BookResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val _books = MutableStateFlow<List<BookResponse>>(emptyList())
    val books: StateFlow<List<BookResponse>> = _books.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchLibrary()
    }

    fun fetchLibrary(
        category: String? = null,
        sort: String? = null,
        author: String? = null,
        yearMin: String? = null,
        yearMax: String? = null,
        rating: String? = null,
        language: String? = null,
        tags: String? = null,
        recentlyAdded: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.libraryApi.getLibrary(
                    "Bearer $token", 
                    category, 
                    sort,
                    author,
                    yearMin,
                    yearMax,
                    rating,
                    language,
                    tags,
                    recentlyAdded
                )
                if (response.isSuccessful) {
                    _books.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load library data."
                }
            } catch (e: Exception) {
                _error.value = NetworkUtils.getFriendlyErrorMessage(e)
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
