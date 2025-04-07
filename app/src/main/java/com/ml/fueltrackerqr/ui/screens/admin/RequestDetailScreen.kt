package com.ml.fueltrackerqr.ui.screens.admin

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import android.util.Log
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.QRCodeData
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.screens.driver.RequestStatusChip
import com.ml.fueltrackerqr.util.QRCodeUtil
import com.ml.fueltrackerqr.viewmodel.AdminActionState
import com.ml.fueltrackerqr.viewmodel.AdminViewModel
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for viewing and managing a fuel request
 *
 * @param onBackClick Callback when back button is clicked
 * @param adminViewModel ViewModel for admin operations
 * @param authViewModel ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    onBackClick: () -> Unit,
    adminViewModel: AdminViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val selectedRequest by adminViewModel.selectedRequest.collectAsState()
    val adminActionState by adminViewModel.adminActionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showApproveDialog by remember { mutableStateOf(false) }
    var showDeclineDialog by remember { mutableStateOf(false) }
    var showEditTripDialog by remember { mutableStateOf(false) }
    var approvedAmount by remember { mutableStateOf("") }
    var declineReason by remember { mutableStateOf("") }
    var editedTripDetails by remember { mutableStateOf("") }

    LaunchedEffect(adminActionState) {
        when (adminActionState) {
            is AdminActionState.Success -> {
                snackbarHostState.showSnackbar((adminActionState as AdminActionState.Success).message)
                adminViewModel.clearAdminActionState()
            }
            is AdminActionState.Error -> {
                snackbarHostState.showSnackbar((adminActionState as AdminActionState.Error).message)
                adminViewModel.clearAdminActionState()
            }
            else -> {}
        }
    }

    // Initialize approved amount and trip details when request is loaded
    LaunchedEffect(selectedRequest) {
        selectedRequest?.let {
            if (approvedAmount.isEmpty()) {
                approvedAmount = it.requestedAmount.toString()
            }
            if (editedTripDetails.isEmpty()) {
                editedTripDetails = it.tripDetails
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (selectedRequest == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val request = selectedRequest!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Request #${request.id.takeLast(8)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                RequestStatusChip(status = request.status)
                            }

                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            DetailRow(label = "Driver", value = request.driverName)
                            DetailRow(label = "Vehicle ID", value = request.vehicleId)
                            DetailRow(label = "Requested Amount", value = "${request.requestedAmount} liters")

                            if (request.status == RequestStatus.DISPENSED) {
                                DetailRow(label = "Dispensed Amount", value = "${request.dispensedAmount} liters")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trip Details",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                if (request.status == RequestStatus.PENDING) {
                                    TextButton(
                                        onClick = {
                                            editedTripDetails = request.tripDetails
                                            showEditTripDialog = true
                                        }
                                    ) {
                                        Text("Edit")
                                    }
                                }
                            }

                            Text(
                                text = request.tripDetails,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            if (request.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = request.notes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Request Date: ${formatDate(request.requestDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (request.status == RequestStatus.APPROVED ||
                                request.status == RequestStatus.DECLINED ||
                                request.status == RequestStatus.DISPENSED
                            ) {
                                Text(
                                    text = "Approval Date: ${formatDate(request.approvalDate)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = "Approved By: ${request.approvedByName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            if (request.status == RequestStatus.DISPENSED) {
                                Text(
                                    text = "Dispensed Date: ${formatDate(request.dispensedDate)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons for pending requests
                    if (request.status == RequestStatus.PENDING) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = { showApproveDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Approve"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Approve")
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = { showDeclineDialog = true },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Decline"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Decline")
                            }
                        }
                    }

                    // QR Code for approved requests
                    if (request.status == RequestStatus.APPROVED) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth()
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
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(16.dp))

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
                                    val qrContent = if (request.qrCodeData.isNotBlank()) {
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
                                    Log.d("RequestDetailScreen", "Generated QR code with content: $qrContent")

                                    Image(
                                        bitmap = qrCodeBitmap.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Scan this QR code at the gas station to dispense fuel",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            if (adminActionState is AdminActionState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Approve dialog
    if (showApproveDialog) {
        AlertDialog(
            onDismissRequest = { showApproveDialog = false },
            title = { Text("Approve Fuel Request") },
            text = {
                Column {
                    Text("Enter the amount of fuel to approve:")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = approvedAmount,
                        onValueChange = { approvedAmount = it },
                        label = { Text("Approved Amount (liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (approvedAmount.isNotBlank() && approvedAmount.toDoubleOrNull() != null && approvedAmount.toDouble() > 0) {
                            currentUser?.let { admin ->
                                selectedRequest?.let { request ->
                                    adminViewModel.approveRequest(
                                        requestId = request.id,
                                        admin = admin,
                                        approvedAmount = approvedAmount.toDouble()
                                    )
                                }
                            }
                            showApproveDialog = false
                        }
                    }
                ) {
                    Text("Approve")
                }
            },
            dismissButton = {
                TextButton(onClick = { showApproveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Decline dialog
    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline Fuel Request") },
            text = {
                Column {
                    Text("Please provide a reason for declining:")

                    Spacer(modifier = Modifier.height(8.dp))

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
                Button(
                    onClick = {
                        if (declineReason.isNotBlank()) {
                            currentUser?.let { admin ->
                                selectedRequest?.let { request ->
                                    adminViewModel.declineRequest(
                                        requestId = request.id,
                                        admin = admin,
                                        notes = declineReason
                                    )
                                }
                            }
                            showDeclineDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
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

    // Edit Trip Details dialog
    if (showEditTripDialog) {
        AlertDialog(
            onDismissRequest = { showEditTripDialog = false },
            title = { Text("Edit Trip Details") },
            text = {
                Column {
                    Text("Update the trip details for this request:")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editedTripDetails,
                        onValueChange = { editedTripDetails = it },
                        label = { Text("Trip Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editedTripDetails.isNotBlank()) {
                            currentUser?.let { admin ->
                                selectedRequest?.let { request ->
                                    // Update the request with new trip details
                                    adminViewModel.updateTripDetails(
                                        requestId = request.id,
                                        tripDetails = editedTripDetails
                                    )
                                }
                            }
                            showEditTripDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTripDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Row displaying a label and value
 *
 * @param label Label text
 * @param value Value text
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Format a timestamp as a date string
 *
 * @param timestamp Timestamp to format
 * @return Formatted date string
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
