package com.simats.e_bookmotivation.ui.screens.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoodTrackingSection(
    moodBeforeCsv: String = "2.5,2.5,2.0,2.8,3.2,3.2", 
    moodAfterCsv: String = "3.2,3.5,2.8,4.0,4.2,3.8",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(0xFFE0F2FE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mood,
                        contentDescription = null,
                        tint = Color(0xFF0284C7),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Mood Elevation",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PremiumMoodGraph(moodBeforeCsv = moodBeforeCsv, moodAfterCsv = moodAfterCsv)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(text = day, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF0EA5E9), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Journal Mood", fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFCBD5E1), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Baseline", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun PremiumMoodGraph(moodBeforeCsv: String, moodAfterCsv: String) {
    
    val defaultBefore = listOf(2.5f, 2.5f, 2.0f, 2.8f, 3.2f, 3.2f)
    val defaultAfter = listOf(3.2f, 3.5f, 2.8f, 4.0f, 4.2f, 3.8f)

    val parsedBefore = try {
        val list = moodBeforeCsv.split(",").mapNotNull { it.trim().toFloatOrNull() }
        if (list.size >= 6) list.take(6) else (list + defaultBefore).take(6)
    } catch (e: Exception) {
        defaultBefore
    }

    val parsedAfter = try {
        val list = moodAfterCsv.split(",").mapNotNull { it.trim().toFloatOrNull() }
        if (list.size >= 6) list.take(6) else (list + defaultAfter).take(6)
    } catch (e: Exception) {
        defaultAfter
    }
    
    val beforeReading = parsedBefore
    val afterReading = parsedAfter
    
    val graphSurfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val graphBackground = MaterialTheme.colorScheme.background

    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val width = size.width
        val height = size.height
        val spacing = width / (beforeReading.size - 1)
        
        // --- Draw Before Reading Background (Gray) ---
        val beforePath = createSmoothPath(beforeReading, spacing, height)
        val fillBeforePath = Path().apply {
            addPath(beforePath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillBeforePath,
            brush = Brush.verticalGradient(
                colors = listOf(graphSurfaceVariant, graphBackground.copy(alpha = 0f)),
                startY = 0f,
                endY = height
            )
        )
        drawPath(
            path = beforePath,
            color = Color(0xFFCBD5E1),
            style = Stroke(
                width = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
            )
        )
        
        // --- Draw After Reading Background (Blue Gradient) ---
        val afterPath = createSmoothPath(afterReading, spacing, height)
        val fillAfterPath = Path().apply {
            addPath(afterPath)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = fillAfterPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0EA5E9).copy(alpha = 0.3f), Color(0xFF0EA5E9).copy(alpha = 0f)),
                startY = 0f,
                endY = height
            )
        )
        drawPath(
            path = afterPath,
            color = Color(0xFF0EA5E9),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw Points
        val afterPoints = afterReading.mapIndexed { index, value ->
            Offset(index * spacing, height - (value / 5f * height))
        }
        afterPoints.forEach { 
            drawCircle(Color.White, radius = 6.dp.toPx(), center = it)
            drawCircle(Color(0xFF0EA5E9), radius = 4.dp.toPx(), center = it)
        }
    }
}

private fun createSmoothPath(values: List<Float>, spacing: Float, height: Float): Path {
    val points = values.mapIndexed { index, value ->
        Offset(index * spacing, height - (value / 5f * height))
    }
    return Path().apply {
        moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val prev = points[i - 1]
            val curr = points[i]
            val midX = (prev.x + curr.x) / 2
            cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
        }
    }
}
