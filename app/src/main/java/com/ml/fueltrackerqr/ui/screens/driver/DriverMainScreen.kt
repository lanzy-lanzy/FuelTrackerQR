package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.navigation.DriverRequestNavigation
import com.ml.fueltrackerqr.ui.theme.DarkTeal
import com.ml.fueltrackerqr.ui.theme.LightCoral
import com.ml.fueltrackerqr.ui.theme.MediumTeal
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel

/**
 * Main screen for driver with enhanced bottom navigation and visual styling
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

    // Create a gradient background for the entire screen with proper extension
    Box(modifier = Modifier.fillMaxSize()) {
        // Apply the gradient background to the entire screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF004D40), // Dark teal
                            Color(0xFF00897B), // Medium teal
                            Color(0xFF00897B).copy(alpha = 0.95f) // Slightly lighter teal
                        )
                    )
                )
        )

        // Place the Scaffold on top of the gradient background
        Scaffold(
            containerColor = Color.Transparent, // Make scaffold transparent to show gradient
            // Don't use system insets - we'll handle them manually
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                // Animated visibility for bottom navigation
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it },
                    exit = fadeOut() + slideOutVertically { it }
                ) {
                    // Use imePadding() to handle keyboard, but not navigationBarsPadding()
                    Box(modifier = Modifier.imePadding()) {
                        DriverBottomNavigation(
                            navController = navController,
                            currentRoute = currentRoute
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Add a subtle animation when switching between screens
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
}

/**
 * Simplified bottom navigation for driver screens with clean styling
 */
@Composable
fun DriverBottomNavigation(
    navController: NavHostController,
    currentRoute: String
) {
    // Add a shadow and rounded corners to the navigation bar
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = Color(0xFF00695C) // Consistent teal color
    ) {
        NavigationBar(
            containerColor = Color.Transparent, // Make transparent to show background
            contentColor = Color.White,
            tonalElevation = 0.dp,
            // Make sure it extends to the bottom of the screen
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // Slightly taller to ensure it covers any divider
        ) {
            DriverScreens.values().forEach { screen ->
                val selected = currentRoute == screen.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = screen.title,
                            tint = if (selected) Color.White else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(DriverScreens.Dashboard.route) {
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
                        indicatorColor = Color(0xFF004D40) // Darker teal for indicator
                    )
                )
            }
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
