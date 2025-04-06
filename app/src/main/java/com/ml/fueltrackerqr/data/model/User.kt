package com.ml.fueltrackerqr.data.model

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
 */
@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.DRIVER,
    val phoneNumber: String = ""
)
