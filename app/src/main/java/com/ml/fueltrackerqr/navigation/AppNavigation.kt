package com.ml.fueltrackerqr.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.ui.screens.admin.AdminDashboardScreen
import com.ml.fueltrackerqr.ui.screens.admin.AdminRequestsScreen
import com.ml.fueltrackerqr.ui.screens.admin.RequestDetailScreen
import com.ml.fueltrackerqr.ui.screens.admin.RequestType
import com.ml.fueltrackerqr.ui.screens.admin.VehicleManagementScreen
import com.ml.fueltrackerqr.ui.screens.auth.LoginScreen
import com.ml.fueltrackerqr.ui.screens.auth.RegisterScreen
import com.ml.fueltrackerqr.ui.screens.driver.DriverMainScreen
import com.ml.fueltrackerqr.ui.screens.gasstation.GasStationDashboardScreen
import com.ml.fueltrackerqr.ui.screens.gasstation.ScanQRCodeScreen
import com.ml.fueltrackerqr.viewmodel.AdminViewModel
import com.ml.fueltrackerqr.viewmodel.AuthState
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.viewmodel.GasStationViewModel
import com.ml.fueltrackerqr.ErrorScreen

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
    RequestHistory,
    VehicleManagement
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
    val TAG = "AppNavigation"
    Log.d(TAG, "Initializing AppNavigation with startDestination: $startDestination")

    // Create ViewModels
    Log.d(TAG, "Creating ViewModels")
    val authViewModel: AuthViewModel = viewModel()
    val driverViewModel: DriverViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()
    val gasStationViewModel: GasStationViewModel = viewModel()
    Log.d(TAG, "ViewModels created successfully")

    // Don't collect auth state here to avoid potential IPC issues
    // We'll handle auth in each screen instead

    Log.d(TAG, "Setting up NavHost with startDestination: $startDestination")
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Authentication screens
        Log.d(TAG, "Setting up Login screen composable")
        composable(AppScreen.Login.name) {
            Log.d(TAG, "Composing LoginScreen")
            LoginScreen(
                onLoginSuccess = { user ->
                    Log.d(TAG, "Login successful for user role: ${user.role}")
                    when (user.role) {
                        UserRole.DRIVER -> {
                            Log.d(TAG, "Navigating to DriverDashboard")
                            navController.navigate(AppScreen.DriverDashboard.name) {
                                popUpTo(AppScreen.Login.name) { inclusive = true }
                            }
                        }
                        UserRole.ADMIN -> {
                            Log.d(TAG, "Navigating to AdminDashboard")
                            navController.navigate(AppScreen.AdminDashboard.name) {
                                popUpTo(AppScreen.Login.name) { inclusive = true }
                            }
                        }
                        UserRole.GAS_STATION -> {
                            Log.d(TAG, "Navigating to GasStationDashboard")
                            navController.navigate(AppScreen.GasStationDashboard.name) {
                                popUpTo(AppScreen.Login.name) { inclusive = true }
                            }
                        }
                    }
                },
                onRegisterClick = {
                    Log.d(TAG, "Register button clicked, navigating to Register screen")
                    navController.navigate(AppScreen.Register.name)
                },
                authViewModel = authViewModel
            )
            Log.d(TAG, "LoginScreen composed successfully")
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
            DriverMainScreen(
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

        // Admin screens
        composable(AppScreen.AdminDashboard.name) {
            AdminDashboardScreen(
                onPendingRequestsClick = {
                    navController.navigate(AppScreen.PendingRequests.name)
                },
                onApprovedRequestsClick = {
                    navController.navigate(AppScreen.ApprovedRequests.name)
                },
                onGenerateQRClick = {
                    // For testing purposes, navigate to RequestDetail
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                onHistoryClick = {
                    navController.navigate(AppScreen.RequestHistory.name)
                },
                onVehicleManagementClick = {
                    navController.navigate(AppScreen.VehicleManagement.name)
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

        composable(AppScreen.PendingRequests.name) {
            AdminRequestsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRequestClick = { request ->
                    adminViewModel.selectRequest(request)
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                adminViewModel = adminViewModel,
                requestType = RequestType.PENDING
            )
        }

        composable(AppScreen.ApprovedRequests.name) {
            AdminRequestsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRequestClick = { request ->
                    adminViewModel.selectRequest(request)
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                adminViewModel = adminViewModel,
                requestType = RequestType.APPROVED
            )
        }

        composable(AppScreen.RequestHistory.name) {
            AdminRequestsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRequestClick = { request ->
                    adminViewModel.selectRequest(request)
                    navController.navigate(AppScreen.RequestDetail.name)
                },
                adminViewModel = adminViewModel,
                requestType = RequestType.ALL
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

        // Vehicle Management screen
        composable(AppScreen.VehicleManagement.name) {
            VehicleManagementScreen(
                onBackToAdminDashboard = {
                    navController.popBackStack()
                }
            )
        }
    }
}
