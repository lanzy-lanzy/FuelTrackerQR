package com.ml.fueltrackerqr.model

import kotlinx.serialization.Serializable
import java.util.Date

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
 *
 * @property id Unique identifier for the request
 * @property driverId ID of the driver who made the request
 * @property driverName Name of the driver who made the request
 * @property vehicleId ID of the vehicle for which fuel is requested
 * @property requestedAmount Amount of fuel requested (in liters)
 * @property dispensedAmount Amount of fuel actually dispensed (in liters)
 * @property status Current status of the request
 * @property requestDate Date when the request was made
 * @property approvalDate Date when the request was approved/declined
 * @property dispensedDate Date when the fuel was dispensed
 * @property approvedById ID of the admin who approved/declined the request
 * @property approvedByName Name of the admin who approved/declined the request
 * @property tripDetails Details about the trip for which fuel is requested
 * @property notes Additional notes for the request
 * @property qrCodeData QR code data for the approved request
 */
@Serializable
data class FuelRequest(
    val id: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val vehicleId: String = "",
    val requestedAmount: Double = 0.0,
    val dispensedAmount: Double = 0.0,
    val status: RequestStatus = RequestStatus.PENDING,
    val requestDate: Long = Date().time,
    val approvalDate: Long = 0,
    val dispensedDate: Long = 0,
    val approvedById: String = "",
    val approvedByName: String = "",
    val tripDetails: String = "",
    val notes: String = "",
    val qrCodeData: String = ""
)
