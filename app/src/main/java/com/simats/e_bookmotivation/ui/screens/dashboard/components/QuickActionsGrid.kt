package com.simats.e_bookmotivation.ui.screens.dashboard.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickActionsGrid(
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedCorner(16.dp)
        ) {
            QuickActionCard(
                title = "Daily Boost",
                icon = Icons.Outlined.Bolt,
                iconColor = MaterialTheme.colorScheme.primary,
                backgroundColor = Color(0xFFEFF6FF),
                onClick = { onActionClick("DailyBoost") },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Set Goal",
                icon = Icons.Outlined.TrackChanges,
                iconColor = Color(0xFF8B5CF6),
                backgroundColor = Color(0xFFF5F3FF),
                onClick = { onActionClick("Goal") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedCorner(16.dp)
        ) {
            QuickActionCard(
                title = "Badges",
                icon = Icons.Outlined.Badge,
                iconColor = Color(0xFF10B981),
                backgroundColor = Color(0xFFECFDF5),
                onClick = { onActionClick("Badges") },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                title = "Journal",
                icon = Icons.Outlined.EditNote,
                iconColor = Color(0xFFF59E0B),
                backgroundColor = Color(0xFFFFFBEB),
                onClick = { onActionClick("Journal") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun Arrangement.spacedCorner(space: Dp) = Arrangement.spacedBy(space)

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 1.05f else 1f, label = "scale")

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = iconColor.copy(alpha = 0.9f)
        )
    }
}
