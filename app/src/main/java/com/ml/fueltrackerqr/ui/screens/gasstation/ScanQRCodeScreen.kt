package com.ml.fueltrackerqr.ui.screens.gasstation

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import com.ml.fueltrackerqr.ui.icons.Filled
import com.ml.fueltrackerqr.ui.icons.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.ui.screens.admin.DetailRow
import com.ml.fueltrackerqr.viewmodel.DispensingState
import com.ml.fueltrackerqr.viewmodel.GasStationViewModel
import com.ml.fueltrackerqr.viewmodel.ScanState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for scanning QR codes and dispensing fuel
 *
 * @param onBackClick Callback when back button is clicked
 * @param onScanComplete Callback when scanning and dispensing is complete
 * @param gasStationViewModel ViewModel for gas station operations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRCodeScreen(
    onBackClick: () -> Unit,
    onScanComplete: () -> Unit,
    gasStationViewModel: GasStationViewModel
) {
    val scanState by gasStationViewModel.scanState.collectAsState()
    val dispensingState by gasStationViewModel.dispensingState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showDispenseDialog by remember { mutableStateOf(false) }
    var dispensedAmount by remember { mutableStateOf("") }

    // Initialize dispensed amount with approved amount when request is loaded
    LaunchedEffect(scanState) {
        if (scanState is ScanState.Success) {
            val request = (scanState as ScanState.Success).request
            dispensedAmount = request.requestedAmount.toString()
        }
    }

    LaunchedEffect(dispensingState) {
        when (dispensingState) {
            is DispensingState.Success -> {
                snackbarHostState.showSnackbar((dispensingState as DispensingState.Success).message)
                gasStationViewModel.clearDispensingState()
                onScanComplete()
            }
            is DispensingState.Error -> {
                snackbarHostState.showSnackbar((dispensingState as DispensingState.Error).message)
                gasStationViewModel.clearDispensingState()
            }
            else -> {}
        }
    }

    LaunchedEffect(scanState) {
        if (scanState is ScanState.Error) {
            snackbarHostState.showSnackbar((scanState as ScanState.Error).message)
            gasStationViewModel.clearScanState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
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
            when (scanState) {
                is ScanState.Initial -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan QR Code",
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Scan Fuel Request QR Code",
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Position the QR code within the scanner frame to verify the fuel request",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.fillMaxWidth(0.8f)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                // In a real app, this would launch the QR code scanner
                                // For this example, we'll simulate a successful scan
                                val sampleQRData = """
                                    {"requestId":"12345","driverId":"driver123","vehicleId":"vehicle456","approvedAmount":25.0,"approvalDate":1620000000000,"expiryDate":1627000000000,"isUsed":false,"signature":"12345:driver123:1620000000000"}
                                """.trimIndent()
                                gasStationViewModel.processQRCode(sampleQRData)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "Scan"
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Start Scanning")
                        }
                    }
                }

                is ScanState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ScanState.Success -> {
                    val request = (scanState as ScanState.Success).request

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
                                Text(
                                    text = "Valid Fuel Request",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                DetailRow(label = "Request ID", value = "#${request.id.takeLast(8)}")
                                DetailRow(label = "Driver", value = request.driverName)
                                DetailRow(label = "Vehicle ID", value = request.vehicleId)
                                DetailRow(label = "Approved Amount", value = "${request.requestedAmount} liters")
                                DetailRow(label = "Approval Date", value = formatDate(request.approvalDate))

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Trip Details",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = request.tripDetails,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showDispenseDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Dispense"
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Dispense Fuel")
                        }
                    }
                }

                is ScanState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error Scanning QR Code",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = (scanState as ScanState.Error).message,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { gasStationViewModel.clearScanState() },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(56.dp)
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }

            if (dispensingState is DispensingState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    // Dispense dialog
    if (showDispenseDialog) {
        AlertDialog(
            onDismissRequest = { showDispenseDialog = false },
            title = { Text("Dispense Fuel") },
            text = {
                Column {
                    Text("Enter the actual amount of fuel dispensed:")

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dispensedAmount,
                        onValueChange = { dispensedAmount = it },
                        label = { Text("Dispensed Amount (liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (dispensedAmount.isNotBlank() && dispensedAmount.toDoubleOrNull() != null && dispensedAmount.toDouble() > 0) {
                            val request = (scanState as? ScanState.Success)?.request
                            request?.let {
                                gasStationViewModel.markAsDispensed(
                                    requestId = it.id,
                                    dispensedAmount = dispensedAmount.toDouble()
                                )
                            }
                            showDispenseDialog = false
                        }
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDispenseDialog = false }) {
                    Text("Cancel")
                }
            }
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
