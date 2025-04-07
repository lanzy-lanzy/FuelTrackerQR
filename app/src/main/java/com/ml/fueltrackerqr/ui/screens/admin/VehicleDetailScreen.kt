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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.OperationState
import com.ml.fueltrackerqr.viewmodel.VehicleViewModel

/**
 * Screen for displaying and managing vehicle details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onAssignClick: (String) -> Unit,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val selectedVehicle by vehicleViewModel.selectedVehicle.collectAsState()
    val operationState by vehicleViewModel.operationState.collectAsState()
    val drivers by vehicleViewModel.drivers.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var assignedDriver by remember { mutableStateOf<User?>(null) }

    // Load vehicle and drivers when the screen is first displayed
    LaunchedEffect(key1 = vehicleId) {
        vehicleViewModel.getVehicleById(vehicleId)
        vehicleViewModel.loadDrivers()
    }

    // Update assigned driver when vehicle changes
    LaunchedEffect(selectedVehicle, drivers) {
        if (selectedVehicle != null && selectedVehicle?.assignedDriverId?.isNotEmpty() == true) {
            assignedDriver = drivers.find { it.id == selectedVehicle?.assignedDriverId }
        } else {
            assignedDriver = null
        }
    }

    // Show snackbar for operation state
    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                snackbarHostState.showSnackbar((operationState as OperationState.Success).message)
                vehicleViewModel.clearOperationState()
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
                        "Vehicle Details",
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
                actions = {
                    IconButton(onClick = { onEditClick(vehicleId) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
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
            if (operationState is OperationState.Loading && selectedVehicle == null) {
                // Show loading indicator while loading vehicle
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF00897B) // Medium teal
                    )
                }
            } else if (selectedVehicle == null) {
                // Show error message if vehicle not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Vehicle not found",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                // Show vehicle details
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Vehicle header card
                    VehicleHeaderCard(vehicle = selectedVehicle!!)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Vehicle details card
                    VehicleDetailsCard(vehicle = selectedVehicle!!)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Driver assignment card
                    DriverAssignmentCard(
                        vehicle = selectedVehicle!!,
                        assignedDriver = assignedDriver,
                        onAssignClick = { onAssignClick(vehicleId) },
                        onUnassignClick = {
                            vehicleViewModel.unassignVehicle(vehicleId)
                        },
                        isLoading = operationState is OperationState.Loading
                    )
                }
            }
        }
    }
}

@Composable
private fun VehicleHeaderCard(vehicle: Vehicle) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF004D40).copy(alpha = 0.9f) // Dark teal with transparency
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
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

            Spacer(modifier = Modifier.width(16.dp))

            // Vehicle name and registration
            Column {
                Text(
                    text = "${vehicle.make} ${vehicle.model}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vehicle.registrationNumber,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = vehicle.year.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun VehicleDetailsCard(vehicle: Vehicle) {
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

            // Vehicle details
            VehicleDetailRow(label = "Registration", value = vehicle.registrationNumber)
            VehicleDetailRow(label = "Make", value = vehicle.make)
            VehicleDetailRow(label = "Model", value = vehicle.model)
            VehicleDetailRow(label = "Year", value = vehicle.year.toString())
            VehicleDetailRow(label = "Fuel Type", value = vehicle.fuelType)
            VehicleDetailRow(label = "Tank Capacity", value = "${vehicle.tankCapacity} liters")
        }
    }
}

@Composable
private fun VehicleDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
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

@Composable
private fun DriverAssignmentCard(
    vehicle: Vehicle,
    assignedDriver: User?,
    onAssignClick: () -> Unit,
    onUnassignClick: () -> Unit,
    isLoading: Boolean
) {
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
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF00695C)
                    )
                }

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "Driver Assignment",
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

            // Driver assignment status
            if (assignedDriver != null) {
                // Show assigned driver info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE0F2F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Driver",
                            tint = Color(0xFF00897B),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = assignedDriver.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF004D40)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = assignedDriver.email,
                            fontSize = 14.sp,
                            color = Color(0xFF00695C)
                        )
                    }
                }

                // Unassign button
                Button(
                    onClick = onUnassignClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF57C73) // Coral
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Unassign Driver")
                    }
                }
            } else {
                // Show no driver assigned message
                Text(
                    text = "No driver assigned to this vehicle",
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color(0xFF00695C)
                )

                // Assign button
                Button(
                    onClick = onAssignClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00897B) // Medium teal
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Assign Driver")
                    }
                }
            }
        }
    }
}
