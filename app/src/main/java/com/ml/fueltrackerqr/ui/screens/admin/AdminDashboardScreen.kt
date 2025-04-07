package com.ml.fueltrackerqr.ui.screens.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.ui.components.AdminDashboardCards
import com.ml.fueltrackerqr.ui.components.DashboardSectionHeader
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.components.StatsRow
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary

/**
 * Dashboard screen for admin users
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    user: User,
    onLogout: () -> Unit,
    onPendingRequestsClick: () -> Unit,
    onApprovedRequestsClick: () -> Unit,
    onGenerateQRClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onVehicleManagementClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var pendingCount by remember { mutableStateOf(0) }
    var approvedCount by remember { mutableStateOf(0) }
    var dispensedCount by remember { mutableStateOf(0) }

    // In a real app, this would fetch the counts from a ViewModel
    // For now, we'll just use some sample data
    LaunchedEffect(Unit) {
        // Simulate loading
        kotlinx.coroutines.delay(1000)
        pendingCount = 5
        approvedCount = 12
        dispensedCount = 28
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Admin Dashboard",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Welcome, ${user.name}",
                            color = TextPrimary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 16.dp)
                    ) {
                        // Stats section
                        DashboardSectionHeader(title = "Overview")

                        Spacer(modifier = Modifier.height(8.dp))

                        StatsRow(
                            pendingCount = pendingCount,
                            approvedCount = approvedCount,
                            dispensedCount = dispensedCount
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Actions section
                        DashboardSectionHeader(title = "Actions")

                        Spacer(modifier = Modifier.height(8.dp))

                        AdminDashboardCards(
                            onPendingRequestsClick = onPendingRequestsClick,
                            onApprovedRequestsClick = onApprovedRequestsClick,
                            onGenerateQRClick = onGenerateQRClick,
                            onHistoryClick = onHistoryClick,
                            onVehicleManagementClick = onVehicleManagementClick
                        )
                    }
                }
            }
        }
    }
}
