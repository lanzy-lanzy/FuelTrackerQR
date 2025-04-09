package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

/**
 * A box with a gradient background
 */
@Composable
fun GradientBox(
    brush: Brush,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(brush = brush)
            .padding(16.dp),
        content = content
    )
}

/**
 * A divider with a gradient background
 */
@Composable
fun TealCoralGradientDivider(
    modifier: Modifier = Modifier
) {
    val brush = Brush.horizontalGradient(
        colors = listOf(
            androidx.compose.ui.graphics.Color(0xFF004D40), // Dark teal
            androidx.compose.ui.graphics.Color(0xFF00897B), // Medium teal
            androidx.compose.ui.graphics.Color(0xFF4DB6AC)  // Light teal (replaced coral)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(brush = brush)
    )
}
