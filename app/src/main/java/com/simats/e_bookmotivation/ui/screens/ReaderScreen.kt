package com.simats.e_bookmotivation.ui.screens


import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt
import com.simats.e_bookmotivation.ui.theme.LocalReadingPreferences
import com.simats.e_bookmotivation.util.TranslationHelper
import kotlinx.coroutines.launch

import androidx.lifecycle.viewmodel.compose.viewModel
import com.simats.e_bookmotivation.network.models.ChapterResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    bookId: Int = 1,
    title: String = "Atomic Habits",
    author: String = "James Clear", 
    onNavigateBack: () -> Unit,
    viewModel: ReaderViewModel = viewModel()
) {
    val chapters by viewModel.chapters.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val translatedPageContent by viewModel.translatedPageContent.collectAsState()
    val isPageTranslated by viewModel.isPageTranslated.collectAsState()
    val translatedSelection by viewModel.translatedSelection.collectAsState()
    val translationError by viewModel.translationError.collectAsState()
    
    val aiSummary by viewModel.aiSummary.collectAsState()
    val isSummarizing by viewModel.isSummarizing.collectAsState()
    val summaryError by viewModel.summaryError.collectAsState()
    
    var currentChapterIndex by remember { mutableIntStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // Track book open for streak + books read
    LaunchedEffect(bookId) {
        viewModel.fetchChapters(bookId)
        try {
            val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            val token = prefs.getString("auth_token", null)
            if (token != null) {
                com.simats.e_bookmotivation.network.RetrofitClient.subPageApi.trackBookOpen(
                    "Bearer $token",
                    mapOf("book_id" to bookId, "title" to title, "author" to author, "is_premium" to false)
                )
            }
        } catch (_: Exception) {}
    }

    // Periodic Progress Tracking
    LaunchedEffect(Unit) {
        var elapsedSeconds = 0
        while (true) {
            kotlinx.coroutines.delay(1000)
            elapsedSeconds++
            
            // Every 1 minute, sync with backend
            if (elapsedSeconds >= 60) {
                viewModel.trackReadingProgress(bookId, 1, 0) // Track 1 minute, 0 pages for now (page estimation can be added later)
                elapsedSeconds = 0
            }
        }
    }

    // Reset page translation when chapter changes
    LaunchedEffect(currentChapterIndex) {
        viewModel.revertPage()
        viewModel.clearTranslatedSelection()
        // Increment page count in backend when chapter changes
        viewModel.trackReadingProgress(bookId, 0, 1)
    }

    // State for our custom popup toolbar
    var showCustomToolbar by remember { mutableStateOf(false) }
    var selectionRect by remember { mutableStateOf(Rect.Zero) }
    
    // State for the three-dots menu
    var showMenu by remember { mutableStateOf(false) }
    
    // Translation UI states
    var showLanguagePicker by remember { mutableStateOf(false) }
    var translationMode by remember { mutableStateOf("") } // "selection" or "page"
    var pendingSelectedText by remember { mutableStateOf("") }
    
    // Captured copy callback
    var onCopy: (() -> Unit)? by remember { mutableStateOf(null) }

    // Create a custom TextToolbar that passes selection rect to our state, avoiding standard popup
    val customTextToolbar = remember {
        object : TextToolbar {
            override val status: TextToolbarStatus = TextToolbarStatus.Hidden
            override fun hide() {
                showCustomToolbar = false
            }

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?
            ) {
                selectionRect = rect
                onCopy = onCopyRequested
                showCustomToolbar = true
            }
        }
    }
    
    val customSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.primary,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )

    val readingPrefState = LocalReadingPreferences.current
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    
    val bgColor = when(readingPrefState.value.theme) {
        "Sepia" -> Color(0xFFFDEBD0)
        "Dark" -> Color(0xFFFDEBD0) // Night Light (Amber)
        else -> Color(0xFFFAF9F6) // Light
    }
    
    val titleColor = when(readingPrefState.value.theme) {
        "Sepia" -> Color(0xFF4E342E)
        "Dark" -> Color(0xFF4E342E) // Night Light (Warm Deep Brown)
        else -> MaterialTheme.colorScheme.onBackground // Light
    }
    
    val textColor = when(readingPrefState.value.theme) {
        "Sepia" -> Color(0xFF5D4037)
        "Dark" -> Color(0xFF5D4037) // Night Light (Warm Brown)
        else -> Color(0xFF1E293B) // Light
    }
    
    val bottomBarBg = when(readingPrefState.value.theme) {
        "Dark" -> MaterialTheme.colorScheme.onBackground
        else -> Color.White
    }
    
    val iconTint = when(readingPrefState.value.theme) {
        "Dark" -> Color(0xFF5D4037)
        else -> MaterialTheme.colorScheme.onBackground
    }

    // Bottom Sheet for Language Picker
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    if (showLanguagePicker) {
        ModalBottomSheet(
            onDismissRequest = { showLanguagePicker = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Translate to",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Language grid
                val languages = TranslationHelper.supportedLanguages
                for (rowIndex in languages.indices step 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (colIndex in 0 until 3) {
                            val langIndex = rowIndex + colIndex
                            if (langIndex < languages.size) {
                                val lang = languages[langIndex]
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 5.dp)
                                        .clickable {
                                            showLanguagePicker = false
                                            if (translationMode == "selection") {
                                                viewModel.translateSelectedText(pendingSelectedText, lang.code)
                                            } else if (translationMode == "page") {
                                                val chapterContent = chapters.getOrNull(currentChapterIndex)?.content ?: ""
                                                viewModel.translatePage(chapterContent, lang.code)
                                            }
                                        }
                                ) {
                                    Text(
                                        text = lang.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp)
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Show translation error as a toast
    LaunchedEffect(translationError) {
        translationError?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(summaryError) {
        summaryError?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    // AI Summary Dialog
    if (aiSummary != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearSummary() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("AI Summary", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text(
                    text = aiSummary!!,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearSummary() }) {
                    Text("Got it")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        )
    }

    Scaffold(
        containerColor = bgColor, // Dynamic paper color
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = title.uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                // More options in top app bar need dynamic tint
                actions = {
                    IconButton(onClick = { /* Bookmark */ }) {
                        Icon(imageVector = Icons.Outlined.BookmarkBorder, contentDescription = "Bookmark", tint = iconTint)
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More", tint = iconTint)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(bottomBarBg)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Translate Page", color = iconTint) },
                                leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null, tint = iconTint) },
                                onClick = { 
                                    showMenu = false
                                    translationMode = "page"
                                    showLanguagePicker = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("AI Summary", color = iconTint) },
                                leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = iconTint) },
                                onClick = { 
                                    showMenu = false
                                    val content = chapters.getOrNull(currentChapterIndex)?.content ?: ""
                                    viewModel.summarizeContent(content)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(bottomBarBg)
                    .fillMaxWidth()
            ) {
                HorizontalDivider(color = if (readingPrefState.value.theme == "Dark") Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant)
                
                // Controls Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Font sizes
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.dp, if (readingPrefState.value.theme == "Dark") Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant),
                        color = bottomBarBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("A-", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = iconTint, modifier = Modifier.clickable {
                                if (readingPrefState.value.fontSize > 12) {
                                    readingPrefState.value = readingPrefState.value.copy(fontSize = readingPrefState.value.fontSize - 1)
                                }
                            })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${readingPrefState.value.fontSize}", fontSize = 14.sp, color = iconTint)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("A+", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = iconTint, modifier = Modifier.clickable {
                                if (readingPrefState.value.fontSize < 32) {
                                    readingPrefState.value = readingPrefState.value.copy(fontSize = readingPrefState.value.fontSize + 1)
                                }
                            })
                        }
                    }

                    // Alignment
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.dp, if (readingPrefState.value.theme == "Dark") Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant),
                        color = bottomBarBg
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FormatAlignLeft, contentDescription = "Left Align", modifier = Modifier.size(18.dp).clickable { }, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(imageVector = Icons.Default.FormatAlignJustify, contentDescription = "Justify", modifier = Modifier.size(18.dp).clickable { }, tint = iconTint)
                        }
                    }

                    // Night Light Toggle
                    Surface(
                        shape = CircleShape,
                        border = BorderStroke(1.dp, if (readingPrefState.value.theme == "Dark") Color(0xFF1E293B) else MaterialTheme.colorScheme.surfaceVariant),
                        color = bottomBarBg,
                        modifier = Modifier.clickable { 
                            val newTheme = if (readingPrefState.value.theme == "Dark") "Light" else "Dark"
                            readingPrefState.value = readingPrefState.value.copy(theme = newTheme)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (readingPrefState.value.theme == "Dark") Icons.Default.Nightlight else Icons.Outlined.LightMode, 
                                contentDescription = "Night Light", 
                                modifier = Modifier.size(20.dp), 
                                tint = if (readingPrefState.value.theme == "Dark") Color(0xFFFFB74D) else Color(0xFFF59E0B)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (readingPrefState.value.theme == "Dark") "Warm Mode: ON" else "Warm Mode: OFF",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = iconTint
                            )
                        }
                    }
                }

                // Chapter Navigation Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.clickable(enabled = currentChapterIndex > 0) { 
                            currentChapterIndex--
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft, 
                            contentDescription = "Prev", 
                            tint = if (currentChapterIndex > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), 
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Prev Chapter", 
                            fontSize = 14.sp, 
                            color = if (currentChapterIndex > 0) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                    Text(
                        text = "Chapter ${if (chapters.isEmpty()) 0 else currentChapterIndex + 1} of ${chapters.size}", 
                        fontSize = 14.sp, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.clickable(enabled = currentChapterIndex < chapters.size - 1) { 
                            currentChapterIndex++
                        }
                    ) {
                        Text(
                            text = "Next", 
                            fontSize = 14.sp, 
                            color = if (currentChapterIndex < chapters.size - 1) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight, 
                            contentDescription = "Next", 
                            tint = if (currentChapterIndex < chapters.size - 1) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f), 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // "Show Original" chip when page is translated
                if (isPageTranslated) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp)
                            .clickable { viewModel.revertPage() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Show Original",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Revert",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                // Translating indicator
                if (isTranslating) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Summarizing indicator
                if (isSummarizing) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "AI is thinking…",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(text = error ?: "Error loading content", color = Color.Red, textAlign = TextAlign.Center)
                    }
                } else if (chapters.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No content available for this book.", color = textColor, textAlign = TextAlign.Center)
                    }
                } else {
                    val currentChapter = chapters[currentChapterIndex]
                    val displayContent = translatedPageContent ?: currentChapter.content

                    Text(
                        text = currentChapter.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = titleColor,
                        modifier = Modifier.padding(bottom = 24.dp),
                        lineHeight = 38.sp
                    )
                    
                    CompositionLocalProvider(
                        LocalTextToolbar provides customTextToolbar,
                        LocalTextSelectionColors provides customSelectionColors
                    ) {
                        SelectionContainer {
                            Column {
                                Text(
                                    text = displayContent,
                                    fontSize = readingPrefState.value.fontSize.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = textColor,
                                    lineHeight = (readingPrefState.value.fontSize * 1.6).sp,
                                    textAlign = TextAlign.Justify,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
                            }
                        }
                    }
                }
                // Extra padding at bottom to clear the bottom bar comfortably
                Spacer(modifier = Modifier.height(60.dp))
            }

            // Translated selection card
            if (translatedSelection != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 80.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shadowElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Translate,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Translation",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearTranslatedSelection() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = translatedSelection!!,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }

            // Custom Context Menu Popup
            if (showCustomToolbar) {
                // Calculate position above the selection rect. 
                // We use selectionRect.top to place it above the selected text.
                val yOffset = selectionRect.top.roundToInt() - 100 // Shifted up to be above
                val xOffset = 0 // Center horizontally relative to alignment

                Popup(
                    alignment = Alignment.TopCenter,
                    offset = IntOffset(xOffset, yOffset),
                    onDismissRequest = {
                        showCustomToolbar = false
                        focusManager.clearFocus()
                    },
                    properties = PopupProperties(focusable = true)
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = bottomBarBg,
                        shadowElevation = 4.dp,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            ContextMenuItem(
                                icon = Icons.Outlined.Save, 
                                label = "Save", 
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { 
                                    showCustomToolbar = false 
                                    // Trigger copy to capture selected text
                                    onCopy?.invoke()
                                    val selectedText = clipboardManager.getText()?.text ?: ""
                                    
                                    scope.launch {
                                        try {
                                            val prefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                            val token = prefs.getString("auth_token", null)
                                            if (token != null && selectedText.isNotEmpty()) {
                                                // POST quote to backend
                                                val quoteMap = mapOf(
                                                    "quote" to selectedText,
                                                    "author" to author,
                                                    "book" to title
                                                )
                                                val quoteResponse = com.simats.e_bookmotivation.network.RetrofitClient.subPageApi.addSavedQuote("Bearer $token", quoteMap)
                                                if (quoteResponse.isSuccessful) {
                                                    android.widget.Toast.makeText(context, "Saved to Quotes!", android.widget.Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            // Handle error
                                        }
                                    }
                                }
                            )
                            Box(modifier = Modifier.height(30.dp).padding(vertical = 4.dp)) { HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = MaterialTheme.colorScheme.surfaceVariant) }
                            ContextMenuItem(
                                icon = Icons.Outlined.Translate, 
                                label = "Translate", 
                                onClick = { 
                                    showCustomToolbar = false
                                    // Capture selected text via clipboard
                                    onCopy?.invoke()
                                    val selectedText = clipboardManager.getText()?.text ?: ""
                                    if (selectedText.isNotEmpty()) {
                                        pendingSelectedText = selectedText
                                        translationMode = "selection"
                                        showLanguagePicker = true
                                    }
                                }
                            )
                            Box(modifier = Modifier.height(30.dp).padding(vertical = 4.dp)) { HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = MaterialTheme.colorScheme.surfaceVariant) }
                            ContextMenuItem(
                                icon = Icons.Outlined.AutoAwesome, 
                                label = "AI Summary", 
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { 
                                    showCustomToolbar = false
                                    onCopy?.invoke()
                                    val selectedText = clipboardManager.getText()?.text ?: ""
                                    if (selectedText.isNotEmpty()) {
                                        viewModel.summarizeContent(selectedText)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContextMenuItem(
    icon: ImageVector,
    label: String,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = color,
            fontWeight = if (color == MaterialTheme.colorScheme.primary) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
