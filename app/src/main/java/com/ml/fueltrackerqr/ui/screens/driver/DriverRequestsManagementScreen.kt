package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.ml.fueltrackerqr.ui.navigation.DriverRequestNavigation
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel

/**
 * Container screen for driver request management functionality
 */
@Composable
fun DriverRequestsManagementScreen(
    onBackToDriverDashboard: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    
    DriverRequestNavigation(
        navController = navController,
        onBackToDriverDashboard = onBackToDriverDashboard,
        driverViewModel = driverViewModel,
        authViewModel = authViewModel
    )
}
