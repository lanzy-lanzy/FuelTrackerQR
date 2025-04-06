package com.ml.fueltrackerqr.model

import kotlinx.serialization.Serializable
import java.util.Date

/**
 * Data class representing QR code data for fuel dispensing
 *
 * @property requestId ID of the fuel request
 * @property driverId ID of the driver
 * @property vehicleId ID of the vehicle
 * @property approvedAmount Amount of fuel approved (in liters)
 * @property approvalDate Date when the request was approved
 * @property expiryDate Date when the QR code expires
 * @property isUsed Whether the QR code has been used
 * @property signature Security signature for verification
 */
@Serializable
data class QRCodeData(
    val requestId: String,
    val driverId: String,
    val vehicleId: String,
    val approvedAmount: Double,
    val approvalDate: Long,
    val expiryDate: Long = Date().time + (7 * 24 * 60 * 60 * 1000), // 7 days from now
    val isUsed: Boolean = false,
    val signature: String = ""
)
