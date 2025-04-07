package com.ml.fueltrackerqr.ui.screens.gasstation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import com.ml.fueltrackerqr.ui.icons.Filled
import com.ml.fueltrackerqr.ui.icons.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.zxing.BinaryBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.ui.components.StylizedQRScanner
import com.ml.fueltrackerqr.ui.screens.admin.DetailRow
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.viewmodel.DispensingState
import com.ml.fueltrackerqr.viewmodel.GasStationViewModel
import com.ml.fueltrackerqr.viewmodel.ScanState
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

/**
 * Class to analyze camera images for QR codes
 */
class QRCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader()
    private val TAG = "QRCodeAnalyzer"

    init {
        // Configure the reader with hints for better QR code detection
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE),
            DecodeHintType.TRY_HARDER to true
        )
        reader.setHints(hints)
        Log.d(TAG, "QRCodeAnalyzer initialized with hints")
    }

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        val width = image.width
        val height = image.height

        Log.v(TAG, "Analyzing image: ${width}x${height}")

        val source = PlanarYUVLuminanceSource(
            data,
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )

        val bitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decode(bitmap)
            val text = result.text
            Log.d(TAG, "Successfully decoded QR code: $text")
            if (text.isNotBlank()) {
                onQRCodeScanned(text)
            } else {
                Log.w(TAG, "Decoded QR code text is empty")
            }
        } catch (e: Exception) {
            // QR code not found in this frame
            Log.v(TAG, "No QR code found in this frame: ${e.message}")
        } finally {
            image.close()
        }
    }
}

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
    val TAG = "ScanQRCodeScreen"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val scanState by gasStationViewModel.scanState.collectAsState()
    val dispensingState by gasStationViewModel.dispensingState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var isScanning by remember { mutableStateOf(false) }
    var showDispenseDialog by remember { mutableStateOf(false) }
    var dispensedAmount by remember { mutableStateOf("") }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) {
                isScanning = true
            }
        }
    )

    // Request camera permission if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Initialize dispensed amount with approved amount when request is loaded
    LaunchedEffect(scanState) {
        if (scanState is ScanState.Success) {
            val request = (scanState as ScanState.Success).request
            dispensedAmount = request.requestedAmount.toString()
            isScanning = false
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
            isScanning = false
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
                    if (hasCameraPermission && isScanning) {
                        // Camera preview with QR scanner
                        StylizedQRScanner(
                            title = "Scan Fuel Request QR Code",
                            subtitle = "Position the QR code within the frame",
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Camera preview
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx).apply {
                                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                    }

                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    cameraProviderFuture.addListener({
                                        val cameraProvider = cameraProviderFuture.get()

                                        val preview = Preview.Builder().build().also {
                                            it.setSurfaceProvider(previewView.surfaceProvider)
                                        }

                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                            .build()
                                            .also {
                                                it.setAnalyzer(
                                                    Executors.newSingleThreadExecutor(),
                                                    QRCodeAnalyzer { qrCode ->
                                                        Log.d(TAG, "QR Code scanned: $qrCode")
                                                        coroutineScope.launch {
                                                            if (isScanning) {
                                                                isScanning = false
                                                                // Ensure the QR code is not empty
                                                                if (qrCode.isNotBlank()) {
                                                                    Log.d(TAG, "Processing QR code: $qrCode")
                                                                    gasStationViewModel.processQRCode(qrCode)
                                                                } else {
                                                                    Log.e(TAG, "Empty QR code scanned")
                                                                    gasStationViewModel.setScanError("Empty QR code detected. Please try again.")
                                                                }
                                                            }
                                                        }
                                                    }
                                                )
                                            }

                                        try {
                                            // Unbind all use cases before rebinding
                                            cameraProvider.unbindAll()

                                            // Bind use cases to camera
                                            cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                CameraSelector.DEFAULT_BACK_CAMERA,
                                                preview,
                                                imageAnalysis
                                            )

                                        } catch (e: Exception) {
                                            Log.e(TAG, "Camera binding failed", e)
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))

                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Stop scanning button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 32.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Button(
                                onClick = { isScanning = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .height(56.dp)
                            ) {
                                Text("Cancel Scanning")
                            }
                        }
                    } else {
                        // Initial screen with start scanning button
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
                                modifier = Modifier.fillMaxWidth(0.8f),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            if (!hasCameraPermission) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Camera Permission Required",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(48.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Camera permission is required to scan QR codes",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(56.dp)
                                ) {
                                    Text("Grant Camera Permission")
                                }
                            } else {
                                Button(
                                    onClick = { isScanning = true },
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
                    }
                }

                is ScanState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Validating QR Code...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
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
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Valid",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )

                                    Spacer(modifier = Modifier.size(8.dp))

                                    Text(
                                        text = "Valid Fuel Request",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                DetailRow(label = "Request ID", value = "#${request.id.takeLast(8)}")
                                DetailRow(label = "Driver", value = request.driverName)

                                // Vehicle details
                                val vehicle = (scanState as ScanState.Success).vehicle
                                if (vehicle != null) {
                                    DetailRow(
                                        label = "Vehicle",
                                        value = "${vehicle.make} ${vehicle.model} (${vehicle.registrationNumber})"
                                    )
                                    DetailRow(label = "Fuel Type", value = vehicle.fuelType)
                                    DetailRow(label = "Tank Capacity", value = "${vehicle.tankCapacity} liters")
                                } else {
                                    DetailRow(label = "Vehicle ID", value = request.vehicleId)
                                }

                                DetailRow(label = "Approved Amount", value = "${request.requestedAmount} liters")
                                DetailRow(label = "Approval Date", value = formatDate(request.approvalDate))

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Trip Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = request.tripDetails,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                if (request.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Notes",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        text = request.notes,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Instructions card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Instructions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "1. Verify driver identity",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = "2. Dispense up to ${request.requestedAmount} liters of fuel",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = "3. Enter the actual amount dispensed",
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Text(
                                    text = "4. Confirm the transaction",
                                    style = MaterialTheme.typography.bodyLarge
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
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Error Scanning QR Code",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = (scanState as ScanState.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
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
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Processing Fuel Dispensing...",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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

                    val request = (scanState as? ScanState.Success)?.request
                    if (request != null) {
                        Text(
                            text = "Approved amount: ${request.requestedAmount} liters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    OutlinedTextField(
                        value = dispensedAmount,
                        onValueChange = { dispensedAmount = it },
                        label = { Text("Dispensed Amount (liters)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Validation message
                    val dispensedAmountValue = dispensedAmount.toDoubleOrNull()
                    if (dispensedAmount.isNotBlank() && (dispensedAmountValue == null || dispensedAmountValue <= 0)) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please enter a valid amount greater than 0",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    // Warning if dispensed amount exceeds approved amount
                    if (dispensedAmountValue != null && request != null && dispensedAmountValue > request.requestedAmount) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Warning: Dispensed amount exceeds approved amount",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
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
                    },
                    enabled = dispensedAmount.isNotBlank() && dispensedAmount.toDoubleOrNull() != null && dispensedAmount.toDouble() > 0
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
