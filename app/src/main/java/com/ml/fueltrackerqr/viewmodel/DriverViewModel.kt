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
                // Always try to load from Firebase first
                fuelRequestRepository.getRequestsByDriver(driverId)
                    .collectLatest { requests ->
                        Log.d(TAG, "Loaded ${requests.size} requests from repository")
                        if (requests.isNotEmpty()) {
                            _driverRequests.value = requests
                        } else if (driverId == "test-user-id") {
                            // If no requests found and this is a test user, use demo data
                            Log.d(TAG, "No requests found for test user, using demo data")
                            _driverRequests.value = demoRequests
                        } else {
                            // For real users with no requests, show empty list
                            _driverRequests.value = emptyList()
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading driver requests", e)
                // If Firebase is not initialized, use demo data for test user
                if (e is FirebaseNotInitializedException) {
                    Log.d(TAG, "Firebase not initialized, using fallback data")
                    if (driverId == "test-user-id") {
                        _driverRequests.value = demoRequests
                    } else {
                        // For real users, show current cached data or empty list
                        if (_driverRequests.value.isEmpty()) {
                            _driverRequests.value = emptyList()
                        }
                    }
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
                // Always try to load from Firebase first
                vehicleRepository.getVehiclesByDriver(driverId)
                    .collectLatest { vehicles ->
                        Log.d(TAG, "Loaded ${vehicles.size} vehicles from repository")
                        if (vehicles.isNotEmpty()) {
                            _driverVehicles.value = vehicles
                        } else if (driverId == "test-user-id") {
                            // If no vehicles found and this is a test user, use demo data
                            Log.d(TAG, "No vehicles found for test user, using demo data")
                            _driverVehicles.value = demoVehicles
                        } else {
                            // For real users with no vehicles, show empty list
                            _driverVehicles.value = emptyList()
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading driver vehicles", e)
                // If Firebase is not initialized, use demo data for test user
                if (e is FirebaseNotInitializedException) {
                    Log.d(TAG, "Firebase not initialized, using fallback data")
                    if (driverId == "test-user-id") {
                        _driverVehicles.value = demoVehicles
                    } else {
                        // For real users, show current cached data or empty list
                        if (_driverVehicles.value.isEmpty()) {
                            _driverVehicles.value = emptyList()
                        }
                    }
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
                // Check if Firebase is initialized
                if (!com.ml.fueltrackerqr.FuelTrackerApp.isFirebaseInitialized) {
                    Log.d(TAG, "Firebase not initialized, attempting to save locally")
                    // Create a new request locally
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

                    _requestState.value = RequestState.Success("Fuel request saved locally. It will be synced when connection is restored.")
                    return@launch
                }

                // Use the repository to save to Firebase
                fuelRequestRepository.createFuelRequest(
                    driverId = driver.id,
                    driverName = driver.name,
                    vehicleId = vehicleId,
                    requestedAmount = requestedAmount,
                    tripDetails = tripDetails,
                    notes = notes
                )
                    .onSuccess { request ->
                        Log.d(TAG, "Fuel request created successfully with ID: ${request.id}")
                        _requestState.value = RequestState.Success("Fuel request submitted successfully")

                        // Refresh the driver requests list
                        loadDriverRequests(driver.id)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to create fuel request", exception)
                        _requestState.value = RequestState.Error(exception.message ?: "Failed to submit request")

                        // Fallback to local storage if Firebase fails
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

                        _requestState.value = RequestState.Success("Fuel request saved locally due to connection issues. It will be synced later.")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception creating fuel request", e)
                _requestState.value = RequestState.Error(e.message ?: "Failed to submit request due to an error")
            }
        }
    }

    /**
     * Cancel a fuel request
     *
     * @param requestId ID of the request to cancel
     */
    fun cancelFuelRequest(requestId: String) {
        viewModelScope.launch {
            _requestState.value = RequestState.Loading

            try {
                // Find the request in the current list
                val request = _driverRequests.value.find { it.id == requestId }

                if (request == null) {
                    _requestState.value = RequestState.Error("Request not found")
                    return@launch
                }

                if (request.status != RequestStatus.PENDING) {
                    _requestState.value = RequestState.Error("Only pending requests can be cancelled")
                    return@launch
                }

                // Call repository to cancel the request
                val result = fuelRequestRepository.cancelFuelRequest(requestId)

                if (result.isSuccess) {
                    // Update the local list
                    val updatedList = _driverRequests.value.map {
                        if (it.id == requestId) {
                            it.copy(status = RequestStatus.DECLINED)
                        } else {
                            it
                        }
                    }
                    _driverRequests.value = updatedList

                    _requestState.value = RequestState.Success("Request cancelled successfully")
                } else {
                    _requestState.value = RequestState.Error(result.exceptionOrNull()?.message ?: "Failed to cancel request")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error cancelling fuel request", e)
                _requestState.value = RequestState.Error(e.message ?: "An error occurred while cancelling the request")
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
