package com.ml.fueltrackerqr.ui.screens.admin

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.screens.driver.RequestStatusChip
import com.ml.fueltrackerqr.viewmodel.AdminViewModel
import com.ml.fueltrackerqr.viewmodel.LoadingState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for displaying and managing fuel requests for admins
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRequestsScreen(
    onBackClick: () -> Unit,
    onRequestClick: (FuelRequest) -> Unit,
    adminViewModel: AdminViewModel,
    requestType: RequestType = RequestType.PENDING
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Get requests based on type
    val requests by when (requestType) {
        RequestType.PENDING -> adminViewModel.pendingRequests.collectAsState()
        RequestType.APPROVED -> adminViewModel.approvedRequests.collectAsState()
        RequestType.ALL -> adminViewModel.allRequests.collectAsState()
    }

    val loadingState by adminViewModel.loadingState.collectAsState()

    // Load requests when the screen is first displayed
    LaunchedEffect(requestType) {
        when (requestType) {
            RequestType.PENDING -> adminViewModel.loadPendingRequests()
            RequestType.APPROVED -> adminViewModel.loadApprovedRequests()
            RequestType.ALL -> adminViewModel.loadAllRequests()
        }
    }

    // Handle loading state changes
    LaunchedEffect(loadingState) {
        when (loadingState) {
            is LoadingState.Loading -> {
                isLoading = true
            }
            is LoadingState.Success -> {
                isLoading = false
            }
            is LoadingState.Error -> {
                isLoading = false
                snackbarHostState.showSnackbar((loadingState as LoadingState.Error).message)
            }
            else -> {
                isLoading = false
            }
        }
    }

    // Filter requests based on search query
    val filteredRequests = remember(requests, searchQuery) {
        if (searchQuery.isBlank()) {
            requests
        } else {
            requests.filter { request ->
                request.id.contains(searchQuery, ignoreCase = true) ||
                request.driverName.contains(searchQuery, ignoreCase = true) ||
                request.vehicleId.contains(searchQuery, ignoreCase = true) ||
                request.tripDetails.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (requestType) {
                            RequestType.PENDING -> "Pending Requests"
                            RequestType.APPROVED -> "Approved Requests"
                            RequestType.ALL -> "All Requests"
                        }
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
                    IconButton(onClick = {
                        when (requestType) {
                            RequestType.PENDING -> adminViewModel.loadPendingRequests()
                            RequestType.APPROVED -> adminViewModel.loadApprovedRequests()
                            RequestType.ALL -> adminViewModel.loadAllRequests()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }

                    // We'll add filtering later
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Search bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = { Text("Search requests...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

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
                        Text(
                            text = if (searchQuery.isNotEmpty())
                                "No requests found matching '$searchQuery'"
                            else
                                "No ${requestType.name.lowercase()} requests found",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    // Show request list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {
                        items(filteredRequests) { request ->
                            RequestCard(
                                request = request,
                                onClick = { onRequestClick(request) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a fuel request summary
 */
@Composable
fun RequestCard(
    request: FuelRequest,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Driver: ${request.driverName}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Vehicle: ${request.vehicleId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${request.requestedAmount} liters",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = dateFormat.format(Date(request.requestDate)),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Trip: ${request.tripDetails}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Enum representing the type of requests to display
 */
enum class RequestType {
    PENDING,
    APPROVED,
    ALL
}
