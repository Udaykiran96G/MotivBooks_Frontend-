package com.simats.e_bookmotivation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.e_bookmotivation.ui.screens.dashboard.BadgesViewModel
import com.simats.e_bookmotivation.network.models.BadgeStatusResponse

data class BadgeData(
    val id: String,
    val title: String, 
    val description: String,
    val icon: ImageVector, 
    val tint: Color, 
    val bgTint: Color,
    val unlocked: Boolean,
    val progress: Float,
    val current: Int,
    val target: Int
)

fun parseIcon(iconName: String): ImageVector = when (iconName) {
    "MenuBook" -> Icons.Default.MenuBook
    "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
    "FormatQuote" -> Icons.Default.FormatQuote
    "EditNote" -> Icons.Default.EditNote
    "MilitaryTech" -> Icons.Default.MilitaryTech
    "Star" -> Icons.Default.Star
    else -> Icons.Default.EmojiEvents
}

fun parseColor(colorName: String): Color = when (colorName) {
    "Primary" -> Color(0xFF3B82F6)
    "Orange" -> Color(0xFFF97316)
    "Yellow" -> Color(0xFFEAB308)
    "Purple" -> Color(0xFFA855F7)
    "Green" -> Color(0xFF22C55E)
    "LightBlue" -> Color(0xFFEFF6FF)
    "LightOrange" -> Color(0xFFFFF7ED)
    "LightYellow" -> Color(0xFFFEF9C3)
    "LightPurple" -> Color(0xFFFAF5FF)
    "LightGreen" -> Color(0xFFF0FDF4)
    else -> Color.Gray
}

val defaultBadges = listOf(
    BadgeStatusResponse("streak_14", "14-Day Streak", "Read for 14 consecutive days", "LocalFireDepartment", "Orange", "LightOrange", false, 0f, 0, 14),
    BadgeStatusResponse("books_5", "5 Books", "Complete 5 books", "MenuBook", "Primary", "LightBlue", false, 0f, 0, 5),
    BadgeStatusResponse("quotes_30", "30 Quotes", "Save 30 inspiring quotes", "FormatQuote", "Purple", "LightPurple", false, 0f, 0, 30),
    BadgeStatusResponse("highlights_100", "100 Highlights", "Highlight 100 passages", "EditNote", "Primary", "LightBlue", false, 0f, 0, 100),
    BadgeStatusResponse("streak_7", "7-Day Reader", "Read every day for a week", "MilitaryTech", "Green", "LightGreen", false, 0f, 0, 7),
    BadgeStatusResponse("action_plans_3", "Growth Master", "Complete 3 Action Plans", "Star", "Yellow", "LightYellow", false, 0f, 0, 3)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onNavigateBack: () -> Unit,
    viewModel: BadgesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchBadges()
    }

    // Merge uiState with defaults
    val displayData = if (uiState.isEmpty()) {
        defaultBadges
    } else {
        uiState
    }

    val badges = displayData.map {
        BadgeData(
            id = it.id,
            title = it.title,
            description = it.description,
            icon = parseIcon(it.icon_name),
            tint = parseColor(it.tint_color),
            bgTint = parseColor(it.bg_color),
            unlocked = it.unlocked,
            progress = it.progress,
            current = it.current_value,
            target = it.target_value
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Badges", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0F172A)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(4.dp), strokeWidth = 2.dp, color = Color(0xFF3B82F6))
                    } else {
                        IconButton(onClick = { viewModel.fetchBadges() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF0F172A))
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                if (error != null && uiState.isEmpty()) {
                   Text(
                       text = "Offline Mode: Showing default badges",
                       color = Color.Gray,
                       fontSize = 12.sp,
                       modifier = Modifier.padding(bottom = 8.dp).align(Alignment.CenterHorizontally)
                   )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Overall Progress", color = Color(0xFF94A3B8), fontSize = 14.sp)
                            Text("${badges.count { it.unlocked }} Badges Unlocked", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(28.dp))
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(badges) { badge ->
                        BadgeCard(badge)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeData) {
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(badge.bgTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = badge.icon, contentDescription = null, tint = badge.tint, modifier = Modifier.size(32.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = badge.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = badge.description, fontSize = 12.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center, lineHeight = 16.sp)
            }

            if (badge.unlocked) {
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "UNLOCKED",
                        color = Color(0xFF22C55E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    LinearProgressIndicator(
                        progress = badge.progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = badge.tint,
                        trackColor = badge.bgTint
                    )
                }
            }
        }
    }
}
