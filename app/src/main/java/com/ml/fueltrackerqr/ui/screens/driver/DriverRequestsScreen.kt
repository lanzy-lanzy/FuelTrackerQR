package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import android.util.Log
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.RequestState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for displaying driver's fuel requests
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRequestsScreen(
    onBackClick: () -> Unit,
    onNewRequestClick: () -> Unit,
    onRequestClick: (String) -> Unit,
    onHistoryClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val driverRequests by driverViewModel.driverRequests.collectAsState()

    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Log all requests for debugging
    LaunchedEffect(driverRequests) {
        Log.d("DriverRequestsScreen", "Total requests in driverRequests: ${driverRequests.size}")
        driverRequests.forEachIndexed { index, request ->
            Log.d("DriverRequestsScreen", "Request $index - ID: ${request.id}, Status: ${request.status}, DriverId: ${request.driverId}")
        }
    }

    // Filter requests based on search query and only show pending and approved
    val filteredRequests = remember(driverRequests, searchQuery) {
        // Log before filtering
        Log.d("DriverRequestsScreen", "Filtering ${driverRequests.size} requests")

        // Don't filter by status for now to see all requests
        val activeRequests = driverRequests

        // Log after status filtering
        Log.d("DriverRequestsScreen", "After status filtering: ${activeRequests.size} requests")

        val result = if (searchQuery.isBlank()) {
            activeRequests
        } else {
            activeRequests.filter { request ->
                request.id.contains(searchQuery, ignoreCase = true) ||
                request.tripDetails.contains(searchQuery, ignoreCase = true) ||
                request.status.name.contains(searchQuery, ignoreCase = true)
            }
        }

        // Log final result
        Log.d("DriverRequestsScreen", "Final filtered requests: ${result.size}")
        result
    }

    // State for request state
    val requestState by driverViewModel.requestState.collectAsState()

    // Function to refresh data
    fun refreshData() {
        isLoading = true
        currentUser?.let { user ->
            driverViewModel.loadDriverRequests(user.id)
        }
    }

    // Load driver requests when the screen is first displayed
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            Log.d("DriverRequestsScreen", "Loading requests for user: ${user.id}")
            driverViewModel.loadDriverRequests(user.id)
        } ?: run {
            Log.e("DriverRequestsScreen", "Current user is null, cannot load requests")
            snackbarHostState.showSnackbar("Please log in to view your requests")
        }
    }

    // State for warning message
    var warningMessage by remember { mutableStateOf("") }

    // Handle request state changes
    LaunchedEffect(requestState) {
        when (requestState) {
            is RequestState.Loading -> {
                isLoading = true
                warningMessage = ""
            }
            is RequestState.Success -> {
                isLoading = false
                warningMessage = ""
                val message = (requestState as RequestState.Success).message
                snackbarHostState.showSnackbar(message)
            }
            is RequestState.Initial -> {
                isLoading = false
                warningMessage = ""
            }
            is RequestState.Warning -> {
                isLoading = false
                val message = (requestState as RequestState.Warning).message
                warningMessage = message
            }
            is RequestState.Error -> {
                isLoading = false
                warningMessage = ""
                val message = (requestState as RequestState.Error).message
                snackbarHostState.showSnackbar(message)
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Fuel Requests",
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
                    // Refresh button
                    IconButton(onClick = { refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }

                    // History button
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "History",
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
        // Remove the FAB since we already have a New Request button in the bottom navigation
        // This eliminates redundancy and potential confusion
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        // Use fillMaxSize to ensure the background covers the entire screen
        SplashGradientBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Warning banner for connectivity issues
                if (warningMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFA000).copy(alpha = 0.2f))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = warningMessage,
                                color = Color(0xFFFFA000),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                // Search bar with improved styling
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { searchQuery = it },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("Search requests...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF00695C) // Teal color for icon
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(28.dp)) // More rounded corners
                ) {
                    // Search suggestions could go here
                }

                if (isLoading) {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF00897B) // Medium teal
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Loading requests...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else if (filteredRequests.isEmpty()) {
                    // Show empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty())
                                    "No requests found matching '$searchQuery'"
                                else
                                    "No active requests found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Create a new request by clicking the + button",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    // Show list of requests with improved spacing
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp) // Increased spacing between cards
                    ) {
                        items(filteredRequests) { request ->
                            RequestCard(
                                request = request,
                                onClick = { onRequestClick(request.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card component for displaying a fuel request
 */
@Composable
fun RequestCard(
    request: FuelRequest,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(request.requestDate))

    val statusColor = when (request.status) {
        RequestStatus.PENDING -> Color(0xFFFFA000) // Amber
        RequestStatus.APPROVED -> Color(0xFF4CAF50) // Green
        RequestStatus.DECLINED -> Color(0xFFF44336) // Red
        RequestStatus.DISPENSED -> Color(0xFF2196F3) // Blue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp // Reduced elevation for a more subtle shadow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with request ID and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Request ID
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Improved profile picture placeholder with gradient
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF004D40), Color(0xFF00897B))
                                )
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${request.id.takeLast(2).uppercase()}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Request ${request.id.takeLast(8)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004D40)
                        )

                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF004D40).copy(alpha = 0.7f)
                        )
                    }
                }

                // Status indicator with improved styling
                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.15f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = request.status.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Request details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Amount
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF004D40).copy(alpha = 0.7f)
                    )

                    Text(
                        text = "${request.requestedAmount} L",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                }

                // Trip details preview
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = "Trip Details",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF004D40).copy(alpha = 0.7f)
                    )

                    Text(
                        text = request.tripDetails,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF004D40),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
