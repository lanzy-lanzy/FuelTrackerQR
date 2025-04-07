package com.ml.fueltrackerqr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.QRCodeData
import com.ml.fueltrackerqr.repository.FuelRequestRepository
import com.ml.fueltrackerqr.repository.VehicleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Date

/**
 * ViewModel for gas station-related operations
 */
class GasStationViewModel : ViewModel() {
    private val TAG = "GasStationViewModel"
    private val fuelRequestRepository = FuelRequestRepository()
    private val vehicleRepository = VehicleRepository()

    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _dispensingState = MutableStateFlow<DispensingState>(DispensingState.Initial)
    val dispensingState: StateFlow<DispensingState> = _dispensingState.asStateFlow()

    private val _recentDispensings = MutableStateFlow<List<DispensingRecord>>(emptyList())
    val recentDispensings: StateFlow<List<DispensingRecord>> = _recentDispensings.asStateFlow()

    // Initialize with some recent dispensings
    init {
        loadRecentDispensings()
    }

    /**
     * Process a scanned QR code
     *
     * @param qrCodeContent Content of the scanned QR code
     */
    fun processQRCode(qrCodeContent: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Processing QR code: $qrCodeContent")
                _scanState.value = ScanState.Loading

                if (qrCodeContent.isBlank()) {
                    Log.e(TAG, "Empty QR code content")
                    _scanState.value = ScanState.Error("Empty QR code detected. Please try again.")
                    return@launch
                }

                // Validate the QR code using the repository
                fuelRequestRepository.validateQRCode(qrCodeContent)
                    .onSuccess { request ->
                        Log.d(TAG, "QR code validation successful for request: ${request.id}")

                        // Load vehicle details if available
                        if (request.vehicleId.isNotBlank()) {
                            try {
                                val vehicleResult = vehicleRepository.getVehicleById(request.vehicleId)
                                vehicleResult.onSuccess { vehicle ->
                                    Log.d(TAG, "Loaded vehicle details: ${vehicle.make} ${vehicle.model}")
                                    _scanState.value = ScanState.Success(request, vehicle)
                                }.onFailure { e ->
                                    Log.w(TAG, "Could not load vehicle details: ${e.message}")
                                    _scanState.value = ScanState.Success(request, null)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error loading vehicle details", e)
                                _scanState.value = ScanState.Success(request, null)
                            }
                        } else {
                            _scanState.value = ScanState.Success(request, null)
                        }
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "QR code validation failed: ${exception.message}")
                        _scanState.value = ScanState.Error(exception.message ?: "Invalid QR code")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error processing QR code", e)
                _scanState.value = ScanState.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    /**
     * Mark a fuel request as dispensed
     *
     * @param requestId ID of the request
     * @param dispensedAmount Actual amount of fuel dispensed
     */
    fun markAsDispensed(requestId: String, dispensedAmount: Double) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Marking request $requestId as dispensed with amount: $dispensedAmount")
                _dispensingState.value = DispensingState.Loading

                // Validate input
                if (dispensedAmount <= 0) {
                    _dispensingState.value = DispensingState.Error("Dispensed amount must be greater than zero")
                    return@launch
                }

                // Get the current request
                val currentRequest = (scanState.value as? ScanState.Success)?.request
                if (currentRequest == null || currentRequest.id != requestId) {
                    _dispensingState.value = DispensingState.Error("Request not found or mismatch")
                    return@launch
                }

                // Mark as dispensed
                fuelRequestRepository.markAsDispensed(requestId, dispensedAmount)
                    .onSuccess { updatedRequest ->
                        Log.d(TAG, "Successfully marked request as dispensed: $requestId")

                        // Add to recent dispensings
                        val newRecord = DispensingRecord(
                            requestId = requestId,
                            driverName = updatedRequest.driverName,
                            vehicleId = updatedRequest.vehicleId,
                            dispensedAmount = dispensedAmount,
                            dispensedDate = Date().time
                        )
                        _recentDispensings.value = listOf(newRecord) + _recentDispensings.value.take(9)

                        _dispensingState.value = DispensingState.Success(
                            "Fuel dispensed successfully: $dispensedAmount liters"
                        )
                        _scanState.value = ScanState.Initial
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Failed to mark request as dispensed", exception)
                        _dispensingState.value = DispensingState.Error(
                            exception.message ?: "Failed to mark as dispensed"
                        )
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error marking as dispensed", e)
                _dispensingState.value = DispensingState.Error("An unexpected error occurred. Please try again.")
            }
        }
    }

    /**
     * Load recent fuel dispensings
     */
    private fun loadRecentDispensings() {
        viewModelScope.launch {
            try {
                // In a real app, this would load from a repository
                // For now, we'll just use some sample data
                val sampleRecords = listOf(
                    DispensingRecord(
                        requestId = "sample1",
                        driverName = "John Doe",
                        vehicleId = "Toyota Corolla - ABC123",
                        dispensedAmount = 25.5,
                        dispensedDate = Date().time - 3600000 // 1 hour ago
                    ),
                    DispensingRecord(
                        requestId = "sample2",
                        driverName = "Jane Smith",
                        vehicleId = "Honda Civic - XYZ789",
                        dispensedAmount = 30.0,
                        dispensedDate = Date().time - 7200000 // 2 hours ago
                    )
                )
                _recentDispensings.value = sampleRecords
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recent dispensings", e)
            }
        }
    }

    /**
     * Clear the scan state
     */
    fun clearScanState() {
        _scanState.value = ScanState.Initial
    }

    /**
     * Set scan error state with a message
     *
     * @param message Error message to display
     */
    fun setScanError(message: String) {
        _scanState.value = ScanState.Error(message)
    }

    /**
     * Clear the dispensing state
     */
    fun clearDispensingState() {
        _dispensingState.value = DispensingState.Initial
    }
}

/**
 * Sealed class representing the state of a QR code scan
 */
sealed class ScanState {
    object Initial : ScanState()
    object Loading : ScanState()
    data class Success(
        val request: FuelRequest,
        val vehicle: com.ml.fueltrackerqr.model.Vehicle? = null
    ) : ScanState()
    data class Error(val message: String) : ScanState()
}

/**
 * Sealed class representing the state of a fuel dispensing operation
 */
sealed class DispensingState {
    object Initial : DispensingState()
    object Loading : DispensingState()
    data class Success(val message: String) : DispensingState()
    data class Error(val message: String) : DispensingState()
}

/**
 * Data class representing a record of a fuel dispensing transaction
 */
data class DispensingRecord(
    val requestId: String,
    val driverName: String,
    val vehicleId: String,
    val dispensedAmount: Double,
    val dispensedDate: Long
)
