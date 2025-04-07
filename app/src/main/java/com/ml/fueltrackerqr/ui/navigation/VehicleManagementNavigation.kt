package com.ml.fueltrackerqr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ml.fueltrackerqr.ui.screens.admin.AddVehicleScreen
import com.ml.fueltrackerqr.ui.screens.admin.AssignDriverScreen
import com.ml.fueltrackerqr.ui.screens.admin.VehicleDetailScreen
import com.ml.fueltrackerqr.ui.screens.admin.VehicleListScreen

/**
 * Navigation routes for vehicle management
 */
object VehicleManagementRoutes {
    const val VEHICLE_LIST = "vehicle_list"
    const val ADD_VEHICLE = "add_vehicle"
    const val VEHICLE_DETAIL = "vehicle_detail/{vehicleId}"
    const val ASSIGN_DRIVER = "assign_driver/{vehicleId}"

    // Helper functions to create routes with arguments
    fun vehicleDetail(vehicleId: String) = "vehicle_detail/$vehicleId"
    fun assignDriver(vehicleId: String) = "assign_driver/$vehicleId"
}

/**
 * Navigation component for vehicle management
 */
@Composable
fun VehicleManagementNavigation(
    navController: NavHostController = rememberNavController(),
    onBackToAdminDashboard: () -> Unit
) {
    val actions = remember(navController) { VehicleManagementActions(navController) }

    NavHost(
        navController = navController,
        startDestination = VehicleManagementRoutes.VEHICLE_LIST
    ) {
        // Vehicle list screen
        composable(VehicleManagementRoutes.VEHICLE_LIST) {
            VehicleListScreen(
                onBackClick = onBackToAdminDashboard,
                onAddVehicleClick = actions.navigateToAddVehicle,
                onVehicleClick = actions.navigateToVehicleDetail
            )
        }

        // Add vehicle screen
        composable(VehicleManagementRoutes.ADD_VEHICLE) {
            AddVehicleScreen(
                onBackClick = actions.navigateBack,
                onVehicleAdded = actions.navigateToVehicleList
            )
        }

        // Vehicle detail screen
        composable(
            route = VehicleManagementRoutes.VEHICLE_DETAIL,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            VehicleDetailScreen(
                vehicleId = vehicleId,
                onBackClick = actions.navigateBack,
                onEditClick = { /* TODO: Implement edit functionality */ },
                onAssignClick = { actions.navigateToAssignDriver(vehicleId) }
            )
        }

        // Assign driver screen
        composable(
            route = VehicleManagementRoutes.ASSIGN_DRIVER,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: ""
            AssignDriverScreen(
                vehicleId = vehicleId,
                onBackClick = actions.navigateBack,
                onAssignComplete = { actions.navigateToVehicleDetail(vehicleId) }
            )
        }
    }
}

/**
 * Actions for vehicle management navigation
 */
class VehicleManagementActions(private val navController: NavHostController) {
    val navigateToVehicleList: () -> Unit = {
        navController.navigate(VehicleManagementRoutes.VEHICLE_LIST) {
            popUpTo(VehicleManagementRoutes.VEHICLE_LIST) {
                inclusive = true
            }
        }
    }

    val navigateToAddVehicle: () -> Unit = {
        navController.navigate(VehicleManagementRoutes.ADD_VEHICLE)
    }

    val navigateToVehicleDetail: (String) -> Unit = { vehicleId ->
        navController.navigate(VehicleManagementRoutes.vehicleDetail(vehicleId))
    }

    val navigateToAssignDriver: (String) -> Unit = { vehicleId ->
        navController.navigate(VehicleManagementRoutes.assignDriver(vehicleId))
    }

    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
}
