package com.ml.fueltrackerqr.ui.screens.driver.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.viewmodel.RequestState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Component to display the list of requests or appropriate empty/loading states
 */
@Composable
fun RequestsList(
    requests: List<FuelRequest>,
    requestState: RequestState,
    isRefreshing: Boolean,
    onNewRequestClick: () -> Unit
) {
    if (requestState is RequestState.Loading && requests.isEmpty()) {
        // Loading state
        LoadingState()
    } else if (requests.isEmpty()) {
        // Empty state
        EmptyState(onNewRequestClick = onNewRequestClick)
    } else {
        // List of requests
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(requests) { request ->
                    RequestCard(request = request)
                }
            }
        }
    }
}

/**
 * Loading state component
 */
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated loading indicator
            CircularProgressIndicator(
                color = Color(0xFF00897B),
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading text
            Text(
                text = "Loading your requests...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF00897B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Empty state component
 */
@Composable
private fun EmptyState(onNewRequestClick: () -> Unit) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F2937).copy(alpha = 0.85f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Empty state icon with enhanced styling
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF57C73).copy(alpha = 0.8f),
                                    Color(0xFF00897B).copy(alpha = 0.8f)
                                )
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "No Fuel Requests",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "No Fuel Requests Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Create a new request by clicking the button below.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add a hint button that points to the FAB
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF57C73).copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFFF57C73),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "New Request",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF57C73),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Unified request card component with consistent styling
 */
@Composable
private fun RequestCard(request: FuelRequest) {
    // Single unified card for the entire request
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00695C) // Consistent with app's teal theme
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Amount and date section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F2937) // Dark background for contrast
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Fuel amount with clean styling
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Amount: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Text(
                            text = "${request.requestedAmount} liters",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date with clean styling
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Date: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Text(
                            text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(request.requestDate)),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            // Request ID and status section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Request ${request.id.takeLast(6)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                // Simple status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = when (request.status) {
                                RequestStatus.APPROVED -> Color(0xFF1B5E20)
                                RequestStatus.PENDING -> Color(0xFF3E2723)
                                RequestStatus.DISPENSED -> Color(0xFF0D47A1)
                                else -> Color.Gray
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = request.status.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
