package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ml.fueltrackerqr.ui.navigation.DriverRequestNavigation
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel

/**
 * Main screen for driver with bottom navigation
 */
@Composable
fun DriverMainScreen(
    onLogoutClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: DriverScreens.Dashboard.route

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        bottomBar = {
            DriverBottomNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DriverScreens.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Dashboard screen
            composable(DriverScreens.Dashboard.route) {
                DriverDashboardScreen(
                    onNewRequestClick = {
                        navController.navigate(DriverScreens.NewRequest.route)
                    },
                    onLogoutClick = onLogoutClick,
                    driverViewModel = driverViewModel,
                    authViewModel = authViewModel
                )
            }

            // Requests screen
            composable(DriverScreens.Requests.route) {
                DriverRequestsManagementScreen(
                    onBackToDriverDashboard = {
                        navController.navigate(DriverScreens.Dashboard.route) {
                            popUpTo(DriverScreens.Dashboard.route) { inclusive = true }
                        }
                    },
                    driverViewModel = driverViewModel,
                    authViewModel = authViewModel
                )
            }

            // New Request screen
            composable(DriverScreens.NewRequest.route) {
                NewRequestScreen(
                    onRequestSubmitted = {
                        // Navigate to requests screen after submission and refresh the requests
                        navController.navigate(DriverScreens.Requests.route) {
                            popUpTo(DriverScreens.NewRequest.route) { inclusive = true }
                        }
                        // Force refresh of requests when returning to the requests screen
                        currentUser?.let { user ->
                            driverViewModel.loadDriverRequests(user.id)
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    driverViewModel = driverViewModel,
                    authViewModel = authViewModel
                )
            }

            // Profile screen
            composable(DriverScreens.Profile.route) {
                DriverProfileScreen(
                    onBackClick = {
                        navController.navigate(DriverScreens.Dashboard.route) {
                            popUpTo(DriverScreens.Dashboard.route) { inclusive = true }
                        }
                    },
                    onEditProfileClick = {
                        // In a real app, this would navigate to a profile edit screen
                        // For now, we'll just show a snackbar or similar notification
                    },
                    driverViewModel = driverViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

/**
 * Bottom navigation for driver screens
 */
@Composable
fun DriverBottomNavigation(
    navController: NavHostController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color(0xFF004D40),
        contentColor = Color.White,
        tonalElevation = 8.dp
    ) {
        DriverScreens.values().forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(DriverScreens.Dashboard.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFF57C73),
                    selectedTextColor = Color(0xFFF57C73),
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

/**
 * Enum for driver screens with routes, titles and icons
 */
enum class DriverScreens(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Dashboard("driver_dashboard", "Dashboard", Icons.Default.Home),
    Requests("driver_requests", "Requests", Icons.AutoMirrored.Filled.List),
    NewRequest("driver_new_request", "New Request", Icons.Default.Add),
    Profile("driver_profile", "Profile", Icons.Default.AccountCircle)
}
