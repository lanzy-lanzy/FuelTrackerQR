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

// Teal-Coral color palette
private val DarkTeal = Color(0xFF004D40)
private val MediumTeal = Color(0xFF00897B)
private val LightTeal = Color(0xFF4DB6AC)

private val DarkCoral = Color(0xFFF57C73)
private val MediumCoral = Color(0xFFF8A097)
private val LightCoral = Color(0xFFFFC4BC)

private val White = Color(0xFFF9FAFB)
private val LightGray = Color(0xFFE0E0E0)

private val TealCoralDarkColorScheme = darkColorScheme(
    primary = MediumTeal,
    onPrimary = White,
    primaryContainer = DarkTeal,
    onPrimaryContainer = White,
    secondary = MediumCoral,
    onSecondary = DarkTeal,
    secondaryContainer = DarkCoral,
    onSecondaryContainer = White,
    tertiary = LightTeal,
    onTertiary = DarkTeal,
    background = DarkTeal,
    onBackground = White,
    surface = MediumTeal,
    onSurface = White,
    surfaceVariant = LightTeal,
    onSurfaceVariant = DarkTeal,
    error = DarkCoral,
    onError = White
)

private val TealCoralLightColorScheme = lightColorScheme(
    primary = MediumTeal,
    onPrimary = White,
    primaryContainer = LightTeal,
    onPrimaryContainer = DarkTeal,
    secondary = MediumCoral,
    onSecondary = DarkTeal,
    secondaryContainer = LightCoral,
    onSecondaryContainer = DarkTeal,
    tertiary = LightTeal,
    onTertiary = DarkTeal,
    background = LightCoral,
    onBackground = DarkTeal,
    surface = White,
    onSurface = DarkTeal,
    surfaceVariant = LightTeal,
    onSurfaceVariant = DarkTeal,
    error = DarkCoral,
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
            window.statusBarColor = (if (darkTheme) DarkTeal else MediumTeal).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
