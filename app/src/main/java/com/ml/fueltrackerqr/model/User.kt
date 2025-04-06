package com.ml.fueltrackerqr.model

import kotlinx.serialization.Serializable

/**
 * Enum representing user roles in the application
 */
enum class UserRole {
    DRIVER,
    ADMIN,
    GAS_STATION
}

/**
 * Data class representing a user in the application
 *
 * @property id Unique identifier for the user
 * @property name Full name of the user
 * @property email Email address of the user
 * @property role Role of the user (DRIVER, ADMIN, or GAS_STATION)
 * @property phoneNumber Optional phone number of the user
 * @property vehicleId Optional vehicle ID associated with the user (for drivers)
 */
@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DRIVER,
    val phoneNumber: String = "",
    val vehicleId: String = ""
)
