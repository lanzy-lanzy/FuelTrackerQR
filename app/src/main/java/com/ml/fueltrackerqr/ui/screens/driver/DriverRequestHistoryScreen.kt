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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * Screen for displaying a driver's fuel request history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRequestHistoryScreen(
    onBackClick: () -> Unit,
    onRequestClick: (String) -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val driverRequests by driverViewModel.driverRequests.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var searchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Filter state
    var selectedStatus by remember { mutableStateOf<RequestStatus?>(null) }

    // Filter requests based on search query and selected status
    val filteredRequests = remember(driverRequests, searchQuery, selectedStatus) {
        var filtered = driverRequests

        // Apply status filter
        if (selectedStatus != null) {
            filtered = filtered.filter { it.status == selectedStatus }
        }

        // Apply search filter
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter { request ->
                request.id.contains(searchQuery, ignoreCase = true) ||
                request.tripDetails.contains(searchQuery, ignoreCase = true) ||
                request.status.name.contains(searchQuery, ignoreCase = true)
            }
        }

        // Sort by date (newest first)
        filtered.sortedByDescending { it.requestDate }
    }

    // Load driver requests when the screen is first displayed
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            driverViewModel.loadDriverRequests(user.id)
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Request History",
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
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = { searchQuery = it },
                    active = searchActive,
                    onActiveChange = { searchActive = it },
                    placeholder = { Text("Search history...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Search suggestions could go here
                }

                // Filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All filter
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("All") },
                        leadingIcon = {
                            if (selectedStatus == null) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00897B),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    // Pending filter
                    FilterChip(
                        selected = selectedStatus == RequestStatus.PENDING,
                        onClick = { selectedStatus = RequestStatus.PENDING },
                        label = { Text("Pending") },
                        leadingIcon = {
                            if (selectedStatus == RequestStatus.PENDING) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFA000),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    // Approved filter
                    FilterChip(
                        selected = selectedStatus == RequestStatus.APPROVED,
                        onClick = { selectedStatus = RequestStatus.APPROVED },
                        label = { Text("Approved") },
                        leadingIcon = {
                            if (selectedStatus == RequestStatus.APPROVED) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4CAF50),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )

                    // Dispensed filter
                    FilterChip(
                        selected = selectedStatus == RequestStatus.DISPENSED,
                        onClick = { selectedStatus = RequestStatus.DISPENSED },
                        label = { Text("Dispensed") },
                        leadingIcon = {
                            if (selectedStatus == RequestStatus.DISPENSED) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2196F3),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isLoading) {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00897B) // Medium teal
                        )
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
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(64.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedStatus != null)
                                    "No matching requests found"
                                else
                                    "No request history found",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty() || selectedStatus != null)
                                    "Try changing your search or filters"
                                else
                                    "Your completed and past requests will appear here",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Show history list with month headers
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                        val groupedRequests = filteredRequests.groupBy { request ->
                            dateFormat.format(Date(request.requestDate))
                        }

                        groupedRequests.forEach { (month, requests) ->
                            item {
                                // Month header
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = month,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    HorizontalDivider(
                                        color = Color.White.copy(alpha = 0.3f),
                                        thickness = 1.dp
                                    )
                                }
                            }

                            items(requests) { request ->
                                HistoryRequestCard(
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
}

/**
 * Card component for displaying a request in history
 */
@Composable
private fun HistoryRequestCard(
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.9f))
            .padding(1.dp) // Border effect
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Request details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Request ${request.id.takeLast(8)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40)
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF004D40).copy(alpha = 0.7f)
                )
            }

            // Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${request.requestedAmount} L",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF004D40)
                )

                Text(
                    text = request.status.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }
        }
    }
}
