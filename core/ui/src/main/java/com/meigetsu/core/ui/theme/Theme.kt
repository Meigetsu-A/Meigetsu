package com.meigetsu.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MeigetsuTheme(
    themeMode: String = "SYSTEM",
    primaryColor: Color = Color(0xFF00BFFF), // Vibrant Deep Sky Blue
    cornerRadius: Int = 16,
    content: @Composable () -> Unit
) {
    // Force Dark Mode for the premium "AniLab" feel
    val darkTheme = true

    val colorScheme = darkColorScheme(
        primary = primaryColor,
        onPrimary = Color.Black,
        surface = Color(0xFF0A0A0A),
        onSurface = Color(0xFFE1E1E1),
        background = Color(0xFF000000),
        onBackground = Color.White,
        surfaceVariant = Color(0xFF1E1E1E),
        onSurfaceVariant = Color(0xFFB0B0B0),
        secondary = Color(0xFF1DB954), // Subtle green secondary
        tertiary = Color(0xFFFFD700) // Gold for ratings
    )

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(cornerRadius.dp),
        large = RoundedCornerShape(24.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
