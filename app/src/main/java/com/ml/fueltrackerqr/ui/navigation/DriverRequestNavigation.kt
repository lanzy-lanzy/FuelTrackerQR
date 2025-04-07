package com.ml.fueltrackerqr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ml.fueltrackerqr.ui.screens.driver.DriverRequestDetailScreen
import com.ml.fueltrackerqr.ui.screens.driver.DriverRequestHistoryScreen
import com.ml.fueltrackerqr.ui.screens.driver.DriverRequestsScreen
import com.ml.fueltrackerqr.ui.screens.driver.NewRequestScreen
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel

/**
 * Navigation routes for driver requests
 */
object DriverRequestRoutes {
    const val REQUESTS_LIST = "requests_list"
    const val NEW_REQUEST = "new_request"
    const val REQUEST_DETAIL = "request_detail/{requestId}"
    const val REQUEST_HISTORY = "request_history"
    
    // Helper functions to create routes with arguments
    fun requestDetail(requestId: String) = "request_detail/$requestId"
}

/**
 * Navigation component for driver requests
 */
@Composable
fun DriverRequestNavigation(
    navController: NavHostController = rememberNavController(),
    onBackToDriverDashboard: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val actions = remember(navController) { DriverRequestActions(navController) }
    
    NavHost(
        navController = navController,
        startDestination = DriverRequestRoutes.REQUESTS_LIST
    ) {
        // Requests list screen
        composable(DriverRequestRoutes.REQUESTS_LIST) {
            DriverRequestsScreen(
                onBackClick = onBackToDriverDashboard,
                onNewRequestClick = actions.navigateToNewRequest,
                onRequestClick = actions.navigateToRequestDetail,
                onHistoryClick = actions.navigateToRequestHistory,
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }
        
        // New request screen
        composable(DriverRequestRoutes.NEW_REQUEST) {
            NewRequestScreen(
                onRequestSubmitted = actions.navigateToRequestsList,
                onBackClick = actions.navigateBack,
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }
        
        // Request detail screen
        composable(
            route = DriverRequestRoutes.REQUEST_DETAIL,
            arguments = listOf(
                navArgument("requestId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString("requestId") ?: ""
            DriverRequestDetailScreen(
                requestId = requestId,
                onBackClick = actions.navigateBack,
                onCancelRequest = { actions.navigateToRequestsList() },
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }
        
        // Request history screen
        composable(DriverRequestRoutes.REQUEST_HISTORY) {
            DriverRequestHistoryScreen(
                onBackClick = actions.navigateBack,
                onRequestClick = actions.navigateToRequestDetail,
                driverViewModel = driverViewModel,
                authViewModel = authViewModel
            )
        }
    }
}

/**
 * Actions for driver request navigation
 */
class DriverRequestActions(private val navController: NavHostController) {
    val navigateToRequestsList: () -> Unit = {
        navController.navigate(DriverRequestRoutes.REQUESTS_LIST) {
            popUpTo(DriverRequestRoutes.REQUESTS_LIST) {
                inclusive = true
            }
        }
    }
    
    val navigateToNewRequest: () -> Unit = {
        navController.navigate(DriverRequestRoutes.NEW_REQUEST)
    }
    
    val navigateToRequestDetail: (String) -> Unit = { requestId ->
        navController.navigate(DriverRequestRoutes.requestDetail(requestId))
    }
    
    val navigateToRequestHistory: () -> Unit = {
        navController.navigate(DriverRequestRoutes.REQUEST_HISTORY)
    }
    
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}
