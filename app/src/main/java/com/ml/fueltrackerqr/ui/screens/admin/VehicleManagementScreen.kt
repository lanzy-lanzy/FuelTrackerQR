package com.ml.fueltrackerqr.ui.screens.admin

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ml.fueltrackerqr.ui.navigation.VehicleManagementNavigation

/**
 * Container screen for vehicle management functionality
 */
@Composable
fun VehicleManagementScreen(
    onBackToAdminDashboard: () -> Unit
) {
    val navController = rememberNavController()
    
    VehicleManagementNavigation(
        navController = navController,
        onBackToAdminDashboard = onBackToAdminDashboard
    )
}
