package com.simats.e_bookmotivation.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simats.e_bookmotivation.network.RetrofitClient
import com.simats.e_bookmotivation.network.models.AdminBookResponse
import com.simats.e_bookmotivation.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

import androidx.compose.ui.draw.shadow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToChapters: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val token = "Bearer ${RetrofitClient.getAuthToken()}"

    // Form state
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("TRENDING") }
    var genre by remember { mutableStateOf("") }
    var coverUrl by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }

    // Book list state
    var books by remember { mutableStateOf<List<AdminBookResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Category options
    val categories = listOf("TOP" to "Top Books", "MONTH" to "Books of the Month", "TRENDING" to "Trending", "RECOMMENDED" to "AI Recommendation")
    var categoryExpanded by remember { mutableStateOf(false) }

    fun loadBooks() {
        scope.launch {
            try {
                val response = RetrofitClient.adminApi.listBooks(token)
                if (response.isSuccessful) {
                    books = response.body() ?: emptyList()
                }
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { loadBooks() }

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
            // Premium Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Admin Console",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            letterSpacing = (-1).sp
                        )
                        Text(
                            "Library Management Ecosystem",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White, CircleShape)
                            .shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.Outlined.Logout, "Logout", tint = Color(0xFFEF4444))
                    }
                }
            }

            // Stats row or informational card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF4F46E5),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.LibraryBooks, null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Total Collection", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                            Text("${books.size} Books Published", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Upload Form Section
            item {
                Text(
                    "Deploy New Content",
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
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Book Title") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Author Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Content Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = categories.find { it.first == category }?.second ?: category,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Category") },
                                    trailingIcon = { 
                                        IconButton(onClick = { categoryExpanded = true }) {
                                            Icon(Icons.Outlined.ArrowDropDown, null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth().clickable { categoryExpanded = true },
                                    shape = RoundedCornerShape(12.dp)
                                )
                                DropdownMenu(
                                    expanded = categoryExpanded,
                                    onDismissRequest = { categoryExpanded = false }
                                ) {
                                    categories.forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = { category = value; categoryExpanded = false }
                                        )
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = genre,
                                onValueChange = { genre = it },
                                label = { Text("Genre") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = coverUrl,
                            onValueChange = { coverUrl = it },
                            label = { Text("Cover URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        // PDF and Premium sections removed as requested

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (title.isBlank() || author.isBlank()) {
                                    Toast.makeText(context, "Title and Author are required", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isUploading = true
                                scope.launch {
                                    try {
                                        val titleBody = title.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val authorBody = author.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val descBody = description.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val catBody = category.toRequestBody("text/plain".toMediaTypeOrNull())
                                        val genreBody = genre.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                        val coverBody = coverUrl.trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                        // Default false for premium and null for pdf since they're removed from UI
                                        val premiumBody = "false".toRequestBody("text/plain".toMediaTypeOrNull())
                                        val pdfPart: MultipartBody.Part? = null

                                        val response = RetrofitClient.adminApi.uploadBook(
                                            token, titleBody, authorBody, descBody, catBody, genreBody, coverBody, premiumBody, pdfPart
                                        )

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                Toast.makeText(context, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                                                title = ""; author = ""; description = ""; genre = ""; coverUrl = ""
                                                loadBooks()
                                            } else {
                                                Toast.makeText(context, "Upload failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) { Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                                    } finally {
                                        withContext(Dispatchers.Main) { isUploading = false }
                                    }
                                }
                            },
                            enabled = !isUploading,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 3.dp)
                            } else {
                                Text("Publish Content", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Book List Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Library Catalog", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text("${books.size} Active", fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4F46E5))
                    }
                }
            } else if (books.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Inbox, null, modifier = Modifier.size(64.dp), tint = Color(0xFFE2E8F0))
                        Text("No books found", color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                    }
                }
            } else {
                items(books) { book ->
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
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp, 70.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC)
                            ) {
                                if (!book.coverUrl.isNullOrEmpty()) {
                                    AsyncImage(model = book.coverUrl, contentDescription = null, contentScale = ContentScale.Crop)
                                } else {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.MenuBook, null, tint = Color(0xFFE2E8F0))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(book.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A), maxLines = 1)
                                Text(book.author, fontSize = 13.sp, color = Color(0xFF64748B))
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = when(book.category) {
                                            "TOP" -> Color(0xFFFEF3C7)
                                            "TRENDING" -> Color(0xFFDCFCE7)
                                            else -> Color(0xFFF1F5F9)
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            book.category,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when(book.category) {
                                                "TOP" -> Color(0xFFB45309)
                                                "TRENDING" -> Color(0xFF15803D)
                                                else -> Color(0xFF475569)
                                            }
                                        )
                                    }
                                    if (book.isPremium) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Outlined.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                            
                            Row {
                                IconButton(onClick = { onNavigateToChapters(book.id) }) {
                                    Icon(Icons.Outlined.EditNote, null, tint = Color(0xFF4F46E5))
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            RetrofitClient.adminApi.deleteBook(token, book.id)
                                            loadBooks()
                                        } catch (_: Exception) {}
                                    }
                                }) {
                                    Icon(Icons.Outlined.Delete, null, tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
