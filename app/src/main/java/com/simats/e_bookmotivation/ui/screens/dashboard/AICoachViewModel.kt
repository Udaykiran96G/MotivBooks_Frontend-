package com.simats.e_bookmotivation.ui.screens.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.AICoachInsightResponse
import com.simats.e_bookmotivation.network.models.AICoachChatRequest
import com.simats.e_bookmotivation.network.models.AICoachChatResponse
import com.simats.e_bookmotivation.network.models.AICoachStrategyResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AICoachViewModel(application: Application) : AndroidViewModel(application) {

    private val _insight = MutableStateFlow<AICoachInsightResponse?>(null)
    val insight: StateFlow<AICoachInsightResponse?> = _insight.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Chat state
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Strategy state
    private val _strategy = MutableStateFlow<AICoachStrategyResponse?>(null)
    val strategy: StateFlow<AICoachStrategyResponse?> = _strategy.asStateFlow()

    private val _isStrategyLoading = MutableStateFlow(false)
    val isStrategyLoading: StateFlow<Boolean> = _isStrategyLoading.asStateFlow()

    init {
        fetchInsight()
    }

    fun fetchInsight() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.aiCoachApi.getAICoachInsight("Bearer $token")
                if (response.isSuccessful) {
                    _insight.value = response.body()
                    _error.value = null
                } else {
                    _error.value = "Failed to load AI Coach insights."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendChat(query: String) {
        if (query.isBlank()) return
        
        // Add user message
        _chatMessages.value = _chatMessages.value + ChatMessage(query, isFromUser = true)
        
        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.aiCoachApi.postChat(
                    "Bearer $token",
                    AICoachChatRequest(query)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        body?.response ?: "I couldn't process that. Try again!",
                        isFromUser = false
                    )
                } else {
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        "Sorry, I'm having trouble connecting. Please try again!",
                        isFromUser = false
                    )
                }
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    "Connection error. Please check your internet and try again.",
                    isFromUser = false
                )
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun generateStrategy() {
        viewModelScope.launch {
            _isStrategyLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.aiCoachApi.generateStrategy("Bearer $token")
                if (response.isSuccessful) {
                    _strategy.value = response.body()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isStrategyLoading.value = false
            }
        }
    }

    private fun getAuthToken(): String? {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
}
