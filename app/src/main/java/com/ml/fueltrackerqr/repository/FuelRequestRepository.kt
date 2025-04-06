package com.ml.fueltrackerqr.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ml.fueltrackerqr.firebase.FirebaseConfig
import com.ml.fueltrackerqr.model.FuelRequest
import com.ml.fueltrackerqr.model.QRCodeData
import com.ml.fueltrackerqr.model.RequestStatus
import com.ml.fueltrackerqr.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Date
import java.util.UUID

/**
 * Repository class for fuel request operations
 */
class FuelRequestRepository {
    private val firestore = FirebaseConfig.firestore
    private val requestsCollection = firestore.collection(FirebaseConfig.FUEL_REQUESTS_COLLECTION)
    
    /**
     * Create a new fuel request
     * 
     * @param driverId ID of the driver making the request
     * @param driverName Name of the driver
     * @param vehicleId ID of the vehicle
     * @param requestedAmount Amount of fuel requested
     * @param tripDetails Details about the trip
     * @param notes Additional notes
     * @return Result containing the created FuelRequest or an exception
     */
    suspend fun createFuelRequest(
        driverId: String,
        driverName: String,
        vehicleId: String,
        requestedAmount: Double,
        tripDetails: String,
        notes: String = ""
    ): Result<FuelRequest> {
        return try {
            val requestId = UUID.randomUUID().toString()
            val request = FuelRequest(
                id = requestId,
                driverId = driverId,
                driverName = driverName,
                vehicleId = vehicleId,
                requestedAmount = requestedAmount,
                status = RequestStatus.PENDING,
                requestDate = Date().time,
                tripDetails = tripDetails,
                notes = notes
            )
            
            requestsCollection.document(requestId).set(request).await()
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get all fuel requests for a specific driver
     * 
     * @param driverId ID of the driver
     * @return Flow emitting a list of fuel requests
     */
    fun getRequestsByDriver(driverId: String): Flow<List<FuelRequest>> = flow {
        try {
            val snapshot = requestsCollection
                .whereEqualTo(FirebaseConfig.FIELD_REQUEST_DRIVER_ID, driverId)
                .orderBy(FirebaseConfig.FIELD_REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()
            
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FuelRequest::class.java)
            }
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    /**
     * Get all fuel requests with a specific status
     * 
     * @param status The status to filter by
     * @return Flow emitting a list of fuel requests
     */
    fun getRequestsByStatus(status: RequestStatus): Flow<List<FuelRequest>> = flow {
        try {
            val snapshot = requestsCollection
                .whereEqualTo(FirebaseConfig.FIELD_REQUEST_STATUS, status.name)
                .orderBy(FirebaseConfig.FIELD_REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()
            
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FuelRequest::class.java)
            }
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    /**
     * Get all fuel requests
     * 
     * @return Flow emitting a list of all fuel requests
     */
    fun getAllRequests(): Flow<List<FuelRequest>> = flow {
        try {
            val snapshot = requestsCollection
                .orderBy(FirebaseConfig.FIELD_REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()
            
            val requests = snapshot.documents.mapNotNull { doc ->
                doc.toObject(FuelRequest::class.java)
            }
            emit(requests)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    
    /**
     * Approve a fuel request
     * 
     * @param requestId ID of the request to approve
     * @param admin Admin user who is approving the request
     * @param approvedAmount Amount of fuel approved
     * @return Result containing the updated FuelRequest or an exception
     */
    suspend fun approveRequest(
        requestId: String,
        admin: User,
        approvedAmount: Double
    ): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                return Result.failure(Exception("Request not found"))
            }
            
            val request = requestDoc.toObject(FuelRequest::class.java)
                ?: return Result.failure(Exception("Failed to parse request data"))
            
            if (request.status != RequestStatus.PENDING) {
                return Result.failure(Exception("Request is not in PENDING status"))
            }
            
            // Create QR code data
            val qrCodeData = QRCodeData(
                requestId = requestId,
                driverId = request.driverId,
                vehicleId = request.vehicleId,
                approvedAmount = approvedAmount,
                approvalDate = Date().time,
                signature = generateSignature(requestId, request.driverId)
            )
            
            val qrCodeJson = Json.encodeToString(qrCodeData)
            
            // Update the request
            val updatedRequest = request.copy(
                status = RequestStatus.APPROVED,
                approvalDate = Date().time,
                approvedById = admin.id,
                approvedByName = admin.name,
                qrCodeData = qrCodeJson
            )
            
            requestsCollection.document(requestId).set(updatedRequest).await()
            Result.success(updatedRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Decline a fuel request
     * 
     * @param requestId ID of the request to decline
     * @param admin Admin user who is declining the request
     * @param notes Reason for declining
     * @return Result containing the updated FuelRequest or an exception
     */
    suspend fun declineRequest(
        requestId: String,
        admin: User,
        notes: String
    ): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                return Result.failure(Exception("Request not found"))
            }
            
            val request = requestDoc.toObject(FuelRequest::class.java)
                ?: return Result.failure(Exception("Failed to parse request data"))
            
            if (request.status != RequestStatus.PENDING) {
                return Result.failure(Exception("Request is not in PENDING status"))
            }
            
            // Update the request
            val updatedRequest = request.copy(
                status = RequestStatus.DECLINED,
                approvalDate = Date().time,
                approvedById = admin.id,
                approvedByName = admin.name,
                notes = notes
            )
            
            requestsCollection.document(requestId).set(updatedRequest).await()
            Result.success(updatedRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Mark a fuel request as dispensed
     * 
     * @param requestId ID of the request
     * @param dispensedAmount Actual amount of fuel dispensed
     * @return Result containing the updated FuelRequest or an exception
     */
    suspend fun markAsDispensed(
        requestId: String,
        dispensedAmount: Double
    ): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                return Result.failure(Exception("Request not found"))
            }
            
            val request = requestDoc.toObject(FuelRequest::class.java)
                ?: return Result.failure(Exception("Failed to parse request data"))
            
            if (request.status != RequestStatus.APPROVED) {
                return Result.failure(Exception("Request is not in APPROVED status"))
            }
            
            // Update the request
            val updatedRequest = request.copy(
                status = RequestStatus.DISPENSED,
                dispensedAmount = dispensedAmount,
                dispensedDate = Date().time
            )
            
            requestsCollection.document(requestId).set(updatedRequest).await()
            Result.success(updatedRequest)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get a fuel request by ID
     * 
     * @param requestId ID of the request
     * @return Result containing the FuelRequest or an exception
     */
    suspend fun getRequestById(requestId: String): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (requestDoc.exists()) {
                val request = requestDoc.toObject(FuelRequest::class.java)
                    ?: throw Exception("Failed to parse request data")
                Result.success(request)
            } else {
                Result.failure(Exception("Request not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Validate a QR code and get the associated fuel request
     * 
     * @param qrCodeContent Content of the scanned QR code
     * @return Result containing the FuelRequest or an exception
     */
    suspend fun validateQRCode(qrCodeContent: String): Result<FuelRequest> {
        return try {
            val qrCodeData = Json.decodeFromString<QRCodeData>(qrCodeContent)
            
            // Validate signature
            val expectedSignature = generateSignature(qrCodeData.requestId, qrCodeData.driverId)
            if (qrCodeData.signature != expectedSignature) {
                return Result.failure(Exception("Invalid QR code signature"))
            }
            
            // Check if expired
            if (qrCodeData.expiryDate < Date().time) {
                return Result.failure(Exception("QR code has expired"))
            }
            
            // Get the request
            val request = getRequestById(qrCodeData.requestId).getOrElse {
                return Result.failure(Exception("Request not found"))
            }
            
            // Check if already dispensed
            if (request.status == RequestStatus.DISPENSED) {
                return Result.failure(Exception("Fuel has already been dispensed for this request"))
            }
            
            Result.success(request)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Generate a signature for QR code validation
     * 
     * @param requestId ID of the request
     * @param driverId ID of the driver
     * @return Signature string
     */
    private fun generateSignature(requestId: String, driverId: String): String {
        // In a real app, this would use a more secure method
        return "$requestId:$driverId:${Date().time}".hashCode().toString()
    }
}
