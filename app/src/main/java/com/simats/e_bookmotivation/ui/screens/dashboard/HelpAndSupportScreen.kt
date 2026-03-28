package com.simats.e_bookmotivation.ui.screens.dashboard

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndSupportScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPasswordResetFaq: () -> Unit,
    onNavigateToOfflineReadingFaq: () -> Unit,
    onNavigateToAICoachFaq: () -> Unit,
    onNavigateToContactSupport: () -> Unit,
    onNavigateToReportBug: () -> Unit,
    onNavigateToSuggestFeature: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & Support",
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
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // FAQs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Frequently Asked Questions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    FaqItem(
                        question = "Forgot Password?",
                        onClick = onNavigateToPasswordResetFaq
                    )
                    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp)
                    
                    FaqItem(
                        question = "Can I read offline?",
                        onClick = onNavigateToOfflineReadingFaq
                    )
                    Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp)
                    
                    FaqItem(
                        question = "How does the AI Coach work?",
                        showDivider = false,
                        onClick = onNavigateToAICoachFaq
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Support Actions
            SupportActionCard(
                icon = Icons.Outlined.ChatBubbleOutline,
                iconBg = Color(0xFFEFF6FF),
                iconTint = MaterialTheme.colorScheme.primary,
                title = "Contact Support",
                subtitle = "Get help from our team",
                onClick = onNavigateToContactSupport
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SupportActionCard(
                icon = Icons.Default.BugReport,
                iconBg = Color(0xFFFEF2F2),
                iconTint = Color(0xFFEF4444),
                title = "Report a Bug",
                subtitle = "Something not working?",
                onClick = onNavigateToReportBug
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SupportActionCard(
                icon = Icons.Outlined.Lightbulb,
                iconBg = Color(0xFFFEFCE8),
                iconTint = Color(0xFFEAB308),
                title = "Suggest Feature",
                subtitle = "We love your ideas",
                onClick = onNavigateToSuggestFeature
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FaqItem(
    question: String,
    showDivider: Boolean = true,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = question,
            fontSize = 16.sp,
            color = Color(0xFF475569)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SupportActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
