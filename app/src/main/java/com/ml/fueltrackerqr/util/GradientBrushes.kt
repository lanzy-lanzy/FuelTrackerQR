package com.ml.fueltrackerqr.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Predefined gradient brushes for use throughout the app
 */
object GradientBrushes {
    // Teal-Coral color palette
    private val DarkTeal = Color(0xFF004D40)
    private val MediumTeal = Color(0xFF00897B)
    private val LightTeal = Color(0xFF4DB6AC)

    private val DarkCoral = Color(0xFFF57C73)
    private val MediumCoral = Color(0xFFF8A097)
    private val LightCoral = Color(0xFFFFC4BC)
    // Primary gradient (teal)
    val primaryGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, DarkTeal)
    )

    // Secondary gradient (teal variant)
    val secondaryGradient = Brush.linearGradient(
        colors = listOf(MediumTeal.copy(alpha = 0.8f), DarkTeal.copy(alpha = 0.9f))
    )

    // Teal horizontal gradient (renamed from teal-coral)
    val tealCoralHorizontalGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )

    // Status gradients
    val pendingGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFCD34D), Color(0xFFFFEEBB)) // Yellow gradient for pending
    )

    val approvedGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )

    val declinedGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFEF4444), Color(0xFFFCA5A5)) // Red gradient for declined
    )

    val dispensedGradient = Brush.linearGradient(
        colors = listOf(LightTeal, MediumTeal)
    )

    // Mixed color gradients
    val tealLightGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )

    val coralDarkGradient = Brush.linearGradient(
        colors = listOf(MediumTeal.copy(alpha = 0.7f), DarkTeal) // Renamed but using teal colors
    )

    val tealCoralGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal) // Renamed but using only teal colors
    )

    // Legacy gradients (keeping for backward compatibility)
    val purpleBlueGradient = Brush.linearGradient(
        colors = listOf(LightTeal, MediumTeal)
    )

    val orangeRedGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFF97316), Color(0xFFEF4444)) // Using actual orange and red
    )

    val greenBlueGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )

    // Background gradients - pure teal family with no coral/pink
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            DarkTeal,                  // Dark teal at top
            MediumTeal,                // Medium teal in middle
            MediumTeal.copy(alpha = 0.95f)  // Slightly lighter teal at bottom
        ),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY  // Ensure gradient extends fully
    )

    val lightBackgroundGradient = Brush.verticalGradient(
        colors = listOf(LightTeal, MediumTeal.copy(alpha = 0.8f))
    )

    // Special background gradients - all within teal family
    val tealToLightTealGradient = Brush.verticalGradient(
        colors = listOf(DarkTeal, LightTeal)
    )

    val lightTealToDarkTealGradient = Brush.verticalGradient(
        colors = listOf(LightTeal, DarkTeal)
    )
}
