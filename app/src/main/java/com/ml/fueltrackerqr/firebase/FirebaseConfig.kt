package com.ml.fueltrackerqr.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Object containing Firebase instances and collection references
 */
object FirebaseConfig {
    // Firebase instances
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // Collection references
    const val USERS_COLLECTION = "users"
    const val FUEL_REQUESTS_COLLECTION = "fuel_requests"
    const val VEHICLES_COLLECTION = "vehicles"

    // User document fields
    const val FIELD_USER_ID = "id"
    const val FIELD_USER_NAME = "name"
    const val FIELD_USER_EMAIL = "email"
    const val FIELD_USER_ROLE = "role"
    const val FIELD_USER_PHONE = "phoneNumber"
    const val FIELD_USER_VEHICLE_ID = "vehicleId"

    // Fuel request document fields
    const val FIELD_REQUEST_ID = "id"
    const val FIELD_REQUEST_DRIVER_ID = "driverId"
    const val FIELD_REQUEST_DRIVER_NAME = "driverName"
    const val FIELD_REQUEST_VEHICLE_ID = "vehicleId"
    const val FIELD_REQUEST_AMOUNT = "requestedAmount"
    const val FIELD_REQUEST_DISPENSED = "dispensedAmount"
    const val FIELD_REQUEST_STATUS = "status"
    const val FIELD_REQUEST_DATE = "requestDate"
    const val FIELD_REQUEST_APPROVAL_DATE = "approvalDate"
    const val FIELD_REQUEST_DISPENSED_DATE = "dispensedDate"
    const val FIELD_REQUEST_APPROVED_BY_ID = "approvedById"
    const val FIELD_REQUEST_APPROVED_BY_NAME = "approvedByName"
    const val FIELD_REQUEST_TRIP_DETAILS = "tripDetails"
    const val FIELD_REQUEST_NOTES = "notes"
    const val FIELD_REQUEST_QR_CODE = "qrCodeData"

    // Vehicle document fields
    const val FIELD_VEHICLE_ID = "id"
    const val FIELD_VEHICLE_REG_NUMBER = "registrationNumber"
    const val FIELD_VEHICLE_MAKE = "make"
    const val FIELD_VEHICLE_MODEL = "model"
    const val FIELD_VEHICLE_YEAR = "year"
    const val FIELD_VEHICLE_FUEL_TYPE = "fuelType"
    const val FIELD_VEHICLE_TANK_CAPACITY = "tankCapacity"
    const val FIELD_VEHICLE_DRIVER_ID = "assignedDriverId"
}
