package com.ml.fueltrackerqr.ui.screens.driver

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dashboard screen for drivers
 *
 * @param onNewRequestClick Callback when new request button is clicked
 * @param onLogoutClick Callback when logout button is clicked
 * @param driverViewModel ViewModel for driver operations
 * @param authViewModel ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    onNewRequestClick: () -> Unit,
    onLogoutClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val driverRequests by driverViewModel.driverRequests.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            driverViewModel.loadDriverRequests(user.id)
            driverViewModel.loadDriverVehicles(user.id)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Driver Dashboard") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewRequestClick,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Request") },
                text = { Text("New Request") }
            )
        }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            if (currentUser == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Welcome, ${currentUser?.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Fuel Requests",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (driverRequests.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No fuel requests yet. Create a new request.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(driverRequests) { request ->
                                FuelRequestCard(request = request)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a fuel request
 *
 * @param request The fuel request to display
 */
@Composable
fun FuelRequestCard(request: FuelRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                RequestStatusChip(status = request.status)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Requested Amount:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${request.requestedAmount} liters",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (request.status == RequestStatus.DISPENSED) {
                    Column {
                        Text(
                            text = "Dispensed Amount:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${request.dispensedAmount} liters",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trip Details: ${request.tripDetails}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Requested: ${formatDate(request.requestDate)}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (request.status == RequestStatus.APPROVED || request.status == RequestStatus.DECLINED) {
                    Text(
                        text = "Reviewed: ${formatDate(request.approvalDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (request.status == RequestStatus.DISPENSED) {
                    Text(
                        text = "Dispensed: ${formatDate(request.dispensedDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notes: ${request.notes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Chip displaying the status of a request
 *
 * @param status Status of the request
 */
@Composable
fun RequestStatusChip(status: RequestStatus) {
    val (backgroundColor, textColor) = when (status) {
        RequestStatus.PENDING -> Pair(Color(0xFFFFF9C4), Color(0xFF8C6D1F))
        RequestStatus.APPROVED -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        RequestStatus.DECLINED -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        RequestStatus.DISPENSED -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .height(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(8.dp)
                    .padding(end = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawCircle(color = textColor)
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = status.name,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
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
