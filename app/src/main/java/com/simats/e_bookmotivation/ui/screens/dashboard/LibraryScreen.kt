@file:Suppress("DEPRECATION")
package com.simats.e_bookmotivation.ui.screens.dashboard


import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.ui.screens.dashboard.components.*

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigate: (String) -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("All") }
    var isGridView by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val books by viewModel.books.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Fetch when sort changes
    LaunchedEffect(selectedSort) {
        viewModel.fetchLibrary(sort = selectedSort)
    }

    val sortOptions = listOf("All", "Most Popular", "Newest", "Highest Rated", "Most Highlighted")


    // Live filtering
    val filteredBooks = remember(searchQuery, books) {
        if (searchQuery.isEmpty()) books
        else books.filter { 
            it.title.contains(searchQuery, ignoreCase = true) || 
            it.author.contains(searchQuery, ignoreCase = true) 
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            FloatingBottomNav(
                currentRoute = "Library",
                onNavigate = onNavigate
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            LibraryHeader(
                onNavigateToStats = { onNavigate("LibraryStats") }
            )

            // STEP 2: INSERT SEARCH + FILTER ROW
            SearchBarWithFilter(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterSheet = true }
            )

            // STEP 6: CONVERT EXISTING SORT TABS TO CHIP ROW
            SortChips(
                options = sortOptions,
                selectedOption = selectedSort,
                onOptionSelected = { selectedSort = it },
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // STEP 7: ADD VIEW TOGGLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                ViewToggle(isGridView = isGridView, onToggle = { isGridView = it })
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.surfaceVariant)

            // STEP 8 & 9: MODIFY BOOK LIST RENDERING & EMPTY STATE
            if (filteredBooks.isEmpty()) {
                EmptyLibraryState(onReset = { 
                    searchQuery = "" 
                    selectedSort = "All"
                })
            } else {
                if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredBooks) { book ->
                            BookGridCard(
                                title = book.title,
                                author = book.author,
                                onClick = { onNavigate("BookDetails/${book.id}/${android.net.Uri.encode(book.title)}/${android.net.Uri.encode(book.author)}") }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredBooks) { book ->
                            BookListItem(
                                title = book.title,
                                author = book.author,
                                onClick = { onNavigate("BookDetails/${book.id}/${android.net.Uri.encode(book.title)}/${android.net.Uri.encode(book.author)}") }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            onDismiss = { showFilterSheet = false },
            onApply = { author, startYear, endYear, rating, lang, cats, goals, recent ->
                showFilterSheet = false
                val tagsString = (cats + goals).joinToString(",")
                viewModel.fetchLibrary(
                    sort = selectedSort,
                    author = if (author.isBlank()) null else author,
                    yearMin = if (startYear.isBlank()) null else startYear,
                    yearMax = if (endYear.isBlank()) null else endYear,
                    rating = rating?.replace("+", ""),
                    language = lang,
                    tags = if (tagsString.isBlank()) null else tagsString,
                    recentlyAdded = if (recent) "true" else "false"
                )
            }
        )
    }
}

@Composable
fun LibraryHeader(onNavigateToStats: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 32.dp, bottom = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "My Library",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Your personal growth collection",
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

@Composable
fun SearchBarWithFilter(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        FilterButton(onClick = onFilterClick)
    }
}

@Composable
fun EmptyLibraryState(onReset: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Your Library is Empty",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Discover new books or try adjusting your current filters to find what you're looking for.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 15.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(56.dp).fillMaxWidth().padding(horizontal = 16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
        ) {
            Text("Reset Filters", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}
