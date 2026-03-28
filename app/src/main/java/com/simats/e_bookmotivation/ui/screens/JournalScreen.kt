package com.simats.e_bookmotivation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.e_bookmotivation.network.models.JournalEntryResponse
import com.simats.e_bookmotivation.ui.screens.dashboard.components.MoodTrackingSection
import com.simats.e_bookmotivation.ui.screens.dashboard.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onNavigate: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: JournalViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val todayPrompt by viewModel.todayPrompt.collectAsState()
    val todayDate by viewModel.todayDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val context = LocalContext.current
    var isHistoryMode by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            isHistoryMode = true // Switch to history after saving
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF4F6FA), // Light premium background
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Growth Journal",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth().padding(start = if (isHistoryMode) 0.dp else 24.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    TextButton(onClick = { isHistoryMode = !isHistoryMode }) {
                        Text(
                            text = if (isHistoryMode) "Write" else "History",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (isLoading && entries.isEmpty() && todayPrompt.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (isHistoryMode) {
                val moodData by viewModel.moodData.collectAsState()
                val moodCsv = moodData?.mood_values?.joinToString(",") ?: ""
                JournalHistoryView(
                    entries = entries, 
                    padding = padding,
                    moodBeforeCsv = "2.5,2.5,2.5,2.5,2.5,2.5",
                    moodAfterCsv = moodCsv,
                    onEntryClick = { entryId ->
                        onNavigate("JournalDetail/$entryId")
                    }
                )
            } else {
                JournalWriteView(
                    prompt = todayPrompt,
                    date = todayDate,
                    isLoading = isLoading,
                    padding = padding,
                    onSave = { content, mood ->
                        viewModel.addJournalEntry(content, mood, todayPrompt) {}
                    }
                )
            }
        }
    }
}

@Composable
fun JournalWriteView(
    prompt: String,
    date: String,
    isLoading: Boolean,
    padding: PaddingValues,
    onSave: (String, String) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<String?>(null) } // "rough", "okay", "great"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp)
    ) {
        // Daily Reflection Prompt Card
        Card(
            modifier = Modifier.fillMaxWidth().shadow(0.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DAILY REFLECTION",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = date,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF94A3B8)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = prompt.ifEmpty { "What is one small step you took today that brought you closer to your goal?" },
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Text Input Card
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f).shadow(0.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            TextField(
                value = content,
                onValueChange = { content = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = {
                    Text(
                        "Write your thoughts here...",
                        color = Color(0xFF94A3B8),
                        fontSize = 18.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, lineHeight = 26.sp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "How are you feeling?",
            color = Color(0xFF64748B),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Selector
        Card(
            modifier = Modifier.fillMaxWidth().shadow(0.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MoodOption("Rough", Icons.Outlined.SentimentDissatisfied, "rough", selectedMood == "rough") { selectedMood = "rough" }
                MoodOption("Okay", Icons.Outlined.SentimentNeutral, "okay", selectedMood == "okay") { selectedMood = "okay" }
                MoodOption("Great", Icons.Outlined.SentimentSatisfiedAlt, "great", selectedMood == "great") { selectedMood = "great" }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSave(content, selectedMood ?: "okay") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = content.isNotBlank() && selectedMood != null && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Entry", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MoodOption(
    label: String,
    icon: ImageVector,
    id: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0xFF94A3B8),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun JournalHistoryView(
    entries: List<JournalEntryResponse>,
    padding: PaddingValues,
    moodBeforeCsv: String,
    moodAfterCsv: String,
    onEntryClick: (Int) -> Unit
) {
    if (entries.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text("No journal entries yet. Tab 'Write' to reflect today.", color = Color(0xFF94A3B8))
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                MoodTrackingSection(
                    moodBeforeCsv = moodBeforeCsv,
                    moodAfterCsv = moodAfterCsv,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            
            item {
                Text(
                    text = "Reflections History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(entries) { entry ->
                JournalEntryHistoryCard(entry, onClick = { onEntryClick(entry.id) })
            }
        }
    }
}

@Composable
fun JournalEntryHistoryCard(entry: JournalEntryResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(0.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mood icon
                    val icon = when(entry.mood) {
                        "rough" -> Icons.Outlined.SentimentDissatisfied
                        "great" -> Icons.Outlined.SentimentSatisfiedAlt
                        else -> Icons.Outlined.SentimentNeutral
                    }
                    val iconColor = when(entry.mood) {
                        "rough" -> Color(0xFFEF4444) // Red
                        "great" -> Color(0xFF10B981) // Green
                        else -> Color(0xFFF59E0B) // Yellow/Neutral
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = if (entry.prompt.isNotEmpty()) entry.prompt else "Daily Reflection", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 15.sp, 
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = entry.content,
                fontSize = 15.sp,
                color = Color(0xFF475569),
                lineHeight = 24.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = entry.date_created, 
                fontSize = 12.sp, 
                color = Color(0xFF94A3B8), 
                fontWeight = FontWeight.Medium
            )
        }
    }
}
