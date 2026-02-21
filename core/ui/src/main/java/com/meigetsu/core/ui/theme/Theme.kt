package com.meigetsu.core.ui.theme
import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryText,
    secondary = SecondaryText,
    tertiary = MutedText,
    background = Background,
    surface = Background,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = Background,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
)
@Composable
fun MeigetsuTheme(
    primaryColor: Color = PrimaryText,
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) {
        DarkColorScheme.copy(primary = primaryColor)
    } else {
        DarkColorScheme.copy(primary = primaryColor)
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
