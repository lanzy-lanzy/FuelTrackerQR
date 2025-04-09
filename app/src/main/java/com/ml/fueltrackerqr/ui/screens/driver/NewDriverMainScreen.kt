package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.imePadding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel
import com.ml.fueltrackerqr.ui.screens.driver.NewRequestScreen
import com.ml.fueltrackerqr.ui.screens.driver.DriverRequestDetailScreen

/**
 * Main screen for driver with enhanced bottom navigation and visual styling
 * This is a completely new implementation to fix UI issues
 */
@Composable
fun NewDriverMainScreen(
    onLogoutClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route ?: DriverScreens.Dashboard.route

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()

    // Create a full-screen container with gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF004D40), // Dark teal
                        Color(0xFF00897B), // Medium teal
                        Color(0xFF00897B)  // Medium teal (no alpha to avoid any transparency issues)
                    )
                )
            )
    ) {
        // Place the Scaffold on top of the gradient background
        Scaffold(
            containerColor = Color.Transparent, // Make scaffold transparent to show gradient
            contentWindowInsets = WindowInsets(0), // No insets to handle them manually
            modifier = Modifier.systemBarsPadding().imePadding(),
            bottomBar = {
                // Animated visibility for bottom navigation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    // Custom bottom navigation with clean styling
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(28.dp)
                            )
                            .clip(RoundedCornerShape(28.dp))
                            .navigationBarsPadding(), // Add padding for navigation bar
                        color = Color(0xFF00695C) // Consistent teal color
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent, // Make transparent to show background
                            contentColor = Color.White,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp) // Fixed height to ensure proper display
                        ) {
                            NewDriverScreens.values().forEach { screen ->
                                val selected = currentRoute == screen.route

                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (selected) Color.White else Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(if (selected) 28.dp else 24.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    selected = selected,
                                    onClick = {
                                        if (currentRoute != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(NewDriverScreens.Dashboard.route) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color.White,
                                        selectedTextColor = Color.White,
                                        unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                        unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                        indicatorColor = Color(0xFF004D40).copy(alpha = 0.3f) // Darker teal for indicator with transparency
                                    )
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            // NavHost for navigation between driver screens
            NavHost(
                navController = navController,
                startDestination = NewDriverScreens.Dashboard.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                // Dashboard screen
                composable(NewDriverScreens.Dashboard.route) {
                    NewDriverDashboardScreen(
                        onNewRequestClick = {
                            navController.navigate(NewDriverScreens.NewRequest.route)
                        },
                        onViewHistoryClick = {
                            navController.navigate(NewDriverScreens.Requests.route)
                        },
                        onProfileClick = {
                            navController.navigate(NewDriverScreens.Profile.route)
                        },
                        onRequestClick = { requestId ->
                            navController.navigate(NewDriverScreens.requestDetailRoute(requestId))
                        },
                        onLogoutClick = onLogoutClick,
                        driverViewModel = driverViewModel,
                        authViewModel = authViewModel
                    )
                }

                // Requests screen
                composable(NewDriverScreens.Requests.route) {
                    DriverRequestsScreen(
                        onBackClick = {
                            navController.navigateUp()
                        },
                        onNewRequestClick = {
                            navController.navigate(NewDriverScreens.NewRequest.route)
                        },
                        onRequestClick = { requestId ->
                            navController.navigate(NewDriverScreens.requestDetailRoute(requestId))
                        },
                        onHistoryClick = {
                            // In a real app, navigate to history view
                        },
                        driverViewModel = driverViewModel,
                        authViewModel = authViewModel
                    )
                }

                // New Request screen
                composable(NewDriverScreens.NewRequest.route) {
                    // Get current user for refreshing requests after submission
                    val currentUser = authViewModel.currentUser.collectAsState().value

                    // Clear request state when entering the screen
                    LaunchedEffect(Unit) {
                        driverViewModel.clearRequestState()
                    }

                    NewRequestScreen(
                        onRequestSubmitted = {
                            // Navigate back to dashboard and refresh requests
                            navController.navigate(NewDriverScreens.Dashboard.route) {
                                popUpTo(NewDriverScreens.Dashboard.route) { inclusive = true }
                            }
                            // Force refresh of requests when returning to dashboard
                            currentUser?.let { user ->
                                driverViewModel.loadDriverRequests(user.id)
                            }
                        },
                        onBackClick = {
                            navController.navigateUp()
                        },
                        driverViewModel = driverViewModel,
                        authViewModel = authViewModel
                    )
                }

                // Request Detail screen
                composable(
                    route = NewDriverScreens.RequestDetail.route,
                    arguments = listOf(
                        navArgument("requestId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
                    DriverRequestDetailScreen(
                        requestId = requestId,
                        onBackClick = {
                            navController.navigateUp()
                        },
                        onCancelRequest = {
                            navController.navigate(NewDriverScreens.Dashboard.route) {
                                popUpTo(NewDriverScreens.Dashboard.route) { inclusive = true }
                            }
                        },
                        driverViewModel = driverViewModel,
                        authViewModel = authViewModel
                    )
                }

                // Profile screen
                composable(NewDriverScreens.Profile.route) {
                    DriverProfileScreen(
                        onBackClick = {
                            navController.navigateUp()
                        },
                        onEditProfileClick = {
                            // In a real app, this would navigate to a profile edit screen
                        },
                        driverViewModel = driverViewModel,
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
}

/**
 * Enum for driver screens with routes, titles and icons
 */
enum class NewDriverScreens(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    Dashboard("driver_dashboard", "Dashboard", Icons.Default.Home),
    Requests("driver_requests", "Requests", Icons.AutoMirrored.Filled.List),
    NewRequest("driver_new_request", "New Request", Icons.Default.Add),
    Profile("driver_profile", "Profile", Icons.Default.AccountCircle),
    RequestDetail("driver_request_detail/{requestId}", "Request Detail", Icons.Default.Info);

    companion object {
        fun requestDetailRoute(requestId: String): String = "driver_request_detail/$requestId"
    }
}
