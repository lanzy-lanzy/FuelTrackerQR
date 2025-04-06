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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
// We'll use these icons when they're available
// import androidx.compose.material.icons.filled.DirectionsCar
// import androidx.compose.material.icons.filled.History
// import androidx.compose.material.icons.filled.LocalGasStation
// import androidx.compose.material.icons.filled.PendingActions
// import androidx.compose.material.icons.filled.QrCode
// import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.ml.fueltrackerqr.ui.theme.AccentGreen
import com.ml.fueltrackerqr.ui.theme.AccentOrange
import com.ml.fueltrackerqr.ui.theme.AccentPurple
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.BackgroundMedium
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.PrimaryLight
import com.ml.fueltrackerqr.ui.theme.Secondary
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * A dashboard card with gradient background
 */
@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.primaryGradient
) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(brush),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = TextPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * Dashboard cards for the driver role
 */
@Composable
fun DriverDashboardCards(
    onNewRequestClick: () -> Unit,
    onViewRequestsClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DashboardCard(
            title = "New Fuel Request",
            subtitle = "Create a new fuel request",
            icon = Icons.Default.Add,
            onClick = onNewRequestClick,
            brush = GradientBrushes.primaryGradient
        )

        DashboardCard(
            title = "My Requests",
            subtitle = "View your pending requests",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of PendingActions
            onClick = onViewRequestsClick,
            brush = GradientBrushes.pendingGradient
        )

        DashboardCard(
            title = "Request History",
            subtitle = "View your past requests",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of History
            onClick = onHistoryClick,
            brush = GradientBrushes.purpleBlueGradient
        )
    }
}

/**
 * Dashboard cards for the admin role
 */
@Composable
fun AdminDashboardCards(
    onPendingRequestsClick: () -> Unit,
    onApprovedRequestsClick: () -> Unit,
    onGenerateQRClick: () -> Unit,
    onHistoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DashboardCard(
            title = "Pending Requests",
            subtitle = "Review and approve requests",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of PendingActions
            onClick = onPendingRequestsClick,
            brush = GradientBrushes.pendingGradient
        )

        DashboardCard(
            title = "Approved Requests",
            subtitle = "View approved requests",
            icon = Icons.Default.CheckCircle,
            onClick = onApprovedRequestsClick,
            brush = GradientBrushes.approvedGradient
        )

        DashboardCard(
            title = "Generate QR Codes",
            subtitle = "Create QR codes for approved requests",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of QrCode
            onClick = onGenerateQRClick,
            brush = GradientBrushes.primaryGradient
        )

        DashboardCard(
            title = "Request History",
            subtitle = "View all past requests",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of History
            onClick = onHistoryClick,
            brush = GradientBrushes.purpleBlueGradient
        )
    }
}

/**
 * Dashboard cards for the gas station role
 */
@Composable
fun GasStationDashboardCards(
    onScanQRClick: () -> Unit,
    onDispensedFuelClick: () -> Unit,
    onVehiclesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        DashboardCard(
            title = "Scan QR Code",
            subtitle = "Scan to dispense fuel",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of QrCodeScanner
            onClick = onScanQRClick,
            brush = GradientBrushes.primaryGradient
        )

        DashboardCard(
            title = "Dispensed Fuel",
            subtitle = "View dispensed fuel records",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of LocalGasStation
            onClick = onDispensedFuelClick,
            brush = GradientBrushes.greenBlueGradient
        )

        DashboardCard(
            title = "Vehicles",
            subtitle = "View vehicle fuel history",
            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of DirectionsCar
            onClick = onVehiclesClick,
            brush = GradientBrushes.purpleBlueGradient
        )
    }
}

/**
 * A section header for dashboard
 */
@Composable
fun DashboardSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        GradientDivider(
            brush = GradientBrushes.primaryGradient,
            modifier = Modifier.fillMaxWidth(0.3f)
        )
    }
}

/**
 * A dashboard stats card
 */
@Composable
fun StatsCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    brush: Brush = GradientBrushes.primaryGradient
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * A row of stats cards
 */
@Composable
fun StatsRow(
    pendingCount: Int,
    approvedCount: Int,
    dispensedCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatsCard(
            title = "Pending",
            value = pendingCount.toString(),
            modifier = Modifier.weight(1f),
            brush = GradientBrushes.pendingGradient
        )

        StatsCard(
            title = "Approved",
            value = approvedCount.toString(),
            modifier = Modifier.weight(1f),
            brush = GradientBrushes.approvedGradient
        )

        StatsCard(
            title = "Dispensed",
            value = dispensedCount.toString(),
            modifier = Modifier.weight(1f),
            brush = GradientBrushes.greenBlueGradient
        )
    }
}
