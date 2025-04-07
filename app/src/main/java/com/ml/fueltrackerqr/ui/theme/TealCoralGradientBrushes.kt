package com.ml.fueltrackerqr.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Predefined gradient brushes using the teal-coral color scheme
 */
object TealCoralGradientBrushes {
    // Teal-Coral color palette
    private val DarkTeal = Color(0xFF004D40)
    private val MediumTeal = Color(0xFF00897B)
    private val LightTeal = Color(0xFF4DB6AC)
    
    private val DarkCoral = Color(0xFFF57C73)
    private val MediumCoral = Color(0xFFF8A097)
    private val LightCoral = Color(0xFFFFC4BC)
    
    // Main app gradients
    val primaryGradient = Brush.linearGradient(
        colors = listOf(DarkTeal, MediumTeal)
    )
    
    val secondaryGradient = Brush.linearGradient(
        colors = listOf(DarkCoral, MediumCoral)
    )
    
    // Background gradients
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(DarkTeal, MediumTeal, LightCoral)
    )
    
    val lightBackgroundGradient = Brush.verticalGradient(
        colors = listOf(LightCoral, MediumCoral.copy(alpha = 0.7f))
    )
    
    // Status gradients
    val approvedGradient = Brush.horizontalGradient(
        colors = listOf(MediumTeal, LightTeal)
    )
    
    val pendingGradient = Brush.horizontalGradient(
        colors = listOf(MediumCoral, LightCoral)
    )
    
    val declinedGradient = Brush.horizontalGradient(
        colors = listOf(DarkCoral, MediumCoral)
    )
    
    val dispensedGradient = Brush.horizontalGradient(
        colors = listOf(LightTeal, MediumTeal)
    )
    
    // Special gradients
    val tealLightGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )
    
    val coralDarkGradient = Brush.linearGradient(
        colors = listOf(MediumCoral, DarkCoral)
    )
    
    val tealCoralGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, MediumCoral)
    )
    
    // Legacy gradients (keeping for backward compatibility)
    val purpleBlueGradient = Brush.linearGradient(
        colors = listOf(LightTeal, MediumTeal)
    )
    
    val orangeRedGradient = Brush.linearGradient(
        colors = listOf(MediumCoral, DarkCoral)
    )
    
    val greenBlueGradient = Brush.linearGradient(
        colors = listOf(MediumTeal, LightTeal)
    )
    
    // Special background gradients
    val tealToCoralGradient = Brush.verticalGradient(
        colors = listOf(DarkTeal, MediumCoral)
    )
    
    val coralToTealGradient = Brush.verticalGradient(
        colors = listOf(DarkCoral, LightTeal)
    )
}
