package com.ml.fueltrackerqr.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.ui.screens.admin.AdminDashboardScreen
import com.ml.fueltrackerqr.ui.screens.admin.RequestDetailScreen
import com.ml.fueltrackerqr.ui.screens.auth.LoginScreen
import com.ml.fueltrackerqr.ui.screens.auth.RegisterScreen
import com.ml.fueltrackerqr.ui.screens.driver.DriverDashboardScreen
import com.ml.fueltrackerqr.ui.screens.driver.NewRequestScreen
import com.ml.fueltrackerqr.ui.screens.gasstation.GasStationDashboardScreen
import com.ml.fueltrackerqr.ui.screens.gasstation.ScanQRCodeScreen
import com.ml.fueltrackerqr.viewmodel.AdminViewModel
import com.ml.fueltrackerqr.viewmodel.AuthState
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.GasStationViewModel

/**
 * Enum representing the different screens in the application
 */
enum class AppScreen {
    Login,
    Register,
    DriverDashboard,
    NewRequest,
    AdminDashboard,
    RequestDetail,
    GasStationDashboard,
    ScanQRCode,
    // New screens
    PendingRequests,
    ApprovedRequests,
    GenerateQR,
    RequestHistory
}

/**
 * Main navigation component for the application
 *
 * @param navController NavHostController for navigation
 * @param startDestination Starting destination for navigation
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = AppScreen.Login.name
) {
    val authViewModel: AuthViewModel = viewModel()
    val driverViewModel: DriverViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val gasStationViewModel: GasStationViewModel = viewModel()

    // Don't collect auth state here to avoid potential IPC issues
    // We'll handle auth in each screen instead

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication screens
        composable(AppScreen.Login.name) {
            LoginScreen(
                onLoginSuccess = { user ->
                    when (user.role) {
                        UserRole.DRIVER -> navController.navigate(AppScreen.DriverDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                        UserRole.ADMIN -> navController.navigate(AppScreen.AdminDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                        UserRole.GAS_STATION -> navController.navigate(AppScreen.GasStationDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(AppScreen.Register.name)
                },
                authViewModel = authViewModel
            )
        }

        composable(AppScreen.Register.name) {
            RegisterScreen(
                onRegisterSuccess = { user ->
                    when (user.role) {
                        UserRole.DRIVER -> navController.navigate(AppScreen.DriverDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                        UserRole.ADMIN -> navController.navigate(AppScreen.AdminDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                        UserRole.GAS_STATION -> navController.navigate(AppScreen.GasStationDashboard.name) {
                            popUpTo(AppScreen.Login.name) { inclusive = true }
                        }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                authViewModel = authViewModel
            )
        }

        // Driver screens
        composable(AppScreen.DriverDashboard.name) {
            DriverDashboardScreen(
                onNewRequestClick = {
                    navController.navigate(AppScreen.NewRequest.name)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppScreen.Login.name) {
                        popUpTo(AppScreen.DriverDashboard.name) { inclusive = true }
                    }
                },
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }

        composable(AppScreen.NewRequest.name) {
            NewRequestScreen(
                onRequestSubmitted = {
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                },
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }

        // Admin screens
        composable(AppScreen.AdminDashboard.name) {
            AdminDashboardScreen(
                onPendingRequestsClick = {
                    // For testing purposes, navigate to RequestDetail
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                onApprovedRequestsClick = {
                    // For testing purposes, navigate to RequestDetail
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                onGenerateQRClick = {
                    // For testing purposes, navigate to RequestDetail
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                onHistoryClick = {
                    // For testing purposes, navigate to RequestDetail
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(AppScreen.Login.name) {
                        popUpTo(AppScreen.AdminDashboard.name) { inclusive = true }
                    }
                },
                user = authViewModel.currentUser.value ?: com.ml.fueltrackerqr.model.User(name = "Admin")
            )
        }

        composable(AppScreen.RequestDetail.name) {
            RequestDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                adminViewModel = adminViewModel,
                authViewModel = authViewModel
            )
        }

        // Gas Station screens
        composable(AppScreen.GasStationDashboard.name) {
            GasStationDashboardScreen(
                onScanClick = {
                    navController.navigate(AppScreen.ScanQRCode.name)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    navController.navigate(AppScreen.Login.name) {
                        popUpTo(AppScreen.GasStationDashboard.name) { inclusive = true }
                    }
                },
                gasStationViewModel = gasStationViewModel,
                authViewModel = authViewModel
            )
        }

        composable(AppScreen.ScanQRCode.name) {
            ScanQRCodeScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onScanComplete = {
                    navController.popBackStack()
                },
                gasStationViewModel = gasStationViewModel
            )
        }
    }

    // Auth state handling has been moved to individual screens
    // to avoid potential IPC issues
}
