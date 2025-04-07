package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * A button with a gradient background
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.primaryGradient,
    enabled: Boolean = true,
    contentColor: Color = TextPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (enabled) brush else Brush.verticalGradient(
                    listOf(Color.Gray.copy(alpha = 0.5f), Color.Gray.copy(alpha = 0.3f))
                )),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

/**
 * A primary gradient button
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    GradientButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        brush = GradientBrushes.tealCoralGradient,
        enabled = enabled
    )
}

/**
 * A secondary gradient button
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    GradientButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        brush = GradientBrushes.secondaryGradient,
        enabled = enabled
    )
}

/**
 * An approved status button
 */
@Composable
fun ApprovedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    GradientButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        brush = GradientBrushes.approvedGradient,
        enabled = enabled
    )
}

/**
 * A declined status button
 */
@Composable
fun DeclinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    GradientButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        brush = GradientBrushes.declinedGradient,
        enabled = enabled
    )
}
