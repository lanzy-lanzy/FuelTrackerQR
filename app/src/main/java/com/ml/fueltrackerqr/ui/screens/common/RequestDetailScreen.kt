package com.ml.fueltrackerqr.ui.screens.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
// import androidx.compose.material.icons.filled.DirectionsCar
// import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
// import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.data.model.UserRole
import com.ml.fueltrackerqr.ui.components.ApprovedButton
import com.ml.fueltrackerqr.ui.components.DeclinedButton
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBox
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.GradientDivider
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.components.StylizedQRCode
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for displaying the details of a fuel request
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    request: FuelRequest,
    userRole: UserRole,
    onBackClick: () -> Unit,
    onApproveClick: (FuelRequest) -> Unit,
    onDeclineClick: (FuelRequest, String) -> Unit,
    onGenerateQRClick: (FuelRequest) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var showDeclineDialog by remember { mutableStateOf(false) }
    var declineReason by remember { mutableStateOf("") }
    var showQRCode by remember { mutableStateOf(false) }

    // Determine the status color and gradient
    val (statusColor, statusGradient) = when (request.status) {
        RequestStatus.PENDING -> Pair(
            com.ml.fueltrackerqr.ui.theme.StatusPending,
            GradientBrushes.pendingGradient
        )
        RequestStatus.APPROVED -> Pair(
            com.ml.fueltrackerqr.ui.theme.StatusApproved,
            GradientBrushes.approvedGradient
        )
        RequestStatus.DECLINED -> Pair(
            com.ml.fueltrackerqr.ui.theme.StatusDeclined,
            GradientBrushes.declinedGradient
        )
        RequestStatus.DISPENSED -> Pair(
            com.ml.fueltrackerqr.ui.theme.StatusDispensed,
            GradientBrushes.greenBlueGradient
        )
    }

    // Decline dialog
    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline Request") },
            text = {
                Column {
                    Text("Please provide a reason for declining this request.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = declineReason,
                        onValueChange = { declineReason = it },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (declineReason.isNotBlank()) {
                            onDeclineClick(request, declineReason)
                            showDeclineDialog = false
                        }
                    }
                ) {
                    Text("Decline")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        GradientBackground(
            brush = GradientBrushes.backgroundGradient,
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Status header
                    GradientBox(
                        brush = statusGradient,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = request.status.name,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Request #${request.id}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = formatDate(request.timestamp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Request details card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundDark.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Request Details",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Driver info
                            DetailRow(
                                icon = Icons.Default.Person,
                                label = "Driver",
                                value = request.driverName
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Vehicle info
                            DetailRow(
                                icon = Icons.Default.CheckCircle, // Using CheckCircle instead of DirectionsCar
                                label = "Vehicle",
                                value = request.vehicleInfo
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Fuel amount
                            DetailRow(
                                icon = Icons.Default.CheckCircle, // Using CheckCircle instead of LocalGasStation
                                label = "Fuel Amount",
                                value = "${request.fuelAmount} Liters"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Destination
                            DetailRow(
                                icon = Icons.Default.LocationOn,
                                label = "Destination",
                                value = request.destination
                            )

                            if (request.purpose.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Purpose",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = request.purpose,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Status details
                    if (request.status != RequestStatus.PENDING) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = BackgroundDark.copy(alpha = 0.7f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Status Details",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                when (request.status) {
                                    RequestStatus.APPROVED -> {
                                        DetailRow(
                                            icon = Icons.Default.CheckCircle,
                                            label = "Approved By",
                                            value = request.approvedBy ?: "Unknown"
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        DetailRow(
                                            icon = null,
                                            label = "Approved At",
                                            value = request.approvedAt?.let { formatDate(it) } ?: "Unknown"
                                        )
                                    }
                                    RequestStatus.DECLINED -> {
                                        DetailRow(
                                            icon = Icons.Default.Close,
                                            label = "Declined By",
                                            value = request.declinedBy ?: "Unknown"
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        DetailRow(
                                            icon = null,
                                            label = "Declined At",
                                            value = request.declinedAt?.let { formatDate(it) } ?: "Unknown"
                                        )

                                        if (!request.declineReason.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(16.dp))

                                            Text(
                                                text = "Reason",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextPrimary
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = request.declineReason ?: "",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = TextPrimary.copy(alpha = 0.8f)
                                            )
                                        }
                                    }
                                    RequestStatus.DISPENSED -> {
                                        DetailRow(
                                            icon = Icons.Default.CheckCircle, // Using CheckCircle instead of LocalGasStation
                                            label = "Dispensed By",
                                            value = request.dispensedBy ?: "Unknown"
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        DetailRow(
                                            icon = null,
                                            label = "Dispensed At",
                                            value = request.dispensedAt?.let { formatDate(it) } ?: "Unknown"
                                        )
                                    }
                                    else -> {}
                                }
                            }
                        }
                    }

                    // Action buttons for admins
                    if (userRole == UserRole.ADMIN && request.status == RequestStatus.PENDING) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DeclinedButton(
                                text = "Decline",
                                onClick = { showDeclineDialog = true },
                                modifier = Modifier.weight(1f)
                            )

                            ApprovedButton(
                                text = "Approve",
                                onClick = { onApproveClick(request) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Generate QR code button for admins
                    if (userRole == UserRole.ADMIN && request.status == RequestStatus.APPROVED) {
                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Generate QR Code",
                            onClick = { showQRCode = true },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // QR code display
                    AnimatedVisibility(
                        visible = showQRCode,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            StylizedQRCode(
                                content = "fuel_request:${request.id}",
                                title = "Fuel Request QR Code",
                                subtitle = "Scan this at the gas station",
                                backgroundBrush = GradientBrushes.primaryGradient
                            )
                        }
                    }
                }

                // Loading indicator
                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

/**
 * A row displaying a detail with an icon, label, and value
 */
@Composable
private fun DetailRow(
    icon: ImageVector?,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))
        } else if (icon == null) {
            Spacer(modifier = Modifier.size(36.dp))
        }

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )
        }
    }
}

/**
 * Format a timestamp into a readable date
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
