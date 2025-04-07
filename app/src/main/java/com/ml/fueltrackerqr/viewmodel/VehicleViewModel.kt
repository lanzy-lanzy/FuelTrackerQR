package com.ml.fueltrackerqr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.repository.UserRepository
import com.ml.fueltrackerqr.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for vehicle management operations
 */
class VehicleViewModel : ViewModel() {
    private val TAG = "VehicleViewModel"
    private val vehicleRepository = VehicleRepository()
    private val userRepository = UserRepository()

    // State for all vehicles
    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles.asStateFlow()

    // State for drivers (for assignment)
    private val _drivers = MutableStateFlow<List<User>>(emptyList())
    val drivers: StateFlow<List<User>> = _drivers.asStateFlow()

    // State for operation status
    private val _operationState = MutableStateFlow<OperationState>(OperationState.Initial)
    val operationState: StateFlow<OperationState> = _operationState.asStateFlow()

    // State for selected vehicle
    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle.asStateFlow()

    // State for search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // State for filtered vehicles
    private val _filteredVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val filteredVehicles: StateFlow<List<Vehicle>> = _filteredVehicles.asStateFlow()

    /**
     * Load all vehicles
     */
    fun loadVehicles() {
        viewModelScope.launch {
            Log.d(TAG, "Loading all vehicles")
            _operationState.value = OperationState.Loading

            try {
                vehicleRepository.getAllVehicles()
                    .collectLatest { vehicleList ->
                        Log.d(TAG, "Loaded ${vehicleList.size} vehicles")
                        _vehicles.value = vehicleList
                        applyFilter() // Apply any existing filter
                        _operationState.value = OperationState.Success("Vehicles loaded successfully")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading vehicles", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to load vehicles")
            }
        }
    }

    /**
     * Load all drivers (for vehicle assignment)
     */
    fun loadDrivers() {
        viewModelScope.launch {
            Log.d(TAG, "Loading all drivers")
            _operationState.value = OperationState.Loading

            try {
                userRepository.getUsersByRole(UserRole.DRIVER)
                    .collectLatest { driverList ->
                        Log.d(TAG, "Loaded ${driverList.size} drivers")
                        _drivers.value = driverList
                        _operationState.value = OperationState.Success("Drivers loaded successfully")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading drivers", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to load drivers")
            }
        }
    }

