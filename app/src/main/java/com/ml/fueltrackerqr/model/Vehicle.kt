package com.ml.fueltrackerqr.model

import kotlinx.serialization.Serializable

/**
 * Data class representing a vehicle in the application
 *
 * @property id Unique identifier for the vehicle
 * @property registrationNumber Registration number of the vehicle
 * @property make Make of the vehicle
 * @property model Model of the vehicle
 * @property year Year of manufacture
 * @property fuelType Type of fuel used by the vehicle
 * @property tankCapacity Fuel tank capacity of the vehicle (in liters)
 * @property assignedDriverId ID of the driver assigned to this vehicle
 */
@Serializable
data class Vehicle(
    val id: String = "",
    val registrationNumber: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val fuelType: String = "",
    val tankCapacity: Double = 0.0,
    val assignedDriverId: String = ""
)
