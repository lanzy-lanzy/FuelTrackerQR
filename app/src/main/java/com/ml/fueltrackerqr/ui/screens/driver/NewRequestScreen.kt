package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.RequestState

/**
 * Screen for creating a new fuel request
 * 
 * @param onRequestSubmitted Callback when a request is successfully submitted
 * @param onBackClick Callback when back button is clicked
 * @param driverViewModel ViewModel for driver operations
 * @param authViewModel ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRequestScreen(
    onRequestSubmitted: () -> Unit,
    onBackClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val driverVehicles by driverViewModel.driverVehicles.collectAsState()
    val requestState by driverViewModel.requestState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var requestedAmount by remember { mutableStateOf("") }
    var tripDetails by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }
    
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            driverViewModel.loadDriverVehicles(user.id)
        }
    }
    
    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestState.Success -> {
                snackbarHostState.showSnackbar((requestState as RequestState.Success).message)
                onRequestSubmitted()
            }
            is RequestState.Error -> {
                snackbarHostState.showSnackbar((requestState as RequestState.Error).message)
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Fuel Request") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "Create New Fuel Request",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
                // Vehicle selection dropdown
                ExposedDropdownMenuBox(
                    expanded = isVehicleDropdownExpanded,
                    onExpandedChange = { isVehicleDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedVehicle?.let { "${it.make} ${it.model} (${it.registrationNumber})" } ?: "Select Vehicle",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isVehicleDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Vehicle") }
                    )
                    
                    ExposedDropdownMenu(
                        expanded = isVehicleDropdownExpanded,
                        onDismissRequest = { isVehicleDropdownExpanded = false }
                    ) {
                        if (driverVehicles.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No vehicles assigned") },
                                onClick = { isVehicleDropdownExpanded = false }
                            )
                        } else {
                            driverVehicles.forEach { vehicle ->
                                DropdownMenuItem(
                                    text = { Text("${vehicle.make} ${vehicle.model} (${vehicle.registrationNumber})") },
                                    onClick = {
                                        selectedVehicle = vehicle
                                        isVehicleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Requested amount field
                OutlinedTextField(
                    value = requestedAmount,
                    onValueChange = { requestedAmount = it },
                    label = { Text("Requested Amount (liters)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Trip details field
                OutlinedTextField(
                    value = tripDetails,
                    onValueChange = { tripDetails = it },
                    label = { Text("Trip Details") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Notes field
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Additional Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Submit button
                Button(
                    onClick = {
                        if (validateInputs(selectedVehicle, requestedAmount, tripDetails)) {
                            currentUser?.let { user ->
                                driverViewModel.createFuelRequest(
                                    driver = user,
                                    vehicleId = selectedVehicle!!.id,
                                    requestedAmount = requestedAmount.toDouble(),
                                    tripDetails = tripDetails,
                                    notes = notes
                                )
                            }
                        } else {
                            // Show validation error
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = requestState !is RequestState.Loading
                ) {
                    Text("Submit Request")
                }
            }
            
            if (requestState is RequestState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

/**
 * Validate inputs for a new fuel request
 * 
 * @param selectedVehicle Selected vehicle
 * @param requestedAmount Requested amount of fuel
 * @param tripDetails Trip details
 * @return True if inputs are valid, false otherwise
 */
private fun validateInputs(
    selectedVehicle: Vehicle?,
    requestedAmount: String,
    tripDetails: String
): Boolean {
    return selectedVehicle != null &&
            requestedAmount.isNotBlank() &&
            requestedAmount.toDoubleOrNull() != null &&
            requestedAmount.toDouble() > 0 &&
            tripDetails.isNotBlank()
}
