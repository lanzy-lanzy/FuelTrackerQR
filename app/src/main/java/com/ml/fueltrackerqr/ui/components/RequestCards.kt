package com.ml.fueltrackerqr.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
// import androidx.compose.material.icons.filled.LocalGasStation
// import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.ui.theme.AccentGreen
import com.ml.fueltrackerqr.ui.theme.AccentOrange
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.BackgroundLight
import com.ml.fueltrackerqr.ui.theme.BackgroundMedium
import com.ml.fueltrackerqr.ui.theme.PrimaryLight
import com.ml.fueltrackerqr.ui.theme.StatusApproved
import com.ml.fueltrackerqr.ui.theme.StatusDeclined
import com.ml.fueltrackerqr.ui.theme.StatusDispensed
import com.ml.fueltrackerqr.ui.theme.StatusPending
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A card that displays a fuel request with a gradient background based on its status
 */
@Composable
fun RequestCard(
    request: FuelRequest,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusIcon, statusText, gradientBrush) = when (request.status) {
        RequestStatus.PENDING -> Quadruple(
            StatusPending,
            Icons.Default.CheckCircle, // Using CheckCircle instead of PendingActions
            "Pending",
            GradientBrushes.pendingGradient
        )
        RequestStatus.APPROVED -> Quadruple(
            StatusApproved,
            Icons.Default.CheckCircle,
            "Approved",
            GradientBrushes.approvedGradient
        )
        RequestStatus.DECLINED -> Quadruple(
            StatusDeclined,
            Icons.Default.Close,
            "Declined",
            GradientBrushes.declinedGradient
        )
        RequestStatus.DISPENSED -> Quadruple(
            StatusDispensed,
            Icons.Default.CheckCircle, // Using CheckCircle instead of LocalGasStation
            "Dispensed",
            GradientBrushes.greenBlueGradient
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
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
            // Status indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(gradientBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusText,
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = formatDate(request.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Gradient divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(gradientBrush)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Request details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Vehicle",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = request.vehicleInfo,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${request.fuelAmount} L",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Driver",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = request.driverName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Destination",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = request.destination,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Format a timestamp into a readable date
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return format.format(date)
}

/**
 * A helper class to hold four values
 */
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
