package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import com.ml.fueltrackerqr.ui.icons.QrCode
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ml.fueltrackerqr.ui.theme.backgroundGradient
import com.ml.fueltrackerqr.ui.theme.primaryGradient
import com.ml.fueltrackerqr.ui.theme.secondaryGradient
import com.ml.fueltrackerqr.ui.icons.Filled
import com.ml.fueltrackerqr.ui.icons.History
import com.ml.fueltrackerqr.ui.icons.LocalGasStation
import com.ml.fueltrackerqr.ui.icons.PendingActions
import com.ml.fueltrackerqr.ui.theme.*
import com.ml.fueltrackerqr.util.GradientBrushes

/**
 * A visual representation of the driver process flow
 */
@Composable
fun DriverProcessFlow(
    modifier: Modifier = Modifier,
    currentStep: Int = 1
) {
    val steps = listOf(
        ProcessStep(
            title = "Request Fuel",
            description = "Create a new fuel request",
            icon = Icons.Default.Add,
            color = Primary,
            brush = GradientBrushes.primaryGradient
        ),
        ProcessStep(
            title = "Pending Approval",
            description = "Wait for admin approval",
            icon = Icons.Filled.PendingActions,
            color = StatusPending,
            brush = GradientBrushes.pendingGradient
        ),
        ProcessStep(
            title = "Get QR Code",
            description = "View approved request QR",
            icon = Icons.Filled.QrCode,
            color = StatusApproved,
            brush = GradientBrushes.approvedGradient
        ),
        ProcessStep(
            title = "Fuel Dispensed",
            description = "Get fuel at gas station",
            icon = Icons.Filled.LocalGasStation,
            color = StatusDispensed,
            brush = GradientBrushes.dispensedGradient
        ),
        ProcessStep(
            title = "View History",
            description = "Track your fuel usage",
            icon = Icons.Filled.History,
            color = AccentPurple,
            brush = GradientBrushes.purpleBlueGradient
        )
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundLight.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Driver Process Flow",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            GradientDivider(
                brush = GradientBrushes.primaryGradient,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Process steps with connecting lines
            steps.forEachIndexed { index, step ->
                val isActive = index < currentStep
                val isCurrentStep = index == currentStep - 1

                ProcessStepItem(
                    step = step,
                    isActive = isActive,
                    isCurrentStep = isCurrentStep,
                    stepNumber = index + 1
                )

                // Add connecting line except for the last item
                if (index < steps.size - 1) {
                    ConnectingLine(
                        isActive = isActive,
                        brush = if (isActive) step.brush else Brush.verticalGradient(
                            listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ProcessStepItem(
    step: ProcessStep,
    isActive: Boolean,
    isCurrentStep: Boolean,
    stepNumber: Int
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentStep) 1.05f else 1f,
        animationSpec = tween(300),
        label = "scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step number circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) step.brush else Brush.radialGradient(
                        listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                    )
                )
        ) {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Step content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isActive) step.color else TextPrimary.copy(alpha = 0.7f)
            )

            Text(
                text = step.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = if (isActive) 0.9f else 0.5f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Step icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) step.brush else Brush.radialGradient(
                        listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.1f))
                    )
                )
        ) {
            Icon(
                imageVector = step.icon,
                contentDescription = step.title,
                tint = if (isActive) TextPrimary else TextPrimary.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ConnectingLine(
    isActive: Boolean,
    brush: Brush
) {
    Box(
        modifier = Modifier
            .padding(start = 18.dp) // Center with the circle
            .height(24.dp)
            .width(2.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                brush = brush,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * Data class representing a process step
 */
data class ProcessStep(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val brush: Brush
)

/**
 * A gradient divider
 */
@Composable
fun GradientDivider(
    brush: Brush,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(2.dp)
            .background(brush)
    )
}
