package com.ml.fueltrackerqr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.repository.FuelRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for admin-related operations
 */
class AdminViewModel : ViewModel() {
    private val fuelRequestRepository = FuelRequestRepository()
    
    private val _pendingRequests = MutableStateFlow<List<FuelRequest>>(emptyList())
    val pendingRequests: StateFlow<List<FuelRequest>> = _pendingRequests.asStateFlow()
    
    private val _approvedRequests = MutableStateFlow<List<FuelRequest>>(emptyList())
    val approvedRequests: StateFlow<List<FuelRequest>> = _approvedRequests.asStateFlow()
    
    private val _allRequests = MutableStateFlow<List<FuelRequest>>(emptyList())
    val allRequests: StateFlow<List<FuelRequest>> = _allRequests.asStateFlow()
    
    private val _selectedRequest = MutableStateFlow<FuelRequest?>(null)
    val selectedRequest: StateFlow<FuelRequest?> = _selectedRequest.asStateFlow()
    
    private val _adminActionState = MutableStateFlow<AdminActionState>(AdminActionState.Initial)
    val adminActionState: StateFlow<AdminActionState> = _adminActionState.asStateFlow()
    
    /**
     * Load pending fuel requests
     */
    fun loadPendingRequests() {
        viewModelScope.launch {
            fuelRequestRepository.getRequestsByStatus(RequestStatus.PENDING)
                .collectLatest { requests ->
                    _pendingRequests.value = requests
                }
        }
    }
    
    /**
     * Load approved fuel requests
     */
    fun loadApprovedRequests() {
        viewModelScope.launch {
            fuelRequestRepository.getRequestsByStatus(RequestStatus.APPROVED)
                .collectLatest { requests ->
                    _approvedRequests.value = requests
                }
        }
    }
    
    /**
     * Load all fuel requests
     */
    fun loadAllRequests() {
        viewModelScope.launch {
            fuelRequestRepository.getAllRequests()
                .collectLatest { requests ->
                    _allRequests.value = requests
                }
        }
    }
    
    /**
     * Select a request for detailed view
     * 
     * @param request The request to select
     */
    fun selectRequest(request: FuelRequest) {
        _selectedRequest.value = request
    }
    
    /**
     * Clear the selected request
     */
    fun clearSelectedRequest() {
        _selectedRequest.value = null
    }
    
    /**
     * Approve a fuel request
     * 
     * @param requestId ID of the request to approve
     * @param admin Admin user who is approving the request
     * @param approvedAmount Amount of fuel approved
     */
    fun approveRequest(requestId: String, admin: User, approvedAmount: Double) {
        viewModelScope.launch {
            _adminActionState.value = AdminActionState.Loading
            fuelRequestRepository.approveRequest(requestId, admin, approvedAmount)
                .onSuccess { request ->
                    _adminActionState.value = AdminActionState.Success("Request approved successfully")
                    _selectedRequest.value = request
                    refreshRequests()
                }
                .onFailure { exception ->
                    _adminActionState.value = AdminActionState.Error(exception.message ?: "Failed to approve request")
                }
        }
    }
    
    /**
     * Decline a fuel request
     * 
     * @param requestId ID of the request to decline
     * @param admin Admin user who is declining the request
     * @param notes Reason for declining
     */
    fun declineRequest(requestId: String, admin: User, notes: String) {
        viewModelScope.launch {
            _adminActionState.value = AdminActionState.Loading
            fuelRequestRepository.declineRequest(requestId, admin, notes)
                .onSuccess {
                    _adminActionState.value = AdminActionState.Success("Request declined")
                    refreshRequests()
                }
                .onFailure { exception ->
                    _adminActionState.value = AdminActionState.Error(exception.message ?: "Failed to decline request")
                }
        }
    }
    
    /**
     * Refresh all request lists
     */
    private fun refreshRequests() {
        loadPendingRequests()
        loadApprovedRequests()
        loadAllRequests()
    }
    
    /**
     * Clear the admin action state
     */
    fun clearAdminActionState() {
        _adminActionState.value = AdminActionState.Initial
    }
}

/**
 * Sealed class representing the state of an admin action
 */
sealed class AdminActionState {
    object Initial : AdminActionState()
    object Loading : AdminActionState()
    data class Success(val message: String) : AdminActionState()
    data class Error(val message: String) : AdminActionState()
}
