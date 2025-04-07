package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ExitToApp

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.GradientBrushes
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

    val requestState by driverViewModel.requestState.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Function to refresh data
    fun refreshData() {
        currentUser?.let { user ->
            isRefreshing = true
            driverViewModel.loadDriverRequests(user.id)
            driverViewModel.loadDriverVehicles(user.id)
        }
    }

    // Initial data loading
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            driverViewModel.loadDriverRequests(user.id)
            driverViewModel.loadDriverVehicles(user.id)
        }
    }

    // Handle request state changes
    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestState.Error -> {
                snackbarHostState.showSnackbar((requestState as RequestState.Error).message)
                isRefreshing = false
            }
            is RequestState.Success -> {
                isRefreshing = false
            }
            is RequestState.Initial -> {
                isRefreshing = false
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Driver Dashboard",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Refresh button
                    IconButton(onClick = { refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }

                    // Logout button
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewRequestClick,
                icon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "New Request",
                        tint = Color.White
                    )
                },
                text = {
                    Text(
                        "New Request",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = Color(0xFFF57C73),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            if (currentUser == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF00897B)
                )
            } else {
                // Show loading indicator if refreshing
                if (isRefreshing || requestState is RequestState.Loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFF00897B))
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                        // Welcome card with gradient background
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(DarkTeal, MediumTeal)
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // User icon
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Dashboard",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = "Welcome,",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )

                                    Text(
                                        text = "${currentUser?.name}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Stats card showing request counts
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1F2937).copy(alpha = 0.7f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Pending count
                            val pendingCount = driverRequests.count { it.status == RequestStatus.PENDING }
                            StatItem(
                                count = pendingCount,
                                label = "Pending",
                                color = Color(0xFFFCD34D)
                            )

                            // Approved count
                            val approvedCount = driverRequests.count { it.status == RequestStatus.APPROVED }
                            StatItem(
                                count = approvedCount,
                                label = "Approved",
                                color = Color(0xFF4DB6AC)
                            )

                            // Dispensed count
                            val dispensedCount = driverRequests.count { it.status == RequestStatus.DISPENSED }
                            StatItem(
                                count = dispensedCount,
                                label = "Dispensed",
                                color = Color(0xFF60A5FA)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Section title with accent
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(24.dp)
                                .background(Color(0xFFF57C73), RoundedCornerShape(4.dp))
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Your Fuel Requests",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (requestState is RequestState.Loading && driverRequests.isEmpty()) {
                        // Loading state
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF00897B))
                        }
                    } else if (driverRequests.isEmpty()) {
                        // Enhanced empty state with card and icon
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F2937).copy(alpha = 0.7f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Empty icon
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFFF57C73).copy(alpha = 0.7f), Color(0xFF00897B).copy(alpha = 0.7f))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Request",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "No Fuel Requests Yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Create a new request by clicking the button below.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    } else {
                        // List of requests
                        Box(modifier = Modifier.fillMaxSize()) {
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
}

/**
 * Enhanced card displaying a fuel request with better styling
 *
 * @param request The fuel request to display
 */
@Composable
fun FuelRequestCard(request: FuelRequest) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937).copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Request ID with accent color
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00897B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${request.id.takeLast(2)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4DB6AC)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Request ${request.id.takeLast(8)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                RequestStatusChip(status = request.status)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.White.copy(alpha = 0.1f),
                thickness = 1.dp
            )

            // Fuel amount section with improved styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Requested amount with icon
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF57C73))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Requested Amount:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Text(
                        text = "${request.requestedAmount} liters",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Dispensed amount (if applicable)
                if (request.status == RequestStatus.DISPENSED) {
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4DB6AC))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Dispensed Amount:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Text(
                            text = "${request.dispensedAmount} liters",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4DB6AC)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Trip details with better styling
            Column {
                Text(
                    text = "Trip Details",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Text(
                    text = request.tripDetails,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dates section with improved styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Requested",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Text(
                        text = formatDate(request.requestDate),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                if (request.status == RequestStatus.APPROVED || request.status == RequestStatus.DECLINED) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Reviewed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        Text(
                            text = formatDate(request.approvalDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                if (request.status == RequestStatus.DISPENSED) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Dispensed",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.5f)
                        )

                        Text(
                            text = formatDate(request.dispensedDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Notes section (if applicable)
            if (request.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.1f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Text(
                        text = request.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

/**
 * Enhanced chip displaying the status of a request with gradient background
 *
 * @param status Status of the request
 */
@Composable
fun RequestStatusChip(status: RequestStatus) {
    // Define colors and gradients for each status
    val (backgroundColor, textColor, gradientBrush) = when (status) {
        RequestStatus.PENDING -> Triple(
            Color(0xFFFFF9C4).copy(alpha = 0.2f),
            Color(0xFFFCD34D),
            Brush.horizontalGradient(listOf(Color(0xFFFCD34D).copy(alpha = 0.7f), Color(0xFFFCD34D).copy(alpha = 0.3f)))
        )
        RequestStatus.APPROVED -> Triple(
            Color(0xFFE8F5E9).copy(alpha = 0.2f),
            Color(0xFF4DB6AC),
            Brush.horizontalGradient(listOf(Color(0xFF4DB6AC).copy(alpha = 0.7f), Color(0xFF4DB6AC).copy(alpha = 0.3f)))
        )
        RequestStatus.DECLINED -> Triple(
            Color(0xFFFFEBEE).copy(alpha = 0.2f),
            Color(0xFFF57C73),
            Brush.horizontalGradient(listOf(Color(0xFFF57C73).copy(alpha = 0.7f), Color(0xFFF57C73).copy(alpha = 0.3f)))
        )
        RequestStatus.DISPENSED -> Triple(
            Color(0xFFE3F2FD).copy(alpha = 0.2f),
            Color(0xFF60A5FA),
            Brush.horizontalGradient(listOf(Color(0xFF60A5FA).copy(alpha = 0.7f), Color(0xFF60A5FA).copy(alpha = 0.3f)))
        )
    }

    // Status chip with gradient background
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(gradientBrush)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Status text
            Text(
                text = status.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Composable for displaying a statistic item in the dashboard
 *
 * @param count The count to display
 * @param label The label for the statistic
 * @param color The accent color for the statistic
 */
@Composable
private fun StatItem(count: Int, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        // Count with circular background
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.8f)
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

