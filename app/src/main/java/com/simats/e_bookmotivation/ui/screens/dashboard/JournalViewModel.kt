package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.JournalEntryResponse
import com.simats.e_bookmotivation.network.models.JournalEntryRequest
import com.simats.e_bookmotivation.network.models.MoodGraphResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val _entries = MutableStateFlow<List<JournalEntryResponse>>(emptyList())
    val entries: StateFlow<List<JournalEntryResponse>> = _entries.asStateFlow()

    private val _todayPrompt = MutableStateFlow("")
    val todayPrompt: StateFlow<String> = _todayPrompt.asStateFlow()

    private val _todayDate = MutableStateFlow("")
    val todayDate: StateFlow<String> = _todayDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()
    
    private val _moodData = MutableStateFlow<MoodGraphResponse?>(null)
    val moodData: StateFlow<MoodGraphResponse?> = _moodData.asStateFlow()

    init {
        fetchJournalEntries()
        fetchMoodData()
    }

    fun fetchJournalEntries() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }
                
                val response = RetrofitClient.dashboardApi.getJournalEntries("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    _entries.value = body.entries
                    _todayPrompt.value = body.today_prompt
                    _todayDate.value = body.today_date
                    _error.value = null
                } else {
                    _error.value = "Failed to load journal entries."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchMoodData() {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token != null) {
                    val response = RetrofitClient.subPageApi.getMoodGraph("Bearer $token")
                    if (response.isSuccessful && response.body() != null) {
                        _moodData.value = response.body()
                    }
                }
            } catch (e: Exception) {
                // Silent fail
            }
        }
    }

    fun addJournalEntry(content: String, mood: String, prompt: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }

                val request = JournalEntryRequest(
                    title = "Daily Reflection",
                    content = content,
                    mood = mood,
                    prompt = prompt
                )
                val response = RetrofitClient.dashboardApi.createJournalEntry("Bearer $token", request)
                
                if (response.isSuccessful) {
                    _successMessage.value = "Entry saved!"
                    fetchJournalEntries() // Refresh the list
                    fetchMoodData() // Refresh the mood graph
                    onSuccess()
                } else {
                    _error.value = "Failed to create journal entry: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    
    fun getEntryById(id: Int): JournalEntryResponse? {
        return _entries.value.find { it.id == id }
    }

    fun clearMessages() {
        _successMessage.value = null
        _error.value = null
    }
}
