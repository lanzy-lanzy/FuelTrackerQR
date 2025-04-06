package com.ml.fueltrackerqr.data.model

import kotlinx.serialization.Serializable

/**
 * Enum representing the status of a fuel request
 */
enum class RequestStatus {
    PENDING,
    APPROVED,
    DECLINED,
    DISPENSED
}

/**
 * Data class representing a fuel request in the application
 */
@Serializable
data class FuelRequest(
    val id: String = "",
    val userId: String = "",
    val driverName: String = "",
    val vehicleInfo: String = "",
    val fuelAmount: Double = 0.0,
    val destination: String = "",
    val purpose: String = "",
    val status: RequestStatus = RequestStatus.PENDING,
    val timestamp: Long = 0,
    val approvedBy: String? = null,
    val approvedAt: Long? = null,
    val declinedBy: String? = null,
    val declinedAt: Long? = null,
    val declineReason: String? = null,
    val dispensedAt: Long? = null,
    val dispensedBy: String? = null
)
