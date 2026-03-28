package com.simats.e_bookmotivation.ui.screens.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.simats.e_bookmotivation.ui.screens.dashboard.components.*
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.AutoAwesome

import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.e_bookmotivation.network.models.DashboardResponse

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardHomeViewModel = viewModel()
) {
    val uiStateResponse by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Map response to default UI state or use raw response logic
    val uiState = remember(uiStateResponse) { 
        if (uiStateResponse != null) {
            val r = uiStateResponse!!
            DashboardUiState(
                date = r.date,
                userName = r.userName,
                streakDays = r.streakDays,
                goalTitle = r.goalTitle,
                goalSubtitle = r.goalSubtitle,
                goalBooksRead = r.goalBooksRead,
                goalTotalBooks = r.goalTotalBooks,
                goalUnit = r.goalUnit,
                currentBook = r.currentBook?.let { book ->
                    CurrentBookState(
                        id = book.id,
                        title = book.title,
                        author = book.author,
                        progress = book.progress,
                        isPremium = book.isPremium,
                        currentChapter = book.currentChapter,
                        totalChapters = book.totalChapters
                    )
                },
                topBooks = r.topBooks.map { BookSummary(it.id, it.title, it.author, it.coverUrl) },
                monthBooks = r.monthBooks.map { BookSummary(it.id, it.title, it.author, it.coverUrl) },
                trendingBooks = r.trendingBooks.map { BookSummary(it.id, it.title, it.author, it.coverUrl) },
                aiRecommendation = AiPickState(r.aiRecommendation.title, r.aiRecommendation.description, r.aiRecommendation.sparkleIcon),
                badges = r.badges.map {
                    BadgeUiState(
                        title = it.title,
                        dateEarned = it.date_earned,
                        iconName = it.icon_name,
                        tintColor = it.tint_color,
                        bgColor = it.bg_color
                    )
                },
                totalNotesTaken = r.total_notes_taken,
                dailyBoost = r.daily_boost,
                communityInspiration = CommunityState(r.communityInspiration.avatarUrls, r.communityInspiration.message)
            )
        } else {
            DashboardUiState() // Fallback to loading/empty state
        }
    }
    
    var visibleItems by remember { mutableStateOf(0) }

    val lifecycleOwner = LocalLifecycleOwner.current

    // Staggered appearance effect
    LaunchedEffect(Unit) {
        for (i in 1..11) { // Increased to 11 for the new DailyBoostCard
            delay(150)
            visibleItems = i
        }
    }

    // Refresh data on Resume (e.g., when coming back from Goal selection)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchDashboardData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            FloatingBottomNav(
                currentRoute = "Home",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        if (isLoading && uiStateResponse == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 1,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        GreetingHeader(
                            date = uiState.date,
                            userName = uiState.userName,
                            notificationCount = uiStateResponse?.unreadNotificationCount ?: 0,
                            onNotificationsClick = { onNavigate("Notifications") },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 2,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        QuickActionsGrid(
                            onActionClick = onNavigate,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 3,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        ActiveGoalCard(
                            title = uiState.goalTitle,
                            subtitle = uiState.goalSubtitle,
                            booksRead = uiState.goalBooksRead,
                            totalBooks = uiState.goalTotalBooks,
                            unit = uiState.goalUnit,
                            onEditClick = { onNavigate("Goal") },
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }


                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 5,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        val book = uiState.currentBook
                        if (book != null) {
                            CurrentBookCard(
                                title = book.title,
                                author = book.author,
                                progress = book.progress,
                                isPremium = book.isPremium,
                                currentChapter = book.currentChapter,
                                totalChapters = book.totalChapters,
                                onContinueClick = {
                                    val encodedTitle = android.net.Uri.encode(book.title)
                                    val encodedAuthor = android.net.Uri.encode(book.author)
                                    onNavigate("Reader/${book.id}/$encodedTitle/$encodedAuthor")
                                },
                                onAudioClick = { 
                                    val encodedTitle = android.net.Uri.encode(book.title)
                                    val encodedAuthor = android.net.Uri.encode(book.author)
                                    onNavigate("AudioMode/${book.id}/$encodedTitle/$encodedAuthor")
                                },
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        } else {
                            CurrentBookCard(
                                title = "",
                                author = "",
                                progress = 0f,
                                isPremium = false,
                                currentChapter = 0,
                                totalChapters = 0,
                                onContinueClick = { onNavigate("Library") },
                                onAudioClick = { },
                                modifier = Modifier.padding(horizontal = 20.dp),
                                isEmpty = true
                            )
                        }
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 7,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        BookListSection(
                            title = "Books of the Month",
                            books = uiState.monthBooks,
                            emptyMessage = "New books coming soon",
                            onViewAllClick = { onNavigate("Library") },
                            onBookClick = { book ->
                                val encodedTitle = android.net.Uri.encode(book.title)
                                val encodedAuthor = android.net.Uri.encode(book.author)
                                onNavigate("BookDetails/${book.id}/$encodedTitle/$encodedAuthor")
                            }
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visibleItems >= 9,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        BookListSection(
                            title = "Trending in Motivation",
                            books = uiState.trendingBooks,
                            emptyMessage = "Stay tuned for trending content",
                            onViewAllClick = { onNavigate("Library") },
                            onBookClick = { book ->
                                val encodedTitle = android.net.Uri.encode(book.title)
                                val encodedAuthor = android.net.Uri.encode(book.author)
                                onNavigate("BookDetails/${book.id}/$encodedTitle/$encodedAuthor")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookListSection(
    title: String,
    books: List<BookSummary>,
    emptyMessage: String = "No books found",
    onViewAllClick: () -> Unit,
    onBookClick: (BookSummary) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        SectionHeader(
            title = title,
            onViewAllClick = onViewAllClick,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (books.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(emptyMessage, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(books) { book ->
                    BookCard(
                        title = book.title,
                        author = book.author,
                        onClick = { onBookClick(book) }
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeIcon(badge: BadgeUiState) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    try { Color(android.graphics.Color.parseColor(badge.bgColor)) } 
                    catch (e: Exception) { MaterialTheme.colorScheme.surfaceVariant }, 
                    CircleShape
                ),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Text(
                text = badge.title.take(1).uppercase(), 
                fontWeight = FontWeight.ExtraBold, 
                fontSize = 18.sp,
                color = try { Color(android.graphics.Color.parseColor(badge.tintColor)) } 
                        catch (e: Exception) { MaterialTheme.colorScheme.primary }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = badge.title, 
            fontSize = 11.sp, 
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}




