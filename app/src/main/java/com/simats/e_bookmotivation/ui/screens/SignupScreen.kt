package com.simats.e_bookmotivation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import com.simats.e_bookmotivation.R
import androidx.compose.ui.graphics.ColorFilter
import com.simats.e_bookmotivation.ui.theme.PaleSlate
import com.simats.e_bookmotivation.ui.theme.SoftSage
import com.simats.e_bookmotivation.ui.theme.CreamWhite
import com.simats.e_bookmotivation.ui.theme.DeepSlate
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.ui.components.AuthTextField
import com.simats.e_bookmotivation.ui.components.GlassCard
import com.simats.e_bookmotivation.ui.theme.SoftBlue
import com.simats.e_bookmotivation.ui.theme.SoftPeach
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
import com.simats.e_bookmotivation.network.models.RegisterRequest

@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToAdminSignup: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isStaff by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PaleSlate, SoftSage, CreamWhite)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Section
            Spacer(modifier = Modifier.height(20.dp))
            IconButton(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(800)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.45f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_books_sparkle),
                            contentDescription = "MotivBooks Logo",
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            alpha = 0.9f
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Create Your Reading Space",
                        style = MaterialTheme.typography.displaySmall, // Smaller than Medium for fit
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your growth journey begins here",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Glass Card
            GlassCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // After name field
                    AuthTextField(
                        value = phone,
                        onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 10) phone = it },
                        label = "Mobile Number",
                        leadingIcon = Icons.Outlined.Phone
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = androidx.compose.material.icons.Icons.Outlined.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        leadingIcon = androidx.compose.material.icons.Icons.Outlined.Lock,
                        isPassword = true
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                     Text(
                        text = "8+ chars, uppercase, lowercase, number & symbol",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        leadingIcon = androidx.compose.material.icons.Icons.Outlined.Lock,
                        isPassword = true
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Administrator Registration?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Button(
                        onClick = onNavigateToAdminSignup,
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
                            text = "Go to Admin Registration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup Button
                    Button(
                        onClick = {
                            if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Email validation
                            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
                            if (!emailRegex.matches(email)) {
                                Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Phone validation
                            if (phone.length != 10) {
                                Toast.makeText(context, "Mobile number must be exactly 10 digits", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // Password validation: 8+ chars, uppercase, lowercase, number, symbol
                            if (password.length < 8) {
                                Toast.makeText(context, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!password.any { it.isUpperCase() }) {
                                Toast.makeText(context, "Password must contain an uppercase letter", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!password.any { it.isLowerCase() }) {
                                Toast.makeText(context, "Password must contain a lowercase letter", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (!password.any { it.isDigit() }) {
                                Toast.makeText(context, "Password must contain a number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val specialChars = "!@#$%^&*(),.?\":{}|<>"
                            if (!password.any { it in specialChars }) {
                                Toast.makeText(context, "Password must contain a symbol", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            if (password != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                try {
                                    val response = RetrofitClient.authApi.register(RegisterRequest(name, email.trim().lowercase(), password, confirmPassword, phone, false)) // isStaff is now false by default
                                    withContext(Dispatchers.Main) {
                                        val body = response.body()
                                        if (response.isSuccessful && body != null) {
                                            val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                            prefs.edit()
                                                .putString("auth_token", body.access)
                                                .putBoolean("is_logged_in", true)
                                                .putBoolean("is_staff", false) // isStaff is now false by default
                                                .apply()
                                            RetrofitClient.setToken(body.access)
                                            
                                            Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                            onSignupSuccess()
                                        } else {
                                            val errorBody = response.errorBody()?.string()
                                            val msg = NetworkUtils.parseErrorMessage(errorBody, "Signup failed")
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
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TextPrimary, // Dark button for premium feel
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Already have an account
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already have an account? ",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Login",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateToLogin() },
                            fontSize = 14.sp
                        )
                    }
                }
            }
             Spacer(modifier = Modifier.height(24.dp))
             Text(
                text = "By signing up you agree to Terms & Privacy",
                color = TextSecondary.copy(alpha = 0.5f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
