package com.simats.e_bookmotivation.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import com.simats.e_bookmotivation.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import android.content.Context
import com.simats.e_bookmotivation.ui.theme.DeepIndigo
import com.simats.e_bookmotivation.ui.theme.MidnightBlue
import com.simats.e_bookmotivation.ui.theme.PitchBlack
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)

        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        
        // --- Token Validation Logic ---
        if (isLoggedIn && token != null) {
            try {
                // Set the token in Retrofit before validating
                com.simats.e_bookmotivation.network.RetrofitClient.setToken(token)
                
                // Call profile to verify the session is still dead/alive
                val response = com.simats.e_bookmotivation.network.RetrofitClient.profileApi.getProfile("Bearer $token")
                
                if (response.isSuccessful) {
                    // Valid session -> Home or AdminDashboard
                    val isStaff = prefs.getBoolean("is_staff", false)
                    if (isStaff) {
                        navController.navigate("AdminDashboard") {
                            popUpTo("splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("Home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                } else {
                    // Invalid session or other server error -> Clear and Login
                    com.simats.e_bookmotivation.util.SessionManager.clearSession(context)
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            } catch (e: Exception) {
                // Network error -> Force Login page (no offline bypass)
                com.simats.e_bookmotivation.util.SessionManager.clearSession(context)
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            // No session -> Clear and Login
            com.simats.e_bookmotivation.util.SessionManager.clearSession(context)
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    // Light Premium Background
    val backgroundColor = Color(0xFFF1F5F9)
    val brandColor = Color(0xFF1E293B)
    val subtitleColor = Color(0xFF64748B)
    val iconColor = Color(0xFF7DD3FC) // Light blue from image

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            // Reverted to standard Icon for Splash
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = iconColor
            )
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column {
                // Main Logo Text
                Text(
                    text = "MotivBooks",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-1).sp
                    ),
                    color = brandColor
                )
                
                // Subtitle
                Text(
                    text = "Read • Grow • Inspire",
                    style = MaterialTheme.typography.bodyLarge,
                    color = subtitleColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
