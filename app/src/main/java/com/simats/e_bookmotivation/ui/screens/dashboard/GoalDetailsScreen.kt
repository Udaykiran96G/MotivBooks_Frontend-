package com.simats.e_bookmotivation.ui.screens.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsScreen(
    onNavigateBack: () -> Unit,
    onNavigate: (String) -> Unit = {},
    onEditClick: () -> Unit = {},
    viewModel: GoalDetailsViewModel = viewModel(),
    progressViewModel: ProgressViewModel = viewModel() // to get title and progress
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val detailsData by viewModel.goalDetailsData.collectAsState()
    val isDetailsLoading by viewModel.isLoading.collectAsState()
    
    val progressData by progressViewModel.progressData.collectAsState()
    val isProgressLoading by progressViewModel.isLoading.collectAsState()
    
    val isLoading = isDetailsLoading || isProgressLoading
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchGoalDetails()
        progressViewModel.fetchProgress(context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Goal Details", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onEditClick) {
                        Text(
                            "Edit",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val title = progressData?.active_goal_title ?: "Set a Goal"
            val booksCompleted = progressData?.active_goal_books_completed ?: 0
            val totalBooks = progressData?.active_goal_total_books ?: 1
            
            val deadlineText = detailsData?.deadline?.let { "On track to complete by $it" } ?: "No deadline set"
            val reflections = detailsData?.reflections_written ?: 0
            val challenges = detailsData?.challenges_done ?: 0

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp)
            ) {
                
                // 1. Premium Arc Progress Card
                item {
                    GoalCircularProgressCard(
                        title = title,
                        booksCompleted = booksCompleted,
                        totalBooks = totalBooks,
                        deadlineText = deadlineText
                    )
                }
                
                // 2. Premium Image-Matched Stat Cards
                item {
                    PremiumGoalStatCard(
                        icon = Icons.Outlined.LibraryBooks,
                        iconBgColor = Color(0xFFEFF6FF),
                        iconTintColor = MaterialTheme.colorScheme.primary,
                        title = "Books Completed",
                        subtitle = "$booksCompleted of $totalBooks books",
                        onClick = { onNavigate("BooksCompleted") }
                    )
                }
                
                item {
                    PremiumGoalStatCard(
                        icon = Icons.Outlined.EditNote,
                        iconBgColor = Color(0xFFFAF5FF),
                        iconTintColor = Color(0xFFA855F7),
                        title = "Reflections",
                        subtitle = "$reflections entries written",
                        onClick = { onNavigate("Reflections") }
                    )
                }
                
                item {
                    PremiumGoalStatCard(
                        icon = Icons.Outlined.Stars,
                        iconBgColor = Color(0xFFFFF7ED),
                        iconTintColor = Color(0xFFF97316),
                        title = "Challenges",
                        subtitle = "$challenges micro-challenges done",
                        onClick = { onNavigate("Challenges") }
                    )
                }
                
                // 3. Image-Matched Add Milestone Button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AddMilestoneButton(onClick = { showBottomSheet = true })
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .padding(bottom = 32.dp) // extra padding for bottom navigation area
            ) {
                Text(
                    text = "Add Milestone",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Break your larger goal down into smaller, achievable steps.",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                var milestoneName by remember { mutableStateOf("") }
                
                OutlinedTextField(
                    value = milestoneName,
                    onValueChange = { milestoneName = it },
                    placeholder = { Text("e.g., Read first 50 pages of Atomic Habits", color = MaterialTheme.colorScheme.secondary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, Color(0xFF2563EB)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Save Milestone",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalCircularProgressCard(
    title: String,
    booksCompleted: Int,
    totalBooks: Int,
    deadlineText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center, 
                modifier = Modifier.size(180.dp)
            ) {
                val progressSurfaceVariant = MaterialTheme.colorScheme.surfaceVariant
                val progressPrimary = MaterialTheme.colorScheme.primary
                
                val safeTotal = if (totalBooks > 0) totalBooks else 1
                val progressRatio = (booksCompleted.toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
                val progressPercentage = (progressRatio * 100).toInt()
                val sweepAngle = progressRatio * 360f

                Canvas(modifier = Modifier.size(160.dp)) {
                    // Gray background circle
                    drawCircle(
                        color = progressSurfaceVariant,
                        style = Stroke(width = 16.dp.toPx())
                    )
                    // Gradient Sweep progress arc
                    val sweepGradient = Brush.sweepGradient(
                        colors = listOf(progressPrimary, Color(0xFF6366F1), Color(0xFF8B5CF6))
                    )
                    drawArc(
                        brush = sweepGradient,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$progressPercentage%",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Completed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = deadlineText,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PremiumGoalStatCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFCBD5E1))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Navigate",
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun AddMilestoneButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Add Milestone",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
