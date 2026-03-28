package com.simats.e_bookmotivation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.simats.e_bookmotivation.network.NetworkUtils
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.ForgotPasswordRequest
import com.simats.e_bookmotivation.network.models.VerifyOTPRequest
import com.simats.e_bookmotivation.network.models.ResetPasswordRequest
import com.simats.e_bookmotivation.ui.components.AuthTextField
import com.simats.e_bookmotivation.ui.components.GlassCard
import com.simats.e_bookmotivation.ui.theme.DeepIndigo
import com.simats.e_bookmotivation.ui.theme.MidnightBlue
import com.simats.e_bookmotivation.ui.theme.SoftBlue
import com.simats.e_bookmotivation.ui.theme.SoftTeal
import com.simats.e_bookmotivation.ui.theme.TextPrimary
import com.simats.e_bookmotivation.ui.theme.TextSecondary

@Composable
fun ResetScreen(
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Step: 0 = email, 1 = OTP, 2 = new password
    var currentStep by remember { mutableIntStateOf(0) }

    val titles = listOf("Forgot Password?", "Enter OTP", "New Password")
    val subtitles = listOf(
        "No worries, we'll send you an OTP to reset your password.",
        "Please enter the 6-digit verification code sent to your email.",
        "Create a strong new password for your account."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepIndigo, MidnightBlue)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(64.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    if (currentStep > 0) {
                        currentStep--
                    } else {
                        onNavigateBack()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // Step indicator
            Text(
                text = "Step ${currentStep + 1} of 3",
                style = MaterialTheme.typography.labelMedium,
                color = SoftTeal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = titles[currentStep],
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = subtitles[currentStep],
                style = MaterialTheme.typography.bodyLarge,
                color = SoftBlue.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Glass Card Form
            GlassCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        // Step 0: Email
                        0 -> {
                            AuthTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email Address",
                                leadingIcon = Icons.Outlined.Email
                            )
                        }
                        // Step 1: OTP
                        1 -> {
                            AuthTextField(
                                value = otpCode,
                                onValueChange = { if (it.length <= 6) otpCode = it },
                                label = "6-Digit OTP",
                                leadingIcon = Icons.Outlined.VpnKey
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            TextButton(
                                onClick = {
                                    if (isLoading) return@TextButton
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.authApi.forgotPassword(
                                                ForgotPasswordRequest(email.trim().lowercase())
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "New OTP sent!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                                                }
                                                isLoading = false
                                            }
                                        } catch (e: Exception) {
                                            val friendlyMsg = NetworkUtils.getFriendlyErrorMessage(e)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, friendlyMsg, Toast.LENGTH_LONG).show()
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = "Didn't receive? Resend OTP",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        // Step 2: New Password
                        2 -> {
                            AuthTextField(
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                label = "New Password",
                                leadingIcon = Icons.Outlined.Lock,
                                isPassword = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            AuthTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = "Confirm Password",
                                leadingIcon = Icons.Outlined.Lock,
                                isPassword = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action button
                    Button(
                        onClick = {
                            when (currentStep) {
                                // Step 0: Send OTP
                                0 -> {
                                    if (email.isBlank()) {
                                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.authApi.forgotPassword(
                                                ForgotPasswordRequest(email.trim().lowercase())
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "OTP sent to your email!", Toast.LENGTH_SHORT).show()
                                                    currentStep = 1
                                                } else {
                                                    val errorBody = response.errorBody()?.string()
                                                    val msg = NetworkUtils.parseErrorMessage(errorBody, "Failed to send OTP")
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                                isLoading = false
                                            }
                                        } catch (e: Exception) {
                                            val friendlyMsg = NetworkUtils.getFriendlyErrorMessage(e)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, friendlyMsg, Toast.LENGTH_LONG).show()
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                                // Step 1: Verify OTP
                                1 -> {
                                    if (otpCode.length != 6) {
                                        Toast.makeText(context, "Please enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.authApi.verifyOtp(
                                                VerifyOTPRequest(email.trim().lowercase(), otpCode.trim())
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "OTP Verified!", Toast.LENGTH_SHORT).show()
                                                    currentStep = 2
                                                } else {
                                                    val errorBody = response.errorBody()?.string()
                                                    val msg = NetworkUtils.parseErrorMessage(errorBody, "Invalid OTP")
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                                isLoading = false
                                            }
                                        } catch (e: Exception) {
                                            val friendlyMsg = NetworkUtils.getFriendlyErrorMessage(e)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, friendlyMsg, Toast.LENGTH_LONG).show()
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                                // Step 2: Reset Password
                                2 -> {
                                    // Password validation: 8+ chars, uppercase, lowercase, number, symbol
                                    if (newPassword.length < 8) {
                                        Toast.makeText(context, "New password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!newPassword.any { it.isUpperCase() }) {
                                        Toast.makeText(context, "New password must contain an uppercase letter", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!newPassword.any { it.isLowerCase() }) {
                                        Toast.makeText(context, "New password must contain a lowercase letter", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    if (!newPassword.any { it.isDigit() }) {
                                        Toast.makeText(context, "New password must contain a number", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    val specialChars = "!@#$%^&*(),.?\":{}|<>"
                                    if (!newPassword.any { it in specialChars }) {
                                        Toast.makeText(context, "New password must contain a symbol", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    if (newPassword != confirmPassword) {
                                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val response = RetrofitClient.authApi.resetPassword(
                                                ResetPasswordRequest(
                                                    email.trim().lowercase(),
                                                    otpCode.trim(),
                                                    newPassword
                                                )
                                            )
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    Toast.makeText(context, "Password reset successfully! Please login.", Toast.LENGTH_LONG).show()
                                                    onResetSuccess()
                                                } else {
                                                    val errorBody = response.errorBody()?.string()
                                                    val msg = NetworkUtils.parseErrorMessage(errorBody, "Failed to reset password")
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                                isLoading = false
                                            }
                                        } catch (e: Exception) {
                                            val friendlyMsg = NetworkUtils.getFriendlyErrorMessage(e)
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, friendlyMsg, Toast.LENGTH_LONG).show()
                                                isLoading = false
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftTeal,
                            contentColor = TextPrimary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = TextPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = when (currentStep) {
                                    0 -> "Send OTP"
                                    1 -> "Verify OTP"
                                    else -> "Reset Password"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

