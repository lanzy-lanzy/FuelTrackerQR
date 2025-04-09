package com.ml.fueltrackerqr.ui.theme

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

// Teal-Coral color palette - using theme-specific names to avoid conflicts
private val ThemeDarkTeal = Color(0xFF004D40)
private val ThemeMediumTeal = Color(0xFF00897B)
private val ThemeLightTeal = Color(0xFF4DB6AC)

private val ThemeDarkCoral = Color(0xFFF57C73)
private val ThemeMediumCoral = Color(0xFFF8A097)
private val ThemeLightCoral = Color(0xFFFFC4BC)

private val White = Color(0xFFF9FAFB)
private val LightGray = Color(0xFFE0E0E0)

private val TealCoralDarkColorScheme = darkColorScheme(
    primary = ThemeMediumTeal,
    onPrimary = White,
    primaryContainer = ThemeDarkTeal,
    onPrimaryContainer = White,
    secondary = ThemeMediumTeal.copy(alpha = 0.8f), // Changed from coral to teal
    onSecondary = White,
    secondaryContainer = ThemeDarkTeal.copy(alpha = 0.8f), // Changed from coral to teal
    onSecondaryContainer = White,
    tertiary = ThemeLightTeal,
    onTertiary = ThemeDarkTeal,
    background = ThemeDarkTeal,
    onBackground = White,
    surface = ThemeMediumTeal,
    onSurface = White,
    surfaceVariant = ThemeLightTeal,
    onSurfaceVariant = ThemeDarkTeal,
    error = Color(0xFFCF6679), // Standard Material Design dark theme error color
    onError = White
)

private val TealCoralLightColorScheme = lightColorScheme(
    primary = ThemeMediumTeal,
    onPrimary = White,
    primaryContainer = ThemeLightTeal,
    onPrimaryContainer = ThemeDarkTeal,
    secondary = ThemeMediumTeal.copy(alpha = 0.8f), // Changed from coral to teal
    onSecondary = ThemeDarkTeal,
    secondaryContainer = ThemeLightTeal.copy(alpha = 0.9f), // Changed from coral to teal
    onSecondaryContainer = ThemeDarkTeal,
    tertiary = ThemeLightTeal,
    onTertiary = ThemeDarkTeal,
    background = ThemeLightTeal, // Changed from coral to teal
    onBackground = ThemeDarkTeal,
    surface = White,
    onSurface = ThemeDarkTeal,
    surfaceVariant = ThemeLightTeal,
    onSurfaceVariant = ThemeDarkTeal,
    error = Color(0xFFB00020), // Standard Material Design error color
    onError = White
)

@Composable
fun TealCoralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        TealCoralDarkColorScheme
    } else {
        TealCoralLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (darkTheme) ThemeDarkTeal else ThemeMediumTeal).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
