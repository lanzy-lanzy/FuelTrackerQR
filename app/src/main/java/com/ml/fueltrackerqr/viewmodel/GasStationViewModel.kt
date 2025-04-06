package com.ml.fueltrackerqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.repository.FuelRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for gas station-related operations
 */
class GasStationViewModel : ViewModel() {
    private val fuelRequestRepository = FuelRequestRepository()
    
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Initial)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()
    
    private val _dispensingState = MutableStateFlow<DispensingState>(DispensingState.Initial)
    val dispensingState: StateFlow<DispensingState> = _dispensingState.asStateFlow()
    
    /**
     * Process a scanned QR code
     * 
     * @param qrCodeContent Content of the scanned QR code
     */
    fun processQRCode(qrCodeContent: String) {
        viewModelScope.launch {
            _scanState.value = ScanState.Loading
            fuelRequestRepository.validateQRCode(qrCodeContent)
                .onSuccess { request ->
                    _scanState.value = ScanState.Success(request)
                }
                .onFailure { exception ->
                    _scanState.value = ScanState.Error(exception.message ?: "Invalid QR code")
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
            _dispensingState.value = DispensingState.Loading
            fuelRequestRepository.markAsDispensed(requestId, dispensedAmount)
                .onSuccess {
                    _dispensingState.value = DispensingState.Success("Fuel dispensed successfully")
                    _scanState.value = ScanState.Initial
                }
                .onFailure { exception ->
                    _dispensingState.value = DispensingState.Error(exception.message ?: "Failed to mark as dispensed")
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
    data class Success(val request: FuelRequest) : ScanState()
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
