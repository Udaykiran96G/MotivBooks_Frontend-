package com.simats.e_bookmotivation.ui.screens.dashboard


import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.ui.screens.dashboard.components.FloatingBottomNav
import com.simats.e_bookmotivation.ui.screens.dashboard.components.MoodTrackingSection
import com.simats.e_bookmotivation.ui.screens.dashboard.components.PremiumMetricCard

@Composable
fun ProgressScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProgressViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.fetchProgress(context)
    }

    val progressData by viewModel.progressData.collectAsState()
    val moodData by viewModel.moodData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            FloatingBottomNav(
                currentRoute = "Progress",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        if (isLoading && progressData == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            
            // 1. Premium Hero Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFE0F2FE), MaterialTheme.colorScheme.background)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(elevation = 8.dp, shape = CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF0EA5E9), MaterialTheme.colorScheme.primary)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Insights,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Your Progress",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "You're crushing it this week!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF0284C7),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Error display
            if (error != null) {
                item {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            // 2. Upgraded Streak Card
            item { 
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    PremiumStreakCard(currentStreak = progressData?.current_streak ?: 0)
                }
            }



            // 4. Upgraded Metrics Row
            item { 
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    MetricsRow(
                        onNavigate = onNavigate,
                        books = progressData?.total_books_read ?: 0,
                        hours = progressData?.total_hours_read ?: 0,
                        quotes = progressData?.total_quotes_saved ?: 0
                    )
                }
            }

            // 5. Upgraded Mood Tracking (from Journal entries)
            item { 
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    val moodValues = moodData?.mood_values ?: listOf(2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f, 2.5f)
                    // Convert list to CSV for the existing MoodTrackingSection
                    val moodCsv = moodValues.take(6).joinToString(",")
                    val baselineCsv = "2.5,2.5,2.5,2.5,2.5,2.5"
                    MoodTrackingSection(
                        moodBeforeCsv = baselineCsv,
                        moodAfterCsv = moodCsv
                    )
                }
            }

        }
    }
}

@Composable
fun PremiumStreakCard(currentStreak: Int = 0) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFFF9800).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFF9800), Color(0xFFFF5722))
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Streak",
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$currentStreak Days",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFE082), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Top 10% of Readers",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(elevation = 16.dp, shape = CircleShape, spotColor = Color(0xFFFFCC80))
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun MetricsRow(
    onNavigate: (String) -> Unit,
    books: Int = 0,
    hours: Int = 0,
    quotes: Int = 0
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PremiumMetricCard(
            icon = Icons.Outlined.MenuBook,
            iconColor = Color(0xFF10B981),
            bgColor = Color(0xFFECFDF5),
            number = books.toString(),
            label = "Books",
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate("BooksCompleted") }
        )
        PremiumMetricCard(
            icon = Icons.Outlined.AccessTime,
            iconColor = Color(0xFFF59E0B),
            bgColor = Color(0xFFFFFBEB),
            number = hours.toString(),
            label = "Hours",
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate("ReadingAnalytics") }
        )
        PremiumMetricCard(
            icon = Icons.Outlined.FormatQuote,
            iconColor = Color(0xFFEC4899),
            bgColor = Color(0xFFFDF2F8),
            number = quotes.toString(),
            label = "Quotes",
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate("SavedQuotes") }
        )
    }
}

@Composable
fun WeeklyAIReportCard(
    digestText: String = "Keep reading to get your first AI weekly digest!",
    dateRange: String = "This Week"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7))), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Generated", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Weekly Digest",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = dateRange,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = digestText,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
