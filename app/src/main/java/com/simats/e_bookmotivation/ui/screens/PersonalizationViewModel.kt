package com.simats.e_bookmotivation.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ReadingPreferenceResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PersonalizationViewModel(application: Application) : AndroidViewModel(application) {

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun savePreferences(interests: List<String>, goals: String?, habit: String?) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)
                if (token == null) {
                    _error.value = "Auth token not found."
                    return@launch
                }

                // Fetch current preferences first to avoid overwriting other settings
                val currentPrefsResponse = RetrofitClient.profileApi.getPreferences("Bearer $token")
                if (currentPrefsResponse.isSuccessful && currentPrefsResponse.body() != null) {
                    val current = currentPrefsResponse.body()!!
                    val updatedPrefs = current.copy(
                        interests = interests.joinToString(","),
                        improvement_goals = goals ?: "",
                        reading_style = habit ?: ""
                    )

                    val response = RetrofitClient.profileApi.updatePreferences("Bearer $token", updatedPrefs)
                    if (response.isSuccessful) {
                        _saveSuccess.value = true
                    } else {
                        _error.value = "Failed to save preferences."
                    }
                } else {
                    // Fallback to default if fetch fails
                    val newPrefs = ReadingPreferenceResponse(
                        font_size = 16,
                        theme = "Light",
                        language = "English",
                        auto_save_highlights = true,
                        interests = interests.joinToString(","),
                        improvement_goals = goals ?: "",
                        reading_style = habit ?: ""
                    )
                    val response = RetrofitClient.profileApi.updatePreferences("Bearer $token", newPrefs)
                     if (response.isSuccessful) {
                        _saveSuccess.value = true
                    } else {
                        _error.value = "Failed to save preferences."
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isSaving.value = false
            }
        }
    }
}
