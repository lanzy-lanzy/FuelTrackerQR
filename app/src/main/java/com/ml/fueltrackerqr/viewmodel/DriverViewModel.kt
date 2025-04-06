package com.ml.fueltrackerqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.repository.FuelRequestRepository
import com.ml.fueltrackerqr.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for driver-related operations
 */
class DriverViewModel : ViewModel() {
    private val fuelRequestRepository = FuelRequestRepository()
    private val vehicleRepository = VehicleRepository()
    
    private val _driverRequests = MutableStateFlow<List<FuelRequest>>(emptyList())
    val driverRequests: StateFlow<List<FuelRequest>> = _driverRequests.asStateFlow()
    
    private val _driverVehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val driverVehicles: StateFlow<List<Vehicle>> = _driverVehicles.asStateFlow()
    
    private val _requestState = MutableStateFlow<RequestState>(RequestState.Initial)
    val requestState: StateFlow<RequestState> = _requestState.asStateFlow()
    
    /**
     * Load fuel requests for a driver
     * 
     * @param driverId ID of the driver
     */
    fun loadDriverRequests(driverId: String) {
        viewModelScope.launch {
            fuelRequestRepository.getRequestsByDriver(driverId)
                .collectLatest { requests ->
                    _driverRequests.value = requests
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
            vehicleRepository.getVehiclesByDriver(driverId)
                .collectLatest { vehicles ->
                    _driverVehicles.value = vehicles
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
            _requestState.value = RequestState.Loading
            fuelRequestRepository.createFuelRequest(
                driverId = driver.id,
                driverName = driver.name,
                vehicleId = vehicleId,
                requestedAmount = requestedAmount,
                tripDetails = tripDetails,
                notes = notes
            )
                .onSuccess {
                    _requestState.value = RequestState.Success("Fuel request submitted successfully")
                    loadDriverRequests(driver.id)
                }
                .onFailure { exception ->
                    _requestState.value = RequestState.Error(exception.message ?: "Failed to submit request")
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
