package com.ml.fueltrackerqr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.firebase.FirebaseNotInitializedException
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.repository.FuelRequestRepository
import com.ml.fueltrackerqr.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

/**
 * ViewModel for driver-related operations
 */
class DriverViewModel : ViewModel() {
    private val TAG = "DriverViewModel"
    private val fuelRequestRepository = FuelRequestRepository()
    private val vehicleRepository = VehicleRepository()

    private val _driverRequests = MutableStateFlow<List<FuelRequest>>(emptyList())
    val driverRequests: StateFlow<List<FuelRequest>> = _driverRequests.asStateFlow()

    private val _driverVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val driverVehicles: StateFlow<List<Vehicle>> = _driverVehicles.asStateFlow()

    private val _requestState = MutableStateFlow<RequestState>(RequestState.Initial)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()

    // Demo data for testing
    private val demoRequests = listOf(
        FuelRequest(
            id = UUID.randomUUID().toString(),
            driverId = "test-user-id",
            driverName = "Test User",
            vehicleId = "test-vehicle-id",
            requestedAmount = 45.0,
            status = RequestStatus.PENDING,
            requestDate = Date().time - 86400000, // 1 day ago
            tripDetails = "Trip to Nairobi",
            notes = "Urgent delivery"
        ),
        FuelRequest(
            id = UUID.randomUUID().toString(),
            driverId = "test-user-id",
            driverName = "Test User",
            vehicleId = "test-vehicle-id",
            requestedAmount = 30.0,
            status = RequestStatus.APPROVED,
            requestDate = Date().time - 172800000, // 2 days ago
            approvalDate = Date().time - 86400000, // 1 day ago
            approvedById = "admin-id",
            approvedByName = "Admin User",
            tripDetails = "Trip to Mombasa",
            notes = ""
        )
    )

    private val demoVehicles = listOf(
        Vehicle(
            id = "test-vehicle-id",
            registrationNumber = "KAA 123B",
            make = "Toyota",
            model = "Land Cruiser",
            year = 2020,
            fuelType = "Diesel",
            tankCapacity = 80.0,
            assignedDriverId = "test-user-id"
        )
    )

    /**
     * Load fuel requests for a driver
     *
     * @param driverId ID of the driver
     */
    fun loadDriverRequests(driverId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Loading driver requests for driver ID: $driverId")

            try {
                // Check if this is the test user
                if (driverId == "test-user-id") {
                    Log.d(TAG, "Using demo requests for test user")
                    _driverRequests.value = demoRequests
                    return@launch
                }

                // Otherwise, try to load from repository
                fuelRequestRepository.getRequestsByDriver(driverId)
                    .collectLatest { requests ->
                        Log.d(TAG, "Loaded ${requests.size} requests from repository")
                        _driverRequests.value = requests
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading driver requests", e)
                // If Firebase is not initialized, use demo data
                if (e is FirebaseNotInitializedException) {
                    Log.d(TAG, "Firebase not initialized, using demo requests")
                    _driverRequests.value = demoRequests
                }
            }
        }
    }

    /**
     * Load vehicles assigned to a driver
     *
     * @param driverId ID of the driver
     */
    fun loadDriverVehicles(driverId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Loading vehicles for driver ID: $driverId")

            try {
                // Check if this is the test user
                if (driverId == "test-user-id") {
                    Log.d(TAG, "Using demo vehicles for test user")
                    _driverVehicles.value = demoVehicles
                    return@launch
                }

                // Otherwise, try to load from repository
                vehicleRepository.getVehiclesByDriver(driverId)
                    .collectLatest { vehicles ->
                        Log.d(TAG, "Loaded ${vehicles.size} vehicles from repository")
                        _driverVehicles.value = vehicles
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading driver vehicles", e)
                // If Firebase is not initialized, use demo data
                if (e is FirebaseNotInitializedException) {
                    Log.d(TAG, "Firebase not initialized, using demo vehicles")
                    _driverVehicles.value = demoVehicles
                }
            }
        }
    }

    /**
     * Create a new fuel request
     *
     * @param driver Driver user making the request
     * @param vehicleId ID of the vehicle
     * @param requestedAmount Amount of fuel requested
     * @param tripDetails Details about the trip
     * @param notes Additional notes
     */
    fun createFuelRequest(
        driver: User,
        vehicleId: String,
        requestedAmount: Double,
        tripDetails: String,
        notes: String = ""
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Creating fuel request for driver: ${driver.name}, vehicle: $vehicleId")
            _requestState.value = RequestState.Loading

            try {
                // For test user or demo mode, create a demo request
                if (driver.id == "test-user-id" || !com.ml.fueltrackerqr.FuelTrackerApp.isFirebaseInitialized) {
                    Log.d(TAG, "Creating demo fuel request")
                    // Create a new demo request
                    val newRequest = FuelRequest(
                        id = UUID.randomUUID().toString(),
                        driverId = driver.id,
                        driverName = driver.name,
                        vehicleId = vehicleId,
                        requestedAmount = requestedAmount,
                        status = RequestStatus.PENDING,
                        requestDate = Date().time,
                        tripDetails = tripDetails,
                        notes = notes
                    )

                    // Add to the current list
                    val currentList = _driverRequests.value.toMutableList()
                    currentList.add(0, newRequest) // Add at the beginning
                    _driverRequests.value = currentList

                    _requestState.value = RequestState.Success("Fuel request submitted successfully (Demo)")
                    return@launch
                }

                // Otherwise use the repository
                fuelRequestRepository.createFuelRequest(
                    driverId = driver.id,
                    driverName = driver.name,
                    vehicleId = vehicleId,
                    requestedAmount = requestedAmount,
                    tripDetails = tripDetails,
                    notes = notes
                )
                    .onSuccess {
                        Log.d(TAG, "Fuel request created successfully")
                        _requestState.value = RequestState.Success("Fuel request submitted successfully")
                        loadDriverRequests(driver.id)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to create fuel request", exception)
                        _requestState.value = RequestState.Error(exception.message ?: "Failed to submit request")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception creating fuel request", e)
                _requestState.value = RequestState.Error(e.message ?: "Failed to submit request due to an error")
            }
        }
    }

    /**
     * Clear the request state
     */
    fun clearRequestState() {
        _requestState.value = RequestState.Initial
    }
}

/**
 * Sealed class representing the state of a request operation
 */
sealed class RequestState {
    object Initial : RequestState()
    object Loading : RequestState()
    data class Success(val message: String) : RequestState()
    data class Error(val message: String) : RequestState()
}
