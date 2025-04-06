package com.ml.fueltrackerqr.ui.screens.gasstation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
// import androidx.compose.material.icons.filled.CalendarMonth
// import androidx.compose.material.icons.filled.DirectionsCar
// import androidx.compose.material.icons.filled.FilterList
// import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.data.model.FuelRequest
import com.ml.fueltrackerqr.data.model.RequestStatus
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBox
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.GradientDivider
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.BackgroundMedium
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen for displaying dispensed fuel history
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DispensedFuelScreen(
    onBackClick: () -> Unit
) {
    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    var isLoading by remember { mutableStateOf(true) }
    var dispensedRequests by remember { mutableStateOf<List<FuelRequest>>(emptyList()) }

    // In a real app, this would fetch the dispensed requests from a ViewModel
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
                status = RequestStatus.DISPENSED,
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                approvedBy = "Admin User",
                approvedAt = System.currentTimeMillis() - 172800000, // 2 days ago
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = System.currentTimeMillis() - 43200000, // 12 hours ago
                dispensedBy = "Gas Station Attendant"
            ),
            FuelRequest(
                id = "2",
                userId = "user2",
                driverName = "Jane Smith",
                vehicleInfo = "Honda Civic - XYZ789",
                fuelAmount = 15.0,
                destination = "Airport",
                purpose = "Pick up client",
                status = RequestStatus.DISPENSED,
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                approvedBy = "Admin User",
                approvedAt = System.currentTimeMillis() - 172800000, // 2 days ago
                declinedBy = null,
                declinedAt = null,
                declineReason = null,
                dispensedAt = System.currentTimeMillis() - 86400000, // 1 day ago
                dispensedBy = "Gas Station Attendant"
            ),
            FuelRequest(
                id = "3",
                userId = "user3",
                driverName = "Bob Johnson",
                vehicleInfo = "Ford F-150 - DEF456",
                fuelAmount = 30.0,
                destination = "Construction Site",
                purpose = "Transport materials",
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
        dispensedRequests = sampleRequests
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dispensed Fuel History", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = { /* TODO: Implement filtering */ }) {
                        Icon(
                            imageVector = Icons.Default.Search, // Using Search instead of FilterList
                            contentDescription = "Filter",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) }
    ) { padding ->
        GradientBackground(
            brush = GradientBrushes.backgroundGradient,
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    // Loading indicator
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                } else if (dispensedRequests.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person, // Using Person instead of LocalGasStation
                            contentDescription = null,
                            tint = TextPrimary.copy(alpha = 0.5f),
                            modifier = Modifier.size(80.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No Dispensed Fuel Records",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Dispensed fuel records will appear here",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Dispensed fuel list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Summary card
                        item {
                            GradientBox(
                                brush = GradientBrushes.greenBlueGradient,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Total Fuel Dispensed",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "${dispensedRequests.sumOf { it.fuelAmount }} Liters",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TextPrimary,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = "Across ${dispensedRequests.size} Transactions",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextPrimary.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Dispensed fuel records
                        items(dispensedRequests) { request ->
                            DispensedFuelCard(request = request)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card displaying a dispensed fuel record
 */
@Composable
fun DispensedFuelCard(
    request: FuelRequest
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundMedium
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction #${request.id.takeLast(5)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextPrimary
                )

                Text(
                    text = "${request.fuelAmount} Liters",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GradientDivider(
                brush = GradientBrushes.greenBlueGradient,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Details
            DetailRow(
                icon = Icons.Default.Person,
                label = "Driver",
                value = request.driverName
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(
                icon = Icons.Default.Person, // Using Person instead of DirectionsCar
                label = "Vehicle",
                value = request.vehicleInfo
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(
                icon = Icons.Default.Person, // Using Person instead of CalendarMonth
                label = "Dispensed At",
                value = request.dispensedAt?.let { formatDate(it) } ?: "Unknown"
            )

            Spacer(modifier = Modifier.height(8.dp))

            DetailRow(
                icon = Icons.Default.Person,
                label = "Dispensed By",
                value = request.dispensedBy ?: "Unknown"
            )
        }
    }
}

/**
 * A row displaying a detail with an icon, label, and value
 */
@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )
        }
    }
}

/**
 * Format a timestamp into a readable date
 */
private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}
