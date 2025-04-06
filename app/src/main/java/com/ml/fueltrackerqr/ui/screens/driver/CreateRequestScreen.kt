package com.ml.fueltrackerqr.ui.screens.driver

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * Screen for creating a new fuel request
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRequestScreen(
    onBackClick: () -> Unit,
    onRequestCreated: () -> Unit,
    userId: String,
    userName: String
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    // Form fields
    var vehicleInfo by remember { mutableStateOf("") }
    var fuelAmount by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            snackbarHostState.showSnackbar("Request created successfully!")
            onRequestCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Fuel Request", color = TextPrimary) },
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
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Form card with semi-transparent background
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
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Vehicle information
                            OutlinedTextField(
                                value = vehicleInfo,
                                onValueChange = { vehicleInfo = it },
                                label = { Text("Vehicle Information") },
                                placeholder = { Text("e.g. Toyota Corolla - ABC123") },
                                modifier = Modifier.fillMaxWidth(),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Fuel amount
                            OutlinedTextField(
                                value = fuelAmount,
                                onValueChange = { fuelAmount = it },
                                label = { Text("Fuel Amount (Liters)") },
                                placeholder = { Text("e.g. 20") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Destination
                            OutlinedTextField(
                                value = destination,
                                onValueChange = { destination = it },
                                label = { Text("Destination") },
                                placeholder = { Text("e.g. City Center") },
                                modifier = Modifier.fillMaxWidth(),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Purpose
                            OutlinedTextField(
                                value = purpose,
                                onValueChange = { purpose = it },
                                label = { Text("Purpose") },
                                placeholder = { Text("e.g. Business meeting") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit button
                    PrimaryButton(
                        text = "Submit Request",
                        onClick = {
                            if (validateInputs(vehicleInfo, fuelAmount, destination)) {
                                isLoading = true
                                // In a real app, this would call a ViewModel method to create the request
                                // For now, we'll just simulate a successful request creation
                                // createFuelRequest(userId, userName, vehicleInfo, fuelAmount.toDouble(), destination, purpose)
                                isLoading = false
                                isSuccess = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
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

                // Success indicator
                AnimatedVisibility(
                    visible = isSuccess,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
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
                                tint = Primary,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Success!",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Validate the input fields
 */
private fun validateInputs(
    vehicleInfo: String,
    fuelAmount: String,
    destination: String
): Boolean {
    return vehicleInfo.isNotBlank() &&
            fuelAmount.isNotBlank() &&
            destination.isNotBlank() &&
            fuelAmount.toDoubleOrNull() != null &&
            (fuelAmount.toDoubleOrNull() ?: 0.0) > 0
}

/**
 * Create a new fuel request
 */
private fun createFuelRequest(
    userId: String,
    driverName: String,
    vehicleInfo: String,
    fuelAmount: Double,
    destination: String,
    purpose: String
): FuelRequest {
    return FuelRequest(
        id = "", // This would be generated by the database
        userId = userId,
        driverName = driverName,
        vehicleInfo = vehicleInfo,
        fuelAmount = fuelAmount,
        destination = destination,
        purpose = purpose,
        status = RequestStatus.PENDING,
        timestamp = System.currentTimeMillis(),
        approvedBy = null,
        approvedAt = null,
        declinedBy = null,
        declinedAt = null,
        declineReason = null,
        dispensedAt = null,
        dispensedBy = null
    )
}
