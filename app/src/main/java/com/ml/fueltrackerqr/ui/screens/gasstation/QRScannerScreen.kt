package com.ml.fueltrackerqr.ui.screens.gasstation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.ApprovedButton
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.StatusApproved
import com.ml.fueltrackerqr.ui.theme.StatusDeclined
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import kotlinx.coroutines.launch

/**
 * Screen for scanning QR codes to dispense fuel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    onBackClick: () -> Unit,
    onDispenseSuccess: () -> Unit
) {
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isLoading by remember { mutableStateOf(false) }
    var showConfirmation by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    var scannedRequest by remember { mutableStateOf<FuelRequest?>(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    // Request camera permission if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Simulate scanning a QR code after a delay
    LaunchedEffect(hasCameraPermission) {
        if (hasCameraPermission) {
            // Simulate scanning delay
            kotlinx.coroutines.delay(3000)
            isLoading = true
            kotlinx.coroutines.delay(1000)
            isLoading = false

            // Create a sample request
            scannedRequest = FuelRequest(
                id = "12345",
                userId = "user1",
                driverName = "John Doe",
                vehicleInfo = "Toyota Corolla - ABC123",
                fuelAmount = 20.0,
                destination = "City Center",
                purpose = "Business meeting",
                status = RequestStatus.APPROVED,
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                approvedBy = "Admin User",
                approvedAt = System.currentTimeMillis() - 43200000, // 12 hours ago
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = null,
                dispensedBy = null
            )

            showConfirmation = true
        }
    }

    // Confirmation dialog
    if (showConfirmation && scannedRequest != null) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text("Confirm Fuel Dispensing") },
            text = {
                Column {
                    Text("Are you sure you want to dispense fuel for:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Driver: ${scannedRequest?.driverName}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vehicle: ${scannedRequest?.vehicleInfo}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Amount: ${scannedRequest?.fuelAmount} Liters",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        isLoading = true
                        // In a real app, this would call a ViewModel method to dispense fuel
                        // For now, we'll just simulate a successful dispensing
                        // Use a non-Composable scope for this button click handler
                        kotlinx.coroutines.MainScope().launch {
                            kotlinx.coroutines.delay(1500)
                            isLoading = false
                            showSuccess = true
                            kotlinx.coroutines.delay(2000)
                            onDispenseSuccess()
                        }
                    }
                ) {
                    Text("Dispense Fuel")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code", color = TextPrimary) },
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
        }
    ) { padding ->
        GradientBackground(
            brush = GradientBrushes.backgroundGradient,
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!hasCameraPermission) {
                    // Camera permission not granted
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                    ) {
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Please grant camera permission to scan QR codes",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Grant Permission")
                        }
                    }
                } else {
                    // Mock QR scanner UI
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Scan QR Code",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Position the QR code within the frame",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Mock scanner frame
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundDark),
                            contentAlignment = Alignment.Center
                        ) {
                            // Scanning animation
                            if (!showConfirmation && !showSuccess) {
                                CircularProgressIndicator(
                                    color = Primary,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }

                    // Success indicator
                    AnimatedVisibility(
                        visible = showSuccess,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BackgroundDark.copy(alpha = 0.9f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = StatusApproved,
                                    modifier = Modifier.size(64.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Fuel Dispensed Successfully!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
