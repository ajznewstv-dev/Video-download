package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = TubeMatePrimary,
    onPrimary = Color.White,
    secondary = TubeMateAccent,
    onSecondary = Color.White,
    tertiary = TubeMateCyan,
    background = TubeMateDarkBg,
    surface = Color(0xFF263238),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TubeMatePrimary,
    onPrimary = Color.White,
    secondary = TubeMateAccent,
    onSecondary = Color.White,
    tertiary = TubeMateTeal,
    background = Color(0xFFF0F2F5),
    surface = Color.White,
    onSurface = TubeMateTextPrimary,
    surfaceVariant = TubeMateSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = TubeMatePrimaryDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
