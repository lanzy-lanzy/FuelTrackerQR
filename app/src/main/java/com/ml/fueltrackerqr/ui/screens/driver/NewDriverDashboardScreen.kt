package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.viewmodel.RequestState
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import kotlinx.coroutines.delay

/**
 * New implementation of the Driver Dashboard Screen
 * Based on the provided screenshot with Quick Actions, Statistics, and Fuel Requests
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewDriverDashboardScreen(
    onNewRequestClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRequestClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val requestState by driverViewModel.requestState.collectAsState()
    val requests by driverViewModel.driverRequests.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var isRefreshing by remember { mutableStateOf(false) }

    // Refresh data when screen is shown
    LaunchedEffect(Unit) {
        currentUser?.id?.let { driverViewModel.loadDriverRequests(it) }
    }

    // Handle refresh with animation
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            currentUser?.id?.let { driverViewModel.loadDriverRequests(it) }
            delay(1000) // Show refresh animation for at least 1 second
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Driver Dashboard",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { isRefreshing = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Logout",
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
        containerColor = Color.Transparent, // Make scaffold transparent to show gradient
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show loading indicator if refreshing
            if (isRefreshing || requestState is RequestState.Loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.TopCenter),
                    color = Color(0xFF4DB6AC) // Light teal
                )
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Show all requests directly as in the screenshot
                if (requests.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No fuel requests yet. Create your first request!",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Sort requests by status and date
                    val sortedRequests = requests.sortedWith(
                        compareBy<FuelRequest> {
                            when(it.status) {
                                RequestStatus.PENDING -> 0
                                RequestStatus.APPROVED -> 1
                                RequestStatus.DISPENSED -> 2
                                else -> 3
                            }
                        }.thenByDescending { it.requestDate }
                    )

                    // Display all requests
                    sortedRequests.forEach { request ->
                        SimpleFuelRequestCard(
                            request = request,
                            onClick = {
                                // Navigate to request detail screen
                                onRequestClick(request.id)
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00897B)
) {
    Box(
        modifier = modifier
            .background(color)
    )
}

@Composable
fun QuickActionsCard(
    onNewRequestClick: () -> Unit,
    onViewHistoryClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00796B).copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Access common features quickly",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton(
                    icon = Icons.Default.Add,
                    label = "New Request",
                    onClick = onNewRequestClick
                )

                QuickActionButton(
                    icon = Icons.Default.Info,
                    label = "View History",
                    onClick = onViewHistoryClick
                )

                QuickActionButton(
                    icon = Icons.Default.AccountCircle,
                    label = "Profile",
                    onClick = onProfileClick
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF00897B))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatisticsCard(requests: List<FuelRequest>) {
    // Count requests by status
    val pendingCount = requests.count { it.status == RequestStatus.PENDING }
    val approvedCount = requests.count { it.status == RequestStatus.APPROVED }
    val dispensedCount = requests.count { it.status == RequestStatus.DISPENSED }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238).copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Fuel Request Statistics",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Overview of your fuel requests",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    value = pendingCount.toString(),
                    label = "Pending",
                    color = Color(0xFFFFC107) // Amber
                )

                StatisticItem(
                    value = approvedCount.toString(),
                    label = "Approved",
                    color = Color(0xFF4DB6AC) // Teal
                )

                StatisticItem(
                    value = dispensedCount.toString(),
                    label = "Dispensed",
                    color = Color(0xFF5C6BC0) // Indigo
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RecentRequestsList(requests: List<FuelRequest>, onRequestClick: (String) -> Unit = {}) {
    if (requests.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF00796B).copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No fuel requests yet. Create your first request!",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        // Show the most recent 5 requests
        requests.sortedByDescending { it.requestDate }.take(5).forEach { request ->
            SimpleFuelRequestCard(
                request = request,
                onClick = { onRequestClick(request.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun SimpleFuelRequestCard(request: FuelRequest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00695C) // Darker teal for card background
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Status indicator dot at top right
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Status indicator dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(getStatusColor(request.status))
                        .align(Alignment.TopEnd)
                )
            }

            // Request details
            Text(
                text = "Amount: ${request.requestedAmount} liters",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Text(
                text = "Date: ${java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date(request.requestDate))}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Request ID and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Request ${request.id.take(6)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )

                // Status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(getStatusChipColor(request.status))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = request.status.name,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun getStatusColor(status: RequestStatus): Color {
    return when (status) {
        RequestStatus.PENDING -> Color(0xFFFFC107) // Yellow
        RequestStatus.APPROVED -> Color(0xFF4CAF50) // Green
        RequestStatus.DECLINED -> Color(0xFFF44336) // Red
        RequestStatus.DISPENSED -> Color(0xFF2196F3) // Blue
    }
}

@Composable
fun getStatusChipColor(status: RequestStatus): Color {
    return when (status) {
        RequestStatus.PENDING -> Color(0xFFFFC107) // Yellow
        RequestStatus.APPROVED -> Color(0xFF4CAF50) // Green
        RequestStatus.DECLINED -> Color(0xFFF44336) // Red
        RequestStatus.DISPENSED -> Color(0xFF2196F3) // Blue
    }
}

@Composable
fun StatusBadge(status: RequestStatus) {
    val (backgroundColor, textColor) = when (status) {
        RequestStatus.PENDING -> Color(0xFFFFC107) to Color(0xFF000000)
        RequestStatus.APPROVED -> Color(0xFF4CAF50) to Color.White
        RequestStatus.DECLINED -> Color(0xFFF44336) to Color.White
        RequestStatus.DISPENSED -> Color(0xFF2196F3) to Color.White
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(backgroundColor)
    )
}

@Composable
fun StatusChip(status: RequestStatus) {
    val (backgroundColor, text) = when (status) {
        RequestStatus.PENDING -> Color(0xFFFFC107) to "PENDING"
        RequestStatus.APPROVED -> Color(0xFF4CAF50) to "APPROVED"
        RequestStatus.DECLINED -> Color(0xFFF44336) to "DECLINED"
        RequestStatus.DISPENSED -> Color(0xFF2196F3) to "DISPENSED"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}
