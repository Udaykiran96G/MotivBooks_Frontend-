package com.simats.e_bookmotivation.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val context = LocalContext.current

    // Show toast when error or success changes
    LaunchedEffect(error) {
        error?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    LaunchedEffect(successMessage) {
        successMessage?.let { Toast.makeText(context, "✅ $it", Toast.LENGTH_SHORT).show() }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Set a New Goal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
        ) {
            item {
                Text(
                    text = "What would you like to focus on?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Choose a goal to help us personalize your daily recommendations and tracking.",
                    fontSize = 15.sp,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 16.dp),
                    lineHeight = 22.sp
                )
            }

            val goals = listOf(
                Triple("read_12", "Read 12 Books this Year", "A great starting point for building a habit."),
                Triple("read_daily", "Read 30 Minutes Daily", "Focus on consistency rather than volume."),
                Triple("skill", "Master a New Skill", "Dive deep into non-fiction and educational books.")
            )

            items(goals) { (id, title, subtitle) ->
                GoalOption(
                    title = title,
                    subtitle = subtitle,
                    icon = when(id) {
                        "read_12" -> Icons.Outlined.Lightbulb
                        "read_daily" -> Icons.Default.CheckCircle
                        else -> Icons.Default.AutoGraph
                    },
                    iconColor = when(id) {
                        "read_12" -> Color(0xFFEAB308)
                        "read_daily" -> Color(0xFF10B981)
                        else -> Color(0xFF6366F1)
                    },
                    bgTint = when(id) {
                        "read_12" -> Color(0xFFFEF9C3)
                        "read_daily" -> Color(0xFFD1FAE5)
                        else -> Color(0xFFDBEAFE)
                    },
                    isSelected = selectedGoal == id,
                    onClick = { selectedGoal = id },
                    onSetGoal = {
                        val (totalValue, goalType, goalUnit) = when(id) {
                            "read_12" -> Triple(12, "BOOKS", "books")
                            "read_daily" -> Triple(30, "MINUTES", "mins")
                            "skill" -> Triple(5, "BOOKS", "books")
                            else -> Triple(1, "BOOKS", "books")
                        }
                        viewModel.updateGoal(title, subtitle, totalValue, goalType, goalUnit, onSuccess = {
                            onNavigateBack()
                        })
                    },
                    isLoading = isLoading && selectedGoal == id
                )
            }
        }
    }
}

@Composable
fun GoalOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    bgTint: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSetGoal: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) bgTint.copy(alpha = 0.3f) else Color.White),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, iconColor) else null,
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(bgTint.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF1E293B))
                    Text(text = subtitle, fontSize = 13.sp, color = Color(0xFF64748B))
                }
            }
            
            if (isSelected) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSetGoal,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iconColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Set Goal", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