    /**
     * Add a new vehicle
     */
    fun addVehicle(
        registrationNumber: String,
        make: String,
        model: String,
        year: Int,
        fuelType: String,
        tankCapacity: Double,
        assignedDriverId: String = ""
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Adding new vehicle: $make $model ($registrationNumber)")
            _operationState.value = OperationState.Loading

            try {
                // Validate input
                if (registrationNumber.isBlank()) {
                    _operationState.value = OperationState.Error("Registration number cannot be empty")
                    return@launch
                }
                if (make.isBlank()) {
                    _operationState.value = OperationState.Error("Make cannot be empty")
                    return@launch
                }
                if (model.isBlank()) {
                    _operationState.value = OperationState.Error("Model cannot be empty")
                    return@launch
                }
                if (year <= 0) {
                    _operationState.value = OperationState.Error("Year must be greater than 0")
                    return@launch
                }
                if (fuelType.isBlank()) {
                    _operationState.value = OperationState.Error("Fuel type cannot be empty")
                    return@launch
                }
                if (tankCapacity <= 0) {
                    _operationState.value = OperationState.Error("Tank capacity must be greater than 0")
                    return@launch
                }

                // Check if registration number is already in use
                val existingVehicle = _vehicles.value.find { it.registrationNumber.equals(registrationNumber, ignoreCase = true) }
                if (existingVehicle != null) {
                    _operationState.value = OperationState.Error("A vehicle with this registration number already exists")
                    return@launch
                }

                // Add the vehicle
                vehicleRepository.addVehicle(
                    registrationNumber = registrationNumber,
                    make = make,
                    model = model,
                    year = year,
                    fuelType = fuelType,
                    tankCapacity = tankCapacity,
                    assignedDriverId = assignedDriverId
                ).onSuccess { vehicle ->
                    Log.d(TAG, "Vehicle added successfully: ${vehicle.id}")
                    _operationState.value = OperationState.Success("Vehicle added successfully")
                    loadVehicles() // Refresh the list
                }.onFailure { e ->
                    Log.e(TAG, "Error adding vehicle", e)
                    _operationState.value = OperationState.Error(e.message ?: "Failed to add vehicle")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception adding vehicle", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to add vehicle due to an error")
            }
        }
    }

    /**
     * Update an existing vehicle
     */
    fun updateVehicle(
        id: String,
        registrationNumber: String,
        make: String,
        model: String,
        year: Int,
        fuelType: String,
        tankCapacity: Double,
        assignedDriverId: String = ""
    ) {
        viewModelScope.launch {
            Log.d(TAG, "Updating vehicle: $id")
            _operationState.value = OperationState.Loading

            try {
                // Validate input
                if (id.isBlank()) {
                    _operationState.value = OperationState.Error("Vehicle ID cannot be empty")
                    return@launch
                }
                if (registrationNumber.isBlank()) {
                    _operationState.value = OperationState.Error("Registration number cannot be empty")
                    return@launch
                }
                if (make.isBlank()) {
                    _operationState.value = OperationState.Error("Make cannot be empty")
                    return@launch
                }
                if (model.isBlank()) {
                    _operationState.value = OperationState.Error("Model cannot be empty")
                    return@launch
                }
                if (year <= 0) {
                    _operationState.value = OperationState.Error("Year must be greater than 0")
                    return@launch
                }
                if (fuelType.isBlank()) {
                    _operationState.value = OperationState.Error("Fuel type cannot be empty")
                    return@launch
                }
                if (tankCapacity <= 0) {
                    _operationState.value = OperationState.Error("Tank capacity must be greater than 0")
                    return@launch
                }

                // Check if registration number is already in use by another vehicle
                val existingVehicle = _vehicles.value.find {
                    it.id != id && it.registrationNumber.equals(registrationNumber, ignoreCase = true)
                }
                if (existingVehicle != null) {
                    _operationState.value = OperationState.Error("Another vehicle with this registration number already exists")
                    return@launch
                }

                // Create updated vehicle object
                val updatedVehicle = Vehicle(
                    id = id,
                    registrationNumber = registrationNumber,
                    make = make,
                    model = model,
                    year = year,
                    fuelType = fuelType,
                    tankCapacity = tankCapacity,
                    assignedDriverId = assignedDriverId
                )

                // Update the vehicle
                vehicleRepository.updateVehicle(updatedVehicle)
                    .onSuccess {
                        Log.d(TAG, "Vehicle updated successfully: $id")
                        _operationState.value = OperationState.Success("Vehicle updated successfully")
                        loadVehicles() // Refresh the list
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error updating vehicle", e)
                        _operationState.value = OperationState.Error(e.message ?: "Failed to update vehicle")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating vehicle", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to update vehicle due to an error")
            }
        }
    }

    /**
     * Assign a vehicle to a driver
     */
    fun assignVehicleToDriver(vehicleId: String, driverId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Assigning vehicle $vehicleId to driver $driverId")
            _operationState.value = OperationState.Loading

            try {
                // Validate input
                if (vehicleId.isBlank()) {
                    _operationState.value = OperationState.Error("Vehicle ID cannot be empty")
                    return@launch
                }
                if (driverId.isBlank()) {
                    _operationState.value = OperationState.Error("Driver ID cannot be empty")
                    return@launch
                }

                // Assign the vehicle
                vehicleRepository.assignVehicleToDriver(vehicleId, driverId)
                    .onSuccess { vehicle ->
                        Log.d(TAG, "Vehicle assigned successfully: ${vehicle.id} to driver: $driverId")
                        _operationState.value = OperationState.Success("Vehicle assigned successfully")
                        loadVehicles() // Refresh the list
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error assigning vehicle", e)
                        _operationState.value = OperationState.Error(e.message ?: "Failed to assign vehicle")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception assigning vehicle", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to assign vehicle due to an error")
            }
        }
    }

    /**
     * Unassign a vehicle from a driver
     */
    fun unassignVehicle(vehicleId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Unassigning vehicle $vehicleId")
            _operationState.value = OperationState.Loading

            try {
                // Validate input
                if (vehicleId.isBlank()) {
                    _operationState.value = OperationState.Error("Vehicle ID cannot be empty")
                    return@launch
                }

                // Get the current vehicle
                vehicleRepository.getVehicleById(vehicleId)
                    .onSuccess { vehicle ->
                        // Create updated vehicle with empty assignedDriverId
                        val updatedVehicle = vehicle.copy(assignedDriverId = "")

                        // Update the vehicle
                        vehicleRepository.updateVehicle(updatedVehicle)
                            .onSuccess {
                                Log.d(TAG, "Vehicle unassigned successfully: $vehicleId")
                                _operationState.value = OperationState.Success("Vehicle unassigned successfully")
                                loadVehicles() // Refresh the list
                            }
                            .onFailure { e ->
                                Log.e(TAG, "Error unassigning vehicle", e)
                                _operationState.value = OperationState.Error(e.message ?: "Failed to unassign vehicle")
                            }
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error getting vehicle for unassignment", e)
                        _operationState.value = OperationState.Error(e.message ?: "Failed to find vehicle")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception unassigning vehicle", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to unassign vehicle due to an error")
            }
        }
    }

    /**
     * Get a vehicle by ID
     */
    fun getVehicleById(vehicleId: String) {
        viewModelScope.launch {
            Log.d(TAG, "Getting vehicle by ID: $vehicleId")
            _operationState.value = OperationState.Loading

            try {
                vehicleRepository.getVehicleById(vehicleId)
                    .onSuccess { vehicle ->
                        Log.d(TAG, "Vehicle retrieved successfully: ${vehicle.id}")
                        _selectedVehicle.value = vehicle
                        _operationState.value = OperationState.Success("Vehicle retrieved successfully")
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error getting vehicle", e)
                        _operationState.value = OperationState.Error(e.message ?: "Failed to get vehicle")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception getting vehicle", e)
                _operationState.value = OperationState.Error(e.message ?: "Failed to get vehicle due to an error")
            }
        }
    }

    /**
     * Set search query and filter vehicles
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    /**
     * Apply filter to vehicles based on search query
     */
    private fun applyFilter() {
        val query = _searchQuery.value.trim().lowercase()
        if (query.isEmpty()) {
            _filteredVehicles.value = _vehicles.value
            return
        }

        _filteredVehicles.value = _vehicles.value.filter { vehicle ->
            vehicle.registrationNumber.lowercase().contains(query) ||
            vehicle.make.lowercase().contains(query) ||
            vehicle.model.lowercase().contains(query) ||
            vehicle.fuelType.lowercase().contains(query)
        }
    }

    /**
     * Clear the selected vehicle
     */
    fun clearSelectedVehicle() {
        _selectedVehicle.value = null
    }

    /**
     * Clear the operation state
     */
    fun clearOperationState() {
        _operationState.value = OperationState.Initial
    }

    /**
     * Set error state
     */
    fun setErrorState(message: String) {
        _operationState.value = OperationState.Error(message)
    }
}

/**
 * Sealed class representing the state of an operation
 */
sealed class OperationState {
    object Initial : OperationState()
    object Loading : OperationState()
    data class Success(val message: String) : OperationState()
    data class Error(val message: String) : OperationState()
}
