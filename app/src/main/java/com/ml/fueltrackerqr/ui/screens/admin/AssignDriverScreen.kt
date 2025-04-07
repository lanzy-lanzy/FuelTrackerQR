package com.ml.fueltrackerqr.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.OperationState
import com.ml.fueltrackerqr.viewmodel.VehicleViewModel

/**
 * Screen for assigning a driver to a vehicle
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignDriverScreen(
    vehicleId: String,
    onBackClick: () -> Unit,
    onAssignComplete: () -> Unit,
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val drivers by vehicleViewModel.drivers.collectAsState()
    val operationState by vehicleViewModel.operationState.collectAsState()
    val selectedVehicle by vehicleViewModel.selectedVehicle.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDriverId by remember { mutableStateOf("") }

    // Filtered drivers based on search query
    val filteredDrivers = remember(drivers, searchQuery) {
        if (searchQuery.isBlank()) {
            drivers
        } else {
            drivers.filter { driver ->
                driver.name.contains(searchQuery, ignoreCase = true) ||
                driver.email.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Load vehicle and drivers when the screen is first displayed
    LaunchedEffect(key1 = vehicleId) {
        vehicleViewModel.getVehicleById(vehicleId)
        vehicleViewModel.loadDrivers()
    }

    // Handle operation state changes
    LaunchedEffect(operationState) {
        when (operationState) {
            is OperationState.Success -> {
                snackbarHostState.showSnackbar((operationState as OperationState.Success).message)
                vehicleViewModel.clearOperationState()
                onAssignComplete()
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
                        "Assign Driver",
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
            ) {
                // Vehicle info card
                selectedVehicle?.let { vehicle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF004D40).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Assigning driver to:",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "${vehicle.make} ${vehicle.model}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Registration: ${vehicle.registrationNumber}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { searchQuery = it },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("Search drivers...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Search suggestions could go here
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Drivers list
                if (operationState is OperationState.Loading && drivers.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00897B) // Medium teal
                        )
                    }
                } else if (filteredDrivers.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "No drivers found matching '$searchQuery'"
                            else
                                "No drivers found",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text(
                        text = "Select a driver:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredDrivers) { driver ->
                            DriverItem(
                                driver = driver,
                                isSelected = selectedDriverId == driver.id,
                                onClick = { selectedDriverId = driver.id }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Assign button
                PrimaryButton(
                    text = "Assign Driver",
                    onClick = {
                        if (selectedDriverId.isNotEmpty() && vehicleId.isNotEmpty()) {
                            vehicleViewModel.assignVehicleToDriver(vehicleId, selectedDriverId)
                        } else {
                            // Show error if no driver selected
                            // Show error message
                            vehicleViewModel.setErrorState("Please select a driver")
                        }
                    },
                    isLoading = operationState is OperationState.Loading,
                    enabled = selectedDriverId.isNotEmpty() && operationState !is OperationState.Loading
                )
            }
        }
    }
}

/**
 * Item component for displaying a driver in the list
 */
@Composable
private fun DriverItem(
    driver: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE0F2F1) else Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Driver icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color(0xFF00897B) else Color(0xFFE0F2F1)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Driver",
                    tint = if (isSelected) Color.White else Color(0xFF00897B),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Driver details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = driver.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF004D40)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = driver.email,
                    fontSize = 14.sp,
                    color = Color(0xFF00695C)
                )
            }

            // Radio button
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}
