package com.simats.e_bookmotivation.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import com.simats.e_bookmotivation.R
import androidx.compose.ui.graphics.ColorFilter
import com.simats.e_bookmotivation.ui.theme.PaleSlate
import com.simats.e_bookmotivation.ui.theme.CalmBlue
import com.simats.e_bookmotivation.ui.theme.DeepSlate
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.ui.theme.SkyBlue
import com.simats.e_bookmotivation.ui.theme.SoftTeal
import com.simats.e_bookmotivation.ui.theme.TextPrimary
import com.simats.e_bookmotivation.ui.theme.TextSecondary

import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalizationScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PersonalizationViewModel = viewModel()
) {
    // State
    val selectedGenres = remember { mutableStateListOf<String>() }
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    var selectedHabit by remember { mutableStateOf<String?>(null) }
    
    val isSaving by viewModel.isSaving.collectAsState(initial = false)
    val saveSuccess by viewModel.saveSuccess.collectAsState(initial = false)
    val error by viewModel.error.collectAsState(initial = null)

    androidx.compose.runtime.LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            onNavigateToHome()
        }
    }

    // Data
    val genres = listOf("Self Growth 🌱", "Productivity ⚡", "Psychology 🧠", "Finance 💰", "Fiction 🧚", "Spiritual 🕊️", "Business 📊", "Philosophy 🏛️")
    val goals = listOf("Confidence 💬", "Discipline 🔥", "Focus 🎯", "Calmness 🧘", "Career 📈")
    val habits = listOf("5 min daily ☕", "10 min focused 📘", "Weekend reader 🌤️", "Deep reader 🧠")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PaleSlate, CalmBlue)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Spacer(modifier = Modifier.height(20.dp))
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Tell us about you",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "This helps us personalize your reading journey",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Illustration Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.45f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_books_sparkle),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxSize(),
                        alpha = 0.9f
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Generic Selection
            SectionTitle("What do you love reading? 📖")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                genres.forEach { genre ->
                    val isSelected = selectedGenres.contains(genre)
                    val backgroundColor by animateColorAsState(if (isSelected) SoftTeal else Color.White)
                    val textColor by animateColorAsState(if (isSelected) TextPrimary else TextSecondary)

                    Surface(
                        modifier = Modifier
                            .clickable {
                                if (isSelected) selectedGenres.remove(genre) else selectedGenres.add(genre)
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = backgroundColor,
                        border = BorderStroke(1.dp, if (isSelected) SoftTeal else Color.LightGray.copy(alpha=0.5f))
                    ) {
                        Text(
                            text = genre,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            color = textColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Goal Selection
            SectionTitle("What do you want to improve? 🎯")
             FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goals.forEach { goal ->
                    val isSelected = selectedGoal == goal
                    val backgroundColor by animateColorAsState(if (isSelected) TextPrimary else Color.White)
                    val textColor by animateColorAsState(if (isSelected) Color.White else TextPrimary)
                    val elevation by animateDpAsState(if (isSelected) 8.dp else 2.dp)

                    Surface(
                        modifier = Modifier
                            .clickable { selectedGoal = goal },
                        shape = RoundedCornerShape(16.dp),
                        color = backgroundColor,
                        shadowElevation = elevation
                    ) {
                        Text(
                            text = goal,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            color = textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Habit Selection
            SectionTitle("Your reading style ⏱️")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                habits.forEach { habit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedHabit = habit }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedHabit == habit,
                            onClick = { selectedHabit = habit },
                            colors = RadioButtonDefaults.colors(selectedColor = TextPrimary)
                        )
                        Text(
                            text = habit,
                            modifier = Modifier.padding(start = 12.dp), // Fixed left -> start
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Continue Button
            Button(
                onClick = { 
                    viewModel.savePreferences(selectedGenres, selectedGoal, selectedHabit)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.large,
                enabled = !isSaving
            ) {
                if (isSaving) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Continue to Home 🚀",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            if (error != null) {
                 Spacer(modifier = Modifier.height(16.dp))
                 Text(
                     text = error!!,
                     color = Color.Red,
                     style = MaterialTheme.typography.bodySmall,
                     textAlign = TextAlign.Center,
                     modifier = Modifier.fillMaxWidth()
                 )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}
