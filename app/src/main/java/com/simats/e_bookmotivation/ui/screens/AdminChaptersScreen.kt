package com.simats.e_bookmotivation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.AdminChapterRequest
import com.simats.e_bookmotivation.network.models.AdminChapterResponse
import com.simats.e_bookmotivation.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AdminChaptersScreen(
    bookId: Int,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val token = "Bearer ${RetrofitClient.getAuthToken()}"

    var chapTitle by remember { mutableStateOf("") }
    var chapContent by remember { mutableStateOf("") }
    var chapOrder by remember { mutableStateOf("1") }
    var isAdding by remember { mutableStateOf(false) }
    var chapters by remember { mutableStateOf<List<AdminChapterResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var bookTitle by remember { mutableStateOf("Book #$bookId") }

    fun loadChapters() {
        scope.launch {
            try {
                val response = RetrofitClient.adminApi.listChapters(token, bookId)
                if (response.isSuccessful) {
                    chapters = response.body() ?: emptyList()
                    val maxOrder = chapters.maxOfOrNull { it.order } ?: 0
                    chapOrder = (maxOrder + 1).toString()
                }
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadChapters()
        // Load book title
        try {
            val resp = RetrofitClient.libraryApi.getBookDetails(token, bookId)
            if (resp.isSuccessful) { bookTitle = resp.body()?.title ?: bookTitle }
        } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0))))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
        ) {
            // Header
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.Outlined.ArrowBack, "Back", tint = Color(0xFF0F172A))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Content Editor",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            bookTitle,
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }

            // Add Chapter Form
            item {
                Text(
                    "Insert New Segment",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = chapTitle,
                                onValueChange = { chapTitle = it },
                                label = { Text("Segment Title") },
                                modifier = Modifier.weight(3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = chapOrder,
                                onValueChange = { chapOrder = it.filter { c -> c.isDigit() } },
                                label = { Text("Order") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = chapContent,
                            onValueChange = { chapContent = it },
                            label = { Text("Text Content") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 5,
                            maxLines = 12,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (chapTitle.isBlank() || chapContent.isBlank()) {
                                    Toast.makeText(context, "Content required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val orderNum = chapOrder.toIntOrNull() ?: 1
                                isAdding = true
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.adminApi.addChapter(
                                            token, bookId,
                                            AdminChapterRequest(chapTitle.trim(), chapContent.trim(), orderNum)
                                        )
                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Segment added!", Toast.LENGTH_SHORT).show()
                                                chapTitle = ""; chapContent = ""
                                                loadChapters()
                                            } else {
                                                Toast.makeText(context, "Insert failed", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) { Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show() }
                                    } finally {
                                        withContext(Dispatchers.Main) { isAdding = false }
                                    }
                                }
                            },
                            enabled = !isAdding,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAdding) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                            } else {
                                Text("Publish Segment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Chapters List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Segment Catalog", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("${chapters.size} Items", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4F46E5))
                    }
                }
            } else if (chapters.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.ContentPasteOff, null, modifier = Modifier.size(64.dp), tint = Color(0xFFE2E8F0))
                        Text("No segments added to this book", color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(chapters.sortedBy { it.order }) { chapter ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            // Order Indicator
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${chapter.order}",
                                    color = Color(0xFF4F46E5),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    chapter.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    chapter.content.take(100) + if (chapter.content.length > 100) "..." else "",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 18.sp
                                )
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        RetrofitClient.adminApi.deleteChapter(token, chapter.id)
                                        loadChapters()
                                    } catch (_: Exception) {}
                                }
                            }) {
                                Icon(Icons.Outlined.DeleteOutline, null, tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }
            }
        }
    }
}
