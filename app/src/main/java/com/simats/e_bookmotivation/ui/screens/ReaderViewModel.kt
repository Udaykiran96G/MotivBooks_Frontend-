package com.simats.e_bookmotivation.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ChapterResponse
import com.simats.e_bookmotivation.network.models.AISummaryRequest
import com.simats.e_bookmotivation.network.models.AISummaryResponse
import com.simats.e_bookmotivation.util.TranslationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val _chapters = MutableStateFlow<List<ChapterResponse>>(emptyList())
    val chapters: StateFlow<List<ChapterResponse>> = _chapters.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // --- Translation States ---
    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _translatedPageContent = MutableStateFlow<String?>(null)
    val translatedPageContent: StateFlow<String?> = _translatedPageContent.asStateFlow()

    private val _isPageTranslated = MutableStateFlow(false)
    val isPageTranslated: StateFlow<Boolean> = _isPageTranslated.asStateFlow()

    private val _translatedSelection = MutableStateFlow<String?>(null)
    val translatedSelection: StateFlow<String?> = _translatedSelection.asStateFlow()

    private val _translationError = MutableStateFlow<String?>(null)
    val translationError: StateFlow<String?> = _translationError.asStateFlow()

    // --- AI Summary States ---
    private val _isSummarizing = MutableStateFlow(false)
    val isSummarizing: StateFlow<Boolean> = _isSummarizing.asStateFlow()

    private val _aiSummary = MutableStateFlow<String?>(null)
    val aiSummary: StateFlow<String?> = _aiSummary.asStateFlow()

    private val _summaryError = MutableStateFlow<String?>(null)
    val summaryError: StateFlow<String?> = _summaryError.asStateFlow()

    fun fetchChapters(bookId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = getAuthToken() ?: return@launch
                val response = RetrofitClient.libraryApi.getChapters("Bearer $token", bookId)
                if (response.isSuccessful) {
                    _chapters.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load book content."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Translate selected text snippet */
    fun translateSelectedText(text: String, targetLangCode: String) {
        viewModelScope.launch {
            _isTranslating.value = true
            _translationError.value = null
            try {
                val result = TranslationHelper.translate(text, targetLangCode)
                _translatedSelection.value = result
            } catch (e: Exception) {
                _translationError.value = "Translation failed: ${e.message}"
            } finally {
                _isTranslating.value = false
            }
        }
    }

    /** Translate entire page/chapter content */
    fun translatePage(content: String, targetLangCode: String) {
        viewModelScope.launch {
            _isTranslating.value = true
            _translationError.value = null
            try {
                val result = TranslationHelper.translate(content, targetLangCode)
                _translatedPageContent.value = result
                _isPageTranslated.value = true
            } catch (e: Exception) {
                _translationError.value = "Page translation failed: ${e.message}"
            } finally {
                _isTranslating.value = false
            }
        }
    }

    /** Revert page to original content */
    fun revertPage() {
        _translatedPageContent.value = null
        _isPageTranslated.value = false
    }

    /** Clear selected text translation */
    fun clearTranslatedSelection() {
        _translatedSelection.value = null
    }

    /** AI Categorical Summary */
    fun summarizeContent(content: String) {
        viewModelScope.launch {
            if (content.isBlank()) return@launch
            _isSummarizing.value = true
            _summaryError.value = null
            _aiSummary.value = null
            try {
                val token = getAuthToken() ?: return@launch
                val request = AISummaryRequest(content)
                val response = RetrofitClient.aiCoachApi.getAISummary("Bearer $token", request)
                if (response.isSuccessful) {
                    _aiSummary.value = response.body()?.summary
                } else {
                    _summaryError.value = "Failed to get AI summary."
                }
            } catch (e: Exception) {
                _summaryError.value = "Summary error: ${e.message}"
            } finally {
                _isSummarizing.value = false
            }
        }
    }

    fun clearSummary() {
        _aiSummary.value = null
        _summaryError.value = null
    }

    fun trackReadingProgress(bookId: Int, minutes: Int, pages: Int) {
        viewModelScope.launch {
            try {
                val token = getAuthToken() ?: return@launch
                val progressData = mapOf(
                    "book_id" to bookId,
                    "minutes" to minutes,
                    "pages" to pages
                )
                RetrofitClient.progressApi.updateReadingProgress("Bearer $token", progressData)
            } catch (e: Exception) {
                // Background tracking, fail silently
            }
        }
    }

    private fun getAuthToken(): String? {
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
}
