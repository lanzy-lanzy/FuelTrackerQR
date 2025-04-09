package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * A gradient background using the teal-coral color scheme
 */
@Composable
fun TealCoralGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // Teal color palette
    val darkTeal = Color(0xFF004D40)
    val mediumTeal = Color(0xFF00897B)
    val lightTeal = Color(0xFF4DB6AC)

    // Create a vertical gradient using only teal colors
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(darkTeal, mediumTeal, mediumTeal.copy(alpha = 0.95f))
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        content = content
    )
}
