package com.ml.fueltrackerqr.ui.screens.admin

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.OperationState
import com.ml.fueltrackerqr.viewmodel.VehicleViewModel

/**
 * Screen for adding a new vehicle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(
    onBackClick: () -> Unit,
    onVehicleAdded: () -> Unit,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val operationState by vehicleViewModel.operationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Form state
    var registrationNumber by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearString by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var tankCapacityString by remember { mutableStateOf("") }

    // Validation state
    var showErrors by remember { mutableStateOf(false) }

    // Dropdown state
    var fuelTypeExpanded by remember { mutableStateOf(false) }
    val fuelTypes = listOf("Petrol", "Diesel", "Electric", "Hybrid", "CNG", "LPG")

    // Handle operation state changes
    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                snackbarHostState.showSnackbar((operationState as OperationState.Success).message)
                vehicleViewModel.clearOperationState()
                onVehicleAdded()
            }
            is OperationState.Error -> {
                snackbarHostState.showSnackbar((operationState as OperationState.Error).message)
                vehicleViewModel.clearOperationState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add New Vehicle",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header card with icon
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF004D40).copy(alpha = 0.9f) // Dark teal with transparency
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Vehicle icon with circle background
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF4DB6AC), Color(0xFFF57C73))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Vehicle",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Add New Vehicle",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Fill in the details to add a new vehicle to the fleet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Vehicle details form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        // Section header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2F1))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            }

                            Spacer(modifier = Modifier.size(12.dp))

                            Text(
                                text = "Vehicle Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF004D40)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            thickness = 2.dp,
                            color = Color(0xFF80CBC4)
                        )

                        // Registration Number
                        OutlinedTextField(
                            value = registrationNumber,
                            onValueChange = { registrationNumber = it },
                            label = { Text("Registration Number") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            },
                            isError = showErrors && registrationNumber.isBlank(),
                            supportingText = {
                                if (showErrors && registrationNumber.isBlank()) {
                                    Text("Registration number is required")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF80CBC4),
                                focusedLabelColor = Color(0xFF00897B),
                                unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Make
                        OutlinedTextField(
                            value = make,
                            onValueChange = { make = it },
                            label = { Text("Make") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            },
                            isError = showErrors && make.isBlank(),
                            supportingText = {
                                if (showErrors && make.isBlank()) {
                                    Text("Make is required")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF80CBC4),
                                focusedLabelColor = Color(0xFF00897B),
                                unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Model
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            label = { Text("Model") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            },
                            isError = showErrors && model.isBlank(),
                            supportingText = {
                                if (showErrors && model.isBlank()) {
                                    Text("Model is required")
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF80CBC4),
                                focusedLabelColor = Color(0xFF00897B),
                                unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Year
                        OutlinedTextField(
                            value = yearString,
                            onValueChange = { yearString = it },
                            label = { Text("Year") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            },
                            isError = showErrors && (yearString.isBlank() || yearString.toIntOrNull() == null),
                            supportingText = {
                                if (showErrors) {
                                    when {
                                        yearString.isBlank() -> Text("Year is required")
                                        yearString.toIntOrNull() == null -> Text("Year must be a number")
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF80CBC4),
                                focusedLabelColor = Color(0xFF00897B),
                                unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        // Fuel Type Dropdown
                        ExposedDropdownMenuBox(
                            expanded = fuelTypeExpanded,
                            onExpandedChange = { fuelTypeExpanded = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            OutlinedTextField(
                                value = fuelType,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fuel Type") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF00695C)
                                    )
                                },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelTypeExpanded)
                                },
                                isError = showErrors && fuelType.isBlank(),
                                supportingText = {
                                    if (showErrors && fuelType.isBlank()) {
                                        Text("Fuel type is required")
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00897B),
                                    unfocusedBorderColor = Color(0xFF80CBC4),
                                    focusedLabelColor = Color(0xFF00897B),
                                    unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = fuelTypeExpanded,
                                onDismissRequest = { fuelTypeExpanded = false }
                            ) {
                                fuelTypes.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            fuelType = option
                                            fuelTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Tank Capacity
                        OutlinedTextField(
                            value = tankCapacityString,
                            onValueChange = { tankCapacityString = it },
                            label = { Text("Tank Capacity (liters)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C)
                                )
                            },
                            isError = showErrors && (tankCapacityString.isBlank() || tankCapacityString.toDoubleOrNull() == null),
                            supportingText = {
                                if (showErrors) {
                                    when {
                                        tankCapacityString.isBlank() -> Text("Tank capacity is required")
                                        tankCapacityString.toDoubleOrNull() == null -> Text("Tank capacity must be a number")
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF00897B),
                                unfocusedBorderColor = Color(0xFF80CBC4),
                                focusedLabelColor = Color(0xFF00897B),
                                unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Submit button
                Button(
                    onClick = {
                        showErrors = true

                        // Validate inputs
                        val year = yearString.toIntOrNull() ?: 0
                        val tankCapacity = tankCapacityString.toDoubleOrNull() ?: 0.0

                        val isValid = registrationNumber.isNotBlank() &&
                                make.isNotBlank() &&
                                model.isNotBlank() &&
                                yearString.isNotBlank() &&
                                year > 0 &&
                                fuelType.isNotBlank() &&
                                tankCapacityString.isNotBlank() &&
                                tankCapacity > 0

                        if (isValid) {
                            vehicleViewModel.addVehicle(
                                registrationNumber = registrationNumber,
                                make = make,
                                model = model,
                                year = year,
                                fuelType = fuelType,
                                tankCapacity = tankCapacity
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B) // Medium teal
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = operationState !is OperationState.Loading
                ) {
                    if (operationState is OperationState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Add Vehicle",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
