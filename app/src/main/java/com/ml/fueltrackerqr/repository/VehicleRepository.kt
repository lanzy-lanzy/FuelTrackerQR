package com.ml.fueltrackerqr.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ml.fueltrackerqr.firebase.FirebaseConfig
import com.ml.fueltrackerqr.model.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository class for vehicle-related operations
 */
class VehicleRepository {
    private val firestore = FirebaseConfig.firestore
    private val vehiclesCollection = firestore.collection(FirebaseConfig.VEHICLES_COLLECTION)
    
    /**
     * Add a new vehicle
     * 
     * @param registrationNumber Registration number of the vehicle
     * @param make Make of the vehicle
     * @param model Model of the vehicle
     * @param year Year of manufacture
     * @param fuelType Type of fuel used by the vehicle
     * @param tankCapacity Fuel tank capacity of the vehicle
     * @param assignedDriverId ID of the driver assigned to this vehicle
     * @return Result containing the created Vehicle or an exception
     */
    suspend fun addVehicle(
        registrationNumber: String,
        make: String,
        model: String,
        year: Int,
        fuelType: String,
        tankCapacity: Double,
        assignedDriverId: String = ""
    ): Result<Vehicle> {
        return try {
            val vehicleId = UUID.randomUUID().toString()
            val vehicle = Vehicle(
                id = vehicleId,
                registrationNumber = registrationNumber,
                make = make,
                model = model,
                year = year,
                fuelType = fuelType,
                tankCapacity = tankCapacity,
                assignedDriverId = assignedDriverId
            )
            
            vehiclesCollection.document(vehicleId).set(vehicle).await()
            Result.success(vehicle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all vehicles
     * 
     * @return Flow emitting a list of all vehicles
     */
    fun getAllVehicles(): Flow<List<Vehicle>> = flow {
        try {
            val snapshot = vehiclesCollection.get().await()
            val vehicles = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Vehicle::class.java)
            }
            emit(vehicles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    /**
     * Get vehicles assigned to a specific driver
     * 
     * @param driverId ID of the driver
     * @return Flow emitting a list of vehicles
     */
    fun getVehiclesByDriver(driverId: String): Flow<List<Vehicle>> = flow {
        try {
            val snapshot = vehiclesCollection
                .whereEqualTo(FirebaseConfig.FIELD_VEHICLE_DRIVER_ID, driverId)
                .get()
                .await()
            
            val vehicles = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Vehicle::class.java)
            }
            emit(vehicles)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    /**
     * Get a vehicle by ID
     * 
     * @param vehicleId ID of the vehicle
     * @return Result containing the Vehicle or an exception
     */
    suspend fun getVehicleById(vehicleId: String): Result<Vehicle> {
        return try {
            val vehicleDoc = vehiclesCollection.document(vehicleId).get().await()
            if (vehicleDoc.exists()) {
                val vehicle = vehicleDoc.toObject(Vehicle::class.java)
                    ?: throw Exception("Failed to parse vehicle data")
                Result.success(vehicle)
            } else {
                Result.failure(Exception("Vehicle not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update a vehicle
     * 
     * @param vehicle The updated vehicle object
     * @return Result indicating success or failure
     */
    suspend fun updateVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            vehiclesCollection.document(vehicle.id).set(vehicle).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Assign a vehicle to a driver
     * 
     * @param vehicleId ID of the vehicle
     * @param driverId ID of the driver
     * @return Result containing the updated Vehicle or an exception
     */
    suspend fun assignVehicleToDriver(vehicleId: String, driverId: String): Result<Vehicle> {
        return try {
            val vehicleResult = getVehicleById(vehicleId)
            if (vehicleResult.isFailure) {
                return Result.failure(vehicleResult.exceptionOrNull() ?: Exception("Vehicle not found"))
            }
            
            val vehicle = vehicleResult.getOrNull()!!
            val updatedVehicle = vehicle.copy(assignedDriverId = driverId)
            
            vehiclesCollection.document(vehicleId).set(updatedVehicle).await()
            Result.success(updatedVehicle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
