package com.simats.e_bookmotivation.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /**
     * PRODUCTION URL: Replace with the actual URL provided by the college deployment team.
     * Example: "https://api.motivbooks.yourcollege.edu/"
     */
    private const val BASE_URL = "https://cinerary-zonia-debasingly.ngrok-free.dev/"
    private var authToken: String? = null

    fun setToken(token: String?) {
        authToken = token
    }

    fun getAuthToken(): String? {
        return authToken
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val request = chain.request()
            val path = request.url.encodedPath
            
            val requestBuilder = request.newBuilder()
                .addHeader("ngrok-skip-browser-warning", "true")
            
            // Only add Authorization from stored token if not already explicitly set per-request
            val alreadyHasAuth = request.header("Authorization") != null
            if (!alreadyHasAuth && !path.endsWith("login/") && !path.endsWith("register/")
                && !path.endsWith("forgot-password/") && !path.endsWith("verify-otp/")
                && !path.endsWith("reset-password/")) {
                authToken?.let { requestBuilder.addHeader("Authorization", "Bearer $it") }
            }
            
            chain.proceed(requestBuilder.build())
        }
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy { instance.create(AuthApi::class.java) }
    val progressApi: ProgressApi by lazy { instance.create(ProgressApi::class.java) }
    val subPageApi: SubPageApi by lazy { instance.create(SubPageApi::class.java) }
    val dashboardApi: DashboardApi by lazy { instance.create(DashboardApi::class.java) }
    val profileApi: ProfileApi by lazy { instance.create(ProfileApi::class.java) }
    val challengeApi: ChallengeApi by lazy { instance.create(ChallengeApi::class.java) }
    val libraryApi: LibraryApi by lazy { instance.create(LibraryApi::class.java) }
    val notificationApi: NotificationApi by lazy { instance.create(NotificationApi::class.java) }
    val aiCoachApi: AICoachApi by lazy { instance.create(AICoachApi::class.java) }
    val adminApi: AdminApi by lazy { instance.create(AdminApi::class.java) }
}
