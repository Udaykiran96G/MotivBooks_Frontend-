package com.simats.e_bookmotivation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography as MaterialTypography

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),
    background = Color(0xFFF8FAFC),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    secondary = Color(0xFF64748B),
    onSecondary = Color.White,
    surfaceVariant = Color(0xFFF1F5F9), // for cards/backgrounds
    onSurfaceVariant = Color(0xFF334155),
    outline = Color(0xFFE2E8F0)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569)
)

private val SepiaColorScheme = lightColorScheme(
    primary = Color(0xFFD97706),
    background = Color(0xFFFBF0D9),
    surface = Color(0xFFF3E5AB),
    onPrimary = Color.White,
    onBackground = Color(0xFF5D4037),
    onSurface = Color(0xFF5D4037),
    secondary = Color(0xFF8D6E63),
    onSecondary = Color.White,
    surfaceVariant = Color(0xFFEFE0B9),
    onSurfaceVariant = Color(0xFF4E342E),
    outline = Color(0xFFD7CCC8)
)

@Composable
fun EBOOKMOTIVATIONTheme(
    themeFlavor: String = "Light", // "Light", "Dark", "Sepia"
    fontSize: Int = 18,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val scaleFactor = fontSize.toFloat() / 18f
    val scaledTypography = MaterialTypography(
        bodyLarge = Typography.bodyLarge.copy(
            fontSize = (Typography.bodyLarge.fontSize.value * scaleFactor).sp,
            lineHeight = (Typography.bodyLarge.lineHeight.value * scaleFactor).sp
        ),
        titleLarge = Typography.titleLarge.copy(
            fontSize = (Typography.titleLarge.fontSize.value * scaleFactor).sp,
            lineHeight = (Typography.titleLarge.lineHeight.value * scaleFactor).sp
        ),
        labelSmall = Typography.labelSmall.copy(
            fontSize = (Typography.labelSmall.fontSize.value * scaleFactor).sp,
            lineHeight = (Typography.labelSmall.lineHeight.value * scaleFactor).sp
        )
    )
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        themeFlavor == "Sepia" -> SepiaColorScheme
        themeFlavor == "Dark" -> DarkColorScheme
        themeFlavor == "Light" -> LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = scaledTypography,
        content = content
    )
}