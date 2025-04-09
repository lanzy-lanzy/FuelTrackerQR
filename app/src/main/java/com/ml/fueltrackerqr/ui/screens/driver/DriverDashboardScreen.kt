package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.ui.screens.driver.components.DashboardTopBar
import com.ml.fueltrackerqr.ui.screens.driver.components.QuickActionsCard
import com.ml.fueltrackerqr.ui.screens.driver.components.RequestsList
import com.ml.fueltrackerqr.ui.screens.driver.components.SectionHeader
import com.ml.fueltrackerqr.ui.screens.driver.components.StatsCard
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.RequestState

/**
 * Dashboard screen for drivers using a component-based architecture
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

    // Enhanced Scaffold with animated elements
    Scaffold(
        topBar = {
            DashboardTopBar(
                currentUser = currentUser,
                isRefreshing = isRefreshing,
                onRefreshClick = { refreshData() },
                onLogoutClick = onLogoutClick
            )
        },
        // Bottom navigation is handled by DriverMainScreen
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Make scaffold transparent to show gradient
    ) { padding ->
        // We don't need SplashGradientBackground here since it's already in DriverMainScreen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
                ) {
                    // Add some space at the top to avoid crowding
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick actions card
                    QuickActionsCard(
                        onNewRequestClick = onNewRequestClick,
                        onHistoryClick = { /* Navigate to history */ },
                        onProfileClick = { /* Navigate to profile */ }
                    )

                    Spacer(modifier = Modifier.height(16.dp)) // Reduced spacing

                    // Stats card
                    StatsCard(requests = driverRequests)

                    Spacer(modifier = Modifier.height(16.dp)) // Reduced spacing

                    // Section header for requests
                    SectionHeader(
                        title = "Your Fuel Requests",
                        icon = Icons.Default.Favorite
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Requests list or empty/loading state
                    RequestsList(
                        requests = driverRequests,
                        requestState = requestState,
                        isRefreshing = isRefreshing,
                        onNewRequestClick = onNewRequestClick
                    )

                    // Add space at the bottom to avoid overlap with navigation
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
