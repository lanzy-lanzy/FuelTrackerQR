package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
// import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.RequestCard
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.DarkTeal
import com.ml.fueltrackerqr.ui.theme.MediumTeal
import com.ml.fueltrackerqr.ui.theme.LightCoral
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * Screen for displaying a driver's fuel requests
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverRequestsScreen(
    onBackClick: () -> Unit,
    onCreateRequestClick: () -> Unit,
    onRequestClick: (FuelRequest) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(true) }
    var requests by remember { mutableStateOf<List<FuelRequest>>(emptyList()) }

    // In a real app, this would fetch the requests from a ViewModel
    // For now, we'll just use some sample data
    val sampleRequests = remember {
        listOf(
            FuelRequest(
                id = "1",
                userId = "user1",
                driverName = "John Doe",
                vehicleInfo = "Toyota Corolla - ABC123",
                fuelAmount = 20.0,
                destination = "City Center",
                purpose = "Business meeting",
                status = RequestStatus.PENDING,
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                approvedBy = null,
                approvedAt = null,
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = null,
                dispensedBy = null
            ),
            FuelRequest(
                id = "2",
                userId = "user1",
                driverName = "John Doe",
                vehicleInfo = "Toyota Corolla - ABC123",
                fuelAmount = 15.0,
                destination = "Airport",
                purpose = "Pick up client",
                status = RequestStatus.APPROVED,
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                approvedBy = "Admin User",
                approvedAt = System.currentTimeMillis() - 86400000, // 1 day ago
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = null,
                dispensedBy = null
            ),
            FuelRequest(
                id = "3",
                userId = "user1",
                driverName = "John Doe",
                vehicleInfo = "Toyota Corolla - ABC123",
                fuelAmount = 10.0,
                destination = "Shopping Mall",
                purpose = "Delivery",
                status = RequestStatus.DECLINED,
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                approvedBy = null,
                approvedAt = null,
                declinedBy = "Admin User",
                declinedAt = System.currentTimeMillis() - 172800000, // 2 days ago
                declineReason = "Insufficient information",
                dispensedAt = null,
                dispensedBy = null
            ),
            FuelRequest(
                id = "4",
                userId = "user1",
                driverName = "John Doe",
                vehicleInfo = "Toyota Corolla - ABC123",
                fuelAmount = 25.0,
                destination = "Conference Center",
                purpose = "Team meeting",
                status = RequestStatus.DISPENSED,
                timestamp = System.currentTimeMillis() - 345600000, // 4 days ago
                approvedBy = "Admin User",
                approvedAt = System.currentTimeMillis() - 259200000, // 3 days ago
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = System.currentTimeMillis() - 172800000, // 2 days ago
                dispensedBy = "Gas Station Attendant"
            )
        )
    }

    // Simulate loading
    androidx.compose.runtime.LaunchedEffect(Unit) {
        // In a real app, this would be a call to a ViewModel method
        kotlinx.coroutines.delay(1000)
        requests = sampleRequests
        isLoading = false
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
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* TODO: Implement filtering */ }) {
                        Icon(
                            imageVector = Icons.Default.List, // Using List icon for filtering
                            contentDescription = "Filter",
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
            FloatingActionButton(
                onClick = onCreateRequestClick,
                containerColor = Color(0xFFF57C73),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Request",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    // Enhanced loading indicator with background
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1F2937).copy(alpha = 0.7f))
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFF57C73),
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                } else if (requests.isEmpty()) {
                    // Enhanced empty state with card and icon
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(8.dp, RoundedCornerShape(24.dp)),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1F2937).copy(alpha = 0.8f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Empty state icon with gradient background
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(DarkTeal, LightCoral)
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Request",
                                        tint = Color.White,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                    text = "No Requests Found",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Tap the + button to create a new fuel request",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    // Enhanced request list with padding
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Section title with accent
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
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

                        // Request list
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(requests) { request ->
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
}
