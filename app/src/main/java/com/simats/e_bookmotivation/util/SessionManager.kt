package com.simats.e_bookmotivation.util

import android.content.Context
import com.simats.e_bookmotivation.network.RetrofitClient

object SessionManager {
    
    /**
     * Clears all authentication and session data from SharedPreferences
     * and resets the RetrofitClient token.
     */
    fun clearSession(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .remove("auth_token")
            .remove("refresh_token")
            .remove("is_logged_in")
            .apply()
            
        // Reset RetrofitClient
        RetrofitClient.setToken(null)
    }
    
    /**
     * Gets the currently stored auth token
     */
    fun getAuthToken(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("auth_token", null)
    }
}
