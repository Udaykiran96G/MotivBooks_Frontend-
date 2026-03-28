package com.simats.e_bookmotivation.network

import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object NetworkUtils {

    /**
     * Converts a network connection exception into a user-friendly message.
     */
    fun getFriendlyErrorMessage(e: Throwable): String {
        return when (e) {
            is UnknownHostException -> 
                "Cannot connect to server. Please check your internet connection and DNS settings."
            is SocketTimeoutException -> 
                "Connection timed out. Please check your internet and try again."
            else -> {
                val msg = e.message ?: ""
                when {
                    msg.contains("timeout", ignoreCase = true) -> 
                        "Connection timed out. Please try again."
                    msg.contains("resolve host", ignoreCase = true) -> 
                        "Cannot connect to server. Please check your internet connection."
                    else -> "Connection error. Please check your network and try again."
                }
            }
        }
    }

    /**
     * Parses error response body from Retrofit/OkHttp.
     */
    fun parseErrorMessage(errorBody: String?, fallback: String): String {
        if (errorBody.isNullOrEmpty()) return fallback
        return try {
            val json = JSONObject(errorBody)
            json.optString("error", json.optString("detail", fallback))
        } catch (e: Exception) {
            fallback
        }
    }
}
