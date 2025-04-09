package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.ui.theme.AccentGreen
import com.ml.fueltrackerqr.ui.theme.AccentOrange
import com.ml.fueltrackerqr.ui.theme.AccentPurple
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.BackgroundLight
import com.ml.fueltrackerqr.ui.theme.BackgroundMedium
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.PrimaryDark
import com.ml.fueltrackerqr.ui.theme.PrimaryLight
import com.ml.fueltrackerqr.ui.theme.Secondary
import com.ml.fueltrackerqr.ui.theme.SecondaryDark
import com.ml.fueltrackerqr.ui.theme.StatusApproved
import com.ml.fueltrackerqr.ui.theme.StatusDeclined
import com.ml.fueltrackerqr.ui.theme.StatusPending
import com.ml.fueltrackerqr.ui.theme.DarkTeal
import com.ml.fueltrackerqr.ui.theme.MediumTeal
import com.ml.fueltrackerqr.ui.theme.LightCoral

/**
 * Predefined gradient brushes for use throughout the app
 */
object GradientBrushes {
    // Main app gradients
    val primaryGradient = Brush.verticalGradient(
        colors = listOf(PrimaryDark, Primary, PrimaryLight)
    )

    val secondaryGradient = Brush.verticalGradient(
        colors = listOf(SecondaryDark, Secondary)
    )

    // Background gradients
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(BackgroundDark, BackgroundMedium, BackgroundLight)
    )

    val lightBackgroundGradient = Brush.verticalGradient(
        colors = listOf(Color.White, Color(0xFFF3F4F6))
    )

    // Splash screen gradient (Teal-only theme)
    val splashGradient = Brush.verticalGradient(
        colors = listOf(DarkTeal, MediumTeal, MediumTeal.copy(alpha = 0.95f))
    )

    // Diagonal version of splash gradient
    val splashDiagonalGradient = Brush.linearGradient(
        colors = listOf(DarkTeal, MediumTeal, MediumTeal.copy(alpha = 0.95f)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
    )

    // Status gradients
    val approvedGradient = Brush.horizontalGradient(
        colors = listOf(StatusApproved, AccentGreen)
    )

    val pendingGradient = Brush.horizontalGradient(
        colors = listOf(StatusPending, AccentOrange)
    )

    val declinedGradient = Brush.horizontalGradient(
        colors = listOf(StatusDeclined, Color(0xFFB91C1C))
    )

    // Special gradients
    val purpleBlueGradient = Brush.horizontalGradient(
        colors = listOf(AccentPurple, Primary)
    )

    val orangeRedGradient = Brush.horizontalGradient(
        colors = listOf(AccentOrange, StatusDeclined)
    )

    val greenBlueGradient = Brush.horizontalGradient(
        colors = listOf(AccentGreen, PrimaryLight)
    )

    // Teal gradients (renamed from Teal-Coral)
    val tealCoralGradient = Brush.horizontalGradient(
        colors = listOf(DarkTeal, MediumTeal)
    )
}

/**
 * A box with a gradient background
 */
@Composable
fun GradientBox(
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.backgroundGradient,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.background(brush),
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * A full screen box with a gradient background
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.backgroundGradient,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    GradientBox(
        modifier = modifier.fillMaxSize(),
        brush = brush,
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * A full screen box with the splash screen gradient background
 */
@Composable
fun SplashGradientBackground(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    GradientBox(
        modifier = modifier.fillMaxSize(),
        brush = GradientBrushes.splashGradient,
        contentAlignment = contentAlignment,
        content = content
    )
}

/**
 * A card with a gradient background
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.primaryGradient,
    contentAlignment: Alignment = Alignment.Center,
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        GradientBox(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            brush = brush,
            contentAlignment = contentAlignment,
            content = content
        )
    }
}

/**
 * A gradient divider
 */
@Composable
fun GradientDivider(
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.primaryGradient
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .clip(RoundedCornerShape(1.dp))
            .background(brush)
    )
}
