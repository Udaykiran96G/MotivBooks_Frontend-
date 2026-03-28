package com.simats.e_bookmotivation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.res.painterResource
import com.simats.e_bookmotivation.R
import androidx.compose.ui.graphics.ColorFilter
import com.simats.e_bookmotivation.ui.theme.PaleSlate
import com.simats.e_bookmotivation.ui.theme.SoftSage
import com.simats.e_bookmotivation.ui.theme.DeepSlate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.ui.components.AuthTextField
import com.simats.e_bookmotivation.ui.components.GlassCard
import com.simats.e_bookmotivation.ui.theme.DeepIndigo
import com.simats.e_bookmotivation.ui.theme.MidnightBlue
import com.simats.e_bookmotivation.ui.theme.SoftBlue
import com.simats.e_bookmotivation.ui.theme.SoftTeal
import com.simats.e_bookmotivation.ui.theme.TextPrimary
import com.simats.e_bookmotivation.ui.theme.TextSecondary
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.simats.e_bookmotivation.network.NetworkUtils
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.LoginRequest
import org.json.JSONObject

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit,
    onNavigateToReset: () -> Unit,
    onNavigateToAdminLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpMode by remember { mutableStateOf(false) }
    var otpSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PaleSlate, SoftSage)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Hero Section
            Spacer(modifier = Modifier.height(40.dp))
            Surface(
                modifier = Modifier.size(160.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = Color.White.copy(alpha = 0.45f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_books_sparkle),
                    contentDescription = "MotivBooks Logo",
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    alpha = 0.9f
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MotivBooks",
                style = MaterialTheme.typography.displayMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Read. Grow. Become.",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Glass Card
            GlassCard {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = androidx.compose.material.icons.Icons.Outlined.Email
                    )
                    
                    if (!isOtpMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            leadingIcon = androidx.compose.material.icons.Icons.Outlined.Lock,
                            isPassword = true
                        )
                        
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = onNavigateToReset) {
                                Text(
                                    text = "Forgot Password?",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else if (otpSent) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AuthTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 6) otp = it },
                            label = "6-Digit OTP",
                            leadingIcon = androidx.compose.material.icons.Icons.Outlined.Lock,
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Button
                    Button(
                        onClick = { 
                            if (email.isBlank()) {
                                Toast.makeText(context, "Please enter email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            isLoading = true
                            scope.launch {
                                try {
                                    if (!isOtpMode) {
                                        // Standard Password Login
                                        if (password.isBlank()) {
                                            withContext(Dispatchers.Main) {
                                                Toast.makeText(context, "Please enter password", Toast.LENGTH_SHORT).show()
                                                isLoading = false
                                            }
                                            return@launch
                                        }
                                        val response = RetrofitClient.authApi.login(LoginRequest(email.trim().lowercase(), password))
                                        handleLoginResponse(response, context, onLoginSuccess)
                                    } else {
                                        // OTP Login
                                        if (!otpSent) {
                                            val response = RetrofitClient.authApi.sendLoginOtp(mapOf("email" to email.trim().lowercase()))
                                            withContext(Dispatchers.Main) {
                                                if (response.isSuccessful) {
                                                    otpSent = true
                                                    Toast.makeText(context, "OTP sent to your email!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else {
                                            if (otp.length != 6) {
                                                withContext(Dispatchers.Main) {
                                                    Toast.makeText(context, "Enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                                                    isLoading = false
                                                }
                                                return@launch
                                            }
                                            val response = RetrofitClient.authApi.loginWithOtp(mapOf(
                                                "email" to email.trim().lowercase(),
                                                "otp" to otp
                                            ))
                                            handleLoginResponse(response, context, onLoginSuccess)
                                        }
                                    }
                                } catch (e: Exception) {
                                    val friendlyMsg = NetworkUtils.getFriendlyErrorMessage(e)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, friendlyMsg, Toast.LENGTH_LONG).show()
                                    }
                                } finally {
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextPrimary,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = if (isOtpMode) (if (otpSent) "Verify & Login" else "Send Login OTP") else "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle Mode Button
                    OutlinedButton(
                        onClick = { 
                            isOtpMode = !isOtpMode
                            otpSent = false
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (isOtpMode) "Login with Password" else "Login with OTP",
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Create Account Button
                    OutlinedButton(
                        onClick = onNavigateToSignup,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = true,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TextSecondary)
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Are you an Administrator?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = onNavigateToAdminLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Go to Admin Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private suspend fun <T> handleLoginResponse(
    response: retrofit2.Response<T>,
    context: android.content.Context,
    onLoginSuccess: () -> Unit
) {
    withContext(Dispatchers.Main) {
        val body = response.body()
        if (response.isSuccessful && body != null) {
            val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            val token = when (body) {
                is com.simats.e_bookmotivation.network.models.LoginResponse -> body.access
                else -> null
            }
            val isStaff = when (body) {
                is com.simats.e_bookmotivation.network.models.LoginResponse -> body.is_staff
                else -> false
            }
            
            if (token != null) {
                prefs.edit()
                    .putString("auth_token", token)
                    .putBoolean("is_logged_in", true)
                    .putBoolean("is_staff", isStaff)
                    .apply()
                RetrofitClient.setToken(token)
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            } else {
                Toast.makeText(context, "Unexpected response format", Toast.LENGTH_SHORT).show()
            }
        } else {
            val errorBody = response.errorBody()?.string()
            val msg = NetworkUtils.parseErrorMessage(errorBody, "Login failed")
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }
}
