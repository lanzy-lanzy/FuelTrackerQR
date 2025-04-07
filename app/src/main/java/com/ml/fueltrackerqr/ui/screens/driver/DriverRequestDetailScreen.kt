package com.ml.fueltrackerqr.ui.screens.driver

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.components.StylizedQRCode
import com.ml.fueltrackerqr.util.QRCodeUtil
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.RequestState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for displaying detailed information about a fuel request
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRequestDetailScreen(
    requestId: String,
    onBackClick: () -> Unit,
    onCancelRequest: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val driverRequests by driverViewModel.driverRequests.collectAsState()
    val requestState by driverViewModel.requestState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showCancelDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Find the request with the given ID
    val request = remember(driverRequests, requestId) {
        driverRequests.find { it.id == requestId }
    }

    // Format dates
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val requestDateFormatted = remember(request) {
        request?.requestDate?.let { dateFormat.format(Date(it)) } ?: ""
    }
    val approvalDateFormatted = remember(request) {
        request?.approvalDate?.takeIf { it > 0 }?.let { dateFormat.format(Date(it)) } ?: ""
    }

    // Status color
    val statusColor = remember(request) {
        when (request?.status) {
            RequestStatus.PENDING -> Color(0xFFFFA000) // Amber
            RequestStatus.APPROVED -> Color(0xFF4CAF50) // Green
            RequestStatus.DECLINED -> Color(0xFFF44336) // Red
            RequestStatus.DISPENSED -> Color(0xFF2196F3) // Blue
            else -> Color.Gray
        }
    }

    // Load data
    LaunchedEffect(requestId) {
        isLoading = false
    }

    // Handle operation state
    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestState.Success -> {
                snackbarHostState.showSnackbar((requestState as RequestState.Success).message)
                driverViewModel.clearRequestState()
                if ((requestState as RequestState.Success).message.contains("cancelled", ignoreCase = true)) {
                    onCancelRequest()
                }
            }
            is RequestState.Error -> {
                snackbarHostState.showSnackbar((requestState as RequestState.Error).message)
                driverViewModel.clearRequestState()
            }
            else -> {}
        }
    }

    // Cancel confirmation dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Request") },
            text = { Text("Are you sure you want to cancel this fuel request? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        request?.let { driverViewModel.cancelFuelRequest(it.id) }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF44336) // Red
                    )
                ) {
                    Text("Yes, Cancel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No, Keep It")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Request Details",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40), // Dark teal
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            if (isLoading) {
                // Show loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00897B) // Medium teal
                    )
                }
            } else if (request == null) {
                // Show error if request not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Request Not Found",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "The requested fuel request could not be found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Show request details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Status card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = statusColor.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Status icon
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(statusColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (request.status) {
                                        RequestStatus.PENDING -> Icons.Default.Info
                                        RequestStatus.APPROVED -> Icons.Default.CheckCircle
                                        RequestStatus.DECLINED -> Icons.Default.Info
                                        RequestStatus.DISPENSED -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = "Status: ${request.status.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )

                                Text(
                                    text = when (request.status) {
                                        RequestStatus.PENDING -> "Your request is waiting for approval"
                                        RequestStatus.APPROVED -> "Your request has been approved"
                                        RequestStatus.DECLINED -> "Your request has been declined"
                                        RequestStatus.DISPENSED -> "Fuel has been dispensed"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = statusColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    // Request details card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Header
                            Text(
                                text = "Request Information",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            HorizontalDivider(
                                color = Color(0xFFE0F2F1),
                                thickness = 1.dp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Request ID
                            DetailRow(
                                label = "Request ID",
                                value = request.id,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Request Date
                            DetailRow(
                                label = "Requested On",
                                value = requestDateFormatted,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Fuel Amount
                            DetailRow(
                                label = "Fuel Amount",
                                value = "${request.requestedAmount} liters",
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Vehicle
                            DetailRow(
                                label = "Vehicle ID",
                                value = request.vehicleId,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            if (request.status == RequestStatus.APPROVED || request.status == RequestStatus.DISPENSED) {
                                // Approval Date
                                DetailRow(
                                    label = "Approved On",
                                    value = approvalDateFormatted,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Approved By
                                DetailRow(
                                    label = "Approved By",
                                    value = request.approvedByName,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            if (request.status == RequestStatus.DISPENSED) {
                                // Dispensed Amount
                                DetailRow(
                                    label = "Dispensed Amount",
                                    value = "${request.dispensedAmount} liters",
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Dispensed Date
                                DetailRow(
                                    label = "Dispensed On",
                                    value = request.dispensedDate.takeIf { it > 0 }?.let {
                                        dateFormat.format(Date(it))
                                    } ?: "",
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }
                        }
                    }

                    // Trip details card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // Header
                            Text(
                                text = "Trip Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            HorizontalDivider(
                                color = Color(0xFFE0F2F1),
                                thickness = 1.dp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Trip details
                            Text(
                                text = request.tripDetails,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF004D40)
                            )

                            if (request.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Additional Notes",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF004D40),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Text(
                                    text = request.notes,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF004D40)
                                )
                            }
                        }
                    }

                    // QR Code section for approved requests
                    if (request.status == RequestStatus.APPROVED && request.qrCodeData?.isNotBlank() == true) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "QR Code for Fuel Dispensing",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF004D40),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                HorizontalDivider(
                                    color = Color(0xFFE0F2F1),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // QR Code
                                Box(
                                    modifier = Modifier
                                        .size(250.dp)
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Use the QR code data from the request if available, otherwise use a simple format
                                    val qrContent = if (!request.qrCodeData.isNullOrBlank()) {
                                        request.qrCodeData
                                    } else {
                                        "fuel_request:${request.id}"
                                    }

                                    val qrCodeBitmap = QRCodeUtil.generateQRCode(
                                        content = qrContent,
                                        width = 500,
                                        height = 500
                                    )

                                    // Log the QR code content for debugging
                                    android.util.Log.d("DriverRequestDetailScreen", "Generated QR code with content: $qrContent")

                                    Image(
                                        bitmap = qrCodeBitmap.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Scan this QR code at the gas station to dispense fuel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF004D40),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "This QR code is valid for 7 days from approval",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF00796B),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Cancel button for pending requests
                    if (request.status == RequestStatus.PENDING) {
                        Button(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF44336) // Red
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Cancel Request")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Row component for displaying a detail with label and value
 */
@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF004D40).copy(alpha = 0.7f),
            modifier = Modifier.weight(0.4f)
        )

        // Value with bold text
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004D40),
            modifier = Modifier.weight(0.6f)
        )
    }
}
