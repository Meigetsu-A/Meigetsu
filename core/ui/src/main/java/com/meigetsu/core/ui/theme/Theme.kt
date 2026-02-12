package com.meigetsu.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MeigetsuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = Color(0xFFE50914), // Netflix Red
    cornerRadius: Int = 12,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            surface = Color(0xFF141414), // Netflix Black
            onSurface = Color.White,
            background = Color.Black,
            onBackground = Color.White,
            surfaceVariant = Color(0xFF2F2F2F)
        )
    } else {
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            surface = Color.White,
            onSurface = Color.Black,
            background = Color(0xFFF5F5F1),
            onBackground = Color.Black,
            surfaceVariant = Color(0xFFE5E5E5)
        )
    }

    val shapes = Shapes(
        small = RoundedCornerShape(cornerRadius.dp / 2),
        medium = RoundedCornerShape(cornerRadius.dp),
        large = RoundedCornerShape(cornerRadius.dp * 2)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = shapes,
        content = content
    )
}
