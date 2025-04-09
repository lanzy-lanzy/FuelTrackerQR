package com.ml.fueltrackerqr.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = TextPrimary,
    secondary = Secondary,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentOrange,
    onTertiary = TextPrimary,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = BackgroundMedium,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    error = StatusDeclined,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = BackgroundDark,
    secondary = Secondary,
    onSecondary = TextPrimary,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = AccentOrange,
    onTertiary = TextPrimary,
    background = TextPrimary,
    onBackground = BackgroundDark,
    surface = TextSecondary,
    onSurface = BackgroundDark,
    surfaceVariant = TextPrimary,
    onSurfaceVariant = BackgroundMedium,
    error = StatusDeclined,
    onError = TextPrimary
)

@Composable
fun FuelTrackerQRTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use our custom colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Update the status bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = (if (darkTheme) BackgroundDark else Primary).toArgb()
            // Set navigation bar color to match the app theme and remove divider
            window.navigationBarColor = (if (darkTheme) BackgroundDark else Primary).toArgb()
            // Make sure system bars use our colors
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
            // Make app edge-to-edge
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}