package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateDpAsState
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.ui.components.TealCoralGradientBackground
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.theme.DarkTeal
import com.ml.fueltrackerqr.ui.theme.MediumTeal
import com.ml.fueltrackerqr.ui.theme.LightCoral
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.RequestState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Screen for creating a new fuel request with enhanced UI and additional fields
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

    // Form state variables
    var selectedVehicle by remember { mutableStateOf<Vehicle?>(null) }
    var requestedAmount by remember { mutableStateOf("") }
    var tripDetails by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }

    // Date picker state
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }

    // Validation state
    var showErrors by remember { mutableStateOf(false) }
    var validationErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Format date for display
    val dateFormatter = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault())
    val formattedDate = selectedDate?.let { dateFormatter.format(Date(it)) } ?: "Select Date"

    // Load driver vehicles when user is available
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            driverViewModel.loadDriverVehicles(user.id)
        }
    }

    // Handle request state changes
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

    // Enhanced date picker dialog with better styling
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDate = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text(
                        "Confirm",
                        color = Color(0xFF00897B),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        "Cancel",
                        color = Color(0xFF00897B)
                    )
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF00695C),
                headlineContentColor = Color(0xFF00695C),
                weekdayContentColor = Color(0xFF00897B),
                subheadContentColor = Color(0xFF00897B),
                yearContentColor = Color(0xFF00897B),
                currentYearContentColor = Color(0xFFF57C73),
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = Color(0xFF00897B),
                dayContentColor = Color(0xFF00695C),
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFF00897B),
                todayContentColor = Color(0xFFF57C73),
                todayDateBorderColor = Color(0xFFF57C73)
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "New Fuel Request",
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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Enhanced header card with icon
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
                        // Fuel icon with circle background
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
                                imageVector = Icons.Default.Info,
                                contentDescription = "Fuel",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Create New Fuel Request",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Complete all fields to submit your fuel request",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Vehicle selection section
                SectionCard(
                    title = "Vehicle Information",
                    icon = Icons.Default.Person
                ) {
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
                            label = { Text("Vehicle") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF00695C) // Darker teal
                                )
                            },
                            isError = showErrors && selectedVehicle == null,
                            supportingText = {
                                if (showErrors && selectedVehicle == null) {
                                    Text("Please select a vehicle")
                                }
                            }
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

                    // Vehicle details if selected
                    AnimatedVisibility(
                        visible = selectedVehicle != null,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        selectedVehicle?.let { vehicle ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFE0F2F1) // Very light teal
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    DetailRow("Make & Model", "${vehicle.make} ${vehicle.model}")
                                    DetailRow("Registration", vehicle.registrationNumber)
                                    DetailRow("Year", vehicle.year.toString())
                                    DetailRow("Fuel Type", vehicle.fuelType)
                                    DetailRow("Tank Capacity", "${vehicle.tankCapacity} liters")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Odometer reading with enhanced styling
                    OutlinedTextField(
                        value = odometer,
                        onValueChange = { odometer = it },
                        label = { Text("Current Odometer Reading (km)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        isError = showErrors && odometer.isBlank(),
                        supportingText = {
                            if (showErrors && odometer.isBlank()) {
                                Text("Please enter current odometer reading")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fuel request details section
                SectionCard(
                    title = "Fuel Request Details",
                    icon = Icons.Default.Info
                ) {
                    // Requested amount field with enhanced styling
                    OutlinedTextField(
                        value = requestedAmount,
                        onValueChange = { requestedAmount = it },
                        label = { Text("Requested Amount (liters)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        isError = showErrors && (requestedAmount.isBlank() || requestedAmount.toDoubleOrNull() == null || requestedAmount.toDoubleOrNull() ?: 0.0 <= 0),
                        supportingText = {
                            if (showErrors) {
                                when {
                                    requestedAmount.isBlank() -> Text("Please enter requested amount")
                                    requestedAmount.toDoubleOrNull() == null -> Text("Please enter a valid number")
                                    requestedAmount.toDoubleOrNull() ?: 0.0 <= 0 -> Text("Amount must be greater than 0")
                                }
                            } else {
                                selectedVehicle?.let { vehicle ->
                                    Text("Tank capacity: ${vehicle.tankCapacity} liters")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trip date field with enhanced styling
                    OutlinedTextField(
                        value = formattedDate,
                        onValueChange = {},
                        label = { Text("Trip Date") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFF00897B) // Medium teal
                                )
                            }
                        },
                        isError = showErrors && selectedDate == null,
                        supportingText = {
                            if (showErrors && selectedDate == null) {
                                Text("Please select a trip date")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Trip details section
                SectionCard(
                    title = "Trip Information",
                    icon = Icons.Default.LocationOn
                ) {
                    // Destination field with enhanced styling
                    OutlinedTextField(
                        value = destination,
                        onValueChange = { destination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        isError = showErrors && destination.isBlank(),
                        supportingText = {
                            if (showErrors && destination.isBlank()) {
                                Text("Please enter destination")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trip details field with enhanced styling
                    OutlinedTextField(
                        value = tripDetails,
                        onValueChange = { tripDetails = it },
                        label = { Text("Trip Purpose & Details") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        isError = showErrors && tripDetails.isBlank(),
                        supportingText = {
                            if (showErrors && tripDetails.isBlank()) {
                                Text("Please enter trip details")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes field with enhanced styling
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Additional Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF00695C) // Darker teal
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00897B),
                            unfocusedBorderColor = Color(0xFF80CBC4),
                            focusedLabelColor = Color(0xFF00897B),
                            unfocusedLabelColor = Color(0xFF00897B).copy(alpha = 0.7f),
                            cursorColor = Color(0xFF00897B)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Enhanced error summary with better styling
                AnimatedVisibility(visible = showErrors && hasValidationErrors(selectedVehicle, requestedAmount, tripDetails, destination, odometer, selectedDate)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF57C73).copy(alpha = 0.15f) // Light coral with transparency
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Error icon with circle background
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF57C73).copy(alpha = 0.2f))
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Error",
                                    tint = Color(0xFFF57C73), // Dark coral
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Please fill in all required fields correctly",
                                color = Color(0xFFF57C73), // Dark coral
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Enhanced submit button with gradient and animation
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val buttonElevation by animateDpAsState(
                    targetValue = if (isPressed) 2.dp else 8.dp,
                    label = "Button Elevation"
                )

                Button(
                    onClick = {
                        showErrors = true
                        if (!hasValidationErrors(selectedVehicle, requestedAmount, tripDetails, destination, odometer, selectedDate)) {
                            currentUser?.let { user ->
                                // Combine all details into trip details
                                val fullTripDetails = buildString {
                                    append("Destination: $destination\n")
                                    append("Trip Date: $formattedDate\n")
                                    append("Odometer Reading: $odometer km\n\n")
                                    append(tripDetails)
                                }

                                driverViewModel.createFuelRequest(
                                    driver = user,
                                    vehicleId = selectedVehicle!!.id,
                                    requestedAmount = requestedAmount.toDouble(),
                                    tripDetails = fullTripDetails,
                                    notes = notes
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(buttonElevation, RoundedCornerShape(28.dp)),
                    enabled = requestState !is RequestState.Loading,
                    interactionSource = interactionSource,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFF57C73), Color(0xFF00897B))
                                ),
                                shape = RoundedCornerShape(28.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (requestState is RequestState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Submit Fuel Request",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Enhanced section card component for grouping related fields
 */
@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Enhanced section header with accent background
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Icon with circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2F1))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFF00695C), // Darker teal
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40) // Dark teal
                )
            }

            // Divider with gradient
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                thickness = 2.dp,
                color = Color(0xFF80CBC4) // Light teal
            )

            // Section content
            content()
        }
    }
}

/**
 * Enhanced detail row component for displaying vehicle information
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label with dot indicator
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF80CBC4)) // Light teal
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF004D40).copy(alpha = 0.7f) // Dark teal with transparency
            )
        }

        // Value with bold text
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF004D40) // Dark teal
        )
    }
}

/**
 * Check if there are any validation errors in the form
 */
private fun hasValidationErrors(
    selectedVehicle: Vehicle?,
    requestedAmount: String,
    tripDetails: String,
    destination: String,
    odometer: String,
    selectedDate: Long?
): Boolean {
    return selectedVehicle == null ||
            requestedAmount.isBlank() ||
            requestedAmount.toDoubleOrNull() == null ||
            (requestedAmount.toDoubleOrNull() ?: 0.0) <= 0 ||
            tripDetails.isBlank() ||
            destination.isBlank() ||
            odometer.isBlank() ||
            selectedDate == null
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
