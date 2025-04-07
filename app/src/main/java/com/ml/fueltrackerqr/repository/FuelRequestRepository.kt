package com.ml.fueltrackerqr.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ml.fueltrackerqr.firebase.FirebaseConfig
import com.ml.fueltrackerqr.firebase.FirebaseNotInitializedException
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
    private val TAG = "FuelRequestRepository"

    // Lazy initialization of Firebase services to handle potential initialization errors
    private val firestore: FirebaseFirestore
        get() = try {
            FirebaseConfig.firestore
        } catch (e: FirebaseNotInitializedException) {
            Log.e(TAG, "Firebase Firestore not initialized", e)
            throw e
        }

    private val requestsCollection
        get() = firestore.collection(FirebaseConfig.FUEL_REQUESTS_COLLECTION)

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
            // Validate inputs
            if (driverId.isBlank()) {
                return Result.failure(IllegalArgumentException("Driver ID cannot be empty"))
            }
            if (vehicleId.isBlank()) {
                return Result.failure(IllegalArgumentException("Vehicle ID cannot be empty"))
            }
            if (requestedAmount <= 0) {
                return Result.failure(IllegalArgumentException("Requested amount must be greater than zero"))
            }

            // Generate a unique ID for the request
            val requestId = UUID.randomUUID().toString()
            Log.d(TAG, "Creating new fuel request with ID: $requestId")

            // Create the request object
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

            // Save to Firestore
            Log.d(TAG, "Saving fuel request to Firestore: $requestId")
            requestsCollection.document(requestId).set(request).await()
            Log.d(TAG, "Successfully saved fuel request: $requestId")

            Result.success(request)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating fuel request", e)
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
            Log.d(TAG, "Fetching fuel requests for driver: $driverId")

            // Validate driver ID
            if (driverId.isBlank()) {
                Log.e(TAG, "Driver ID is blank, cannot fetch requests")
                emit(emptyList())
                return@flow
            }

            // First emit an empty list to indicate loading has started
            emit(emptyList())

            // Log the collection path we're querying
            Log.d(TAG, "Querying collection: ${FirebaseConfig.FUEL_REQUESTS_COLLECTION} for driver: $driverId")
            Log.d(TAG, "Using field: ${FirebaseConfig.FIELD_REQUEST_DRIVER_ID} for query")

            // Just do a simple fetch to avoid crashes - without ordering to avoid index requirement
            val query = requestsCollection
                .whereEqualTo(FirebaseConfig.FIELD_REQUEST_DRIVER_ID, driverId)
                // Removed orderBy to avoid requiring a composite index

            Log.d(TAG, "Executing query: $query")

            val snapshot = query.get().await()

            Log.d(TAG, "Query completed. Documents found: ${snapshot.documents.size}")

            // Log each document for debugging
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "Document $index - ID: ${doc.id}")
                Log.d(TAG, "Document $index - Data: ${doc.data}")
            }

            // Process the results
            val requests = snapshot.documents.mapNotNull { doc ->
                try {
                    // Manual conversion to ensure proper enum handling
                    val data = doc.data
                    if (data != null) {
                        val id = doc.id
                        val driverId = data[FirebaseConfig.FIELD_REQUEST_DRIVER_ID] as? String ?: ""
                        val driverName = data[FirebaseConfig.FIELD_REQUEST_DRIVER_NAME] as? String ?: ""
                        val vehicleId = data[FirebaseConfig.FIELD_REQUEST_VEHICLE_ID] as? String ?: ""
                        val requestedAmount = (data[FirebaseConfig.FIELD_REQUEST_AMOUNT] as? Number)?.toDouble() ?: 0.0
                        val dispensedAmount = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED] as? Number)?.toDouble() ?: 0.0

                        // Handle status enum conversion carefully
                        val statusStr = data[FirebaseConfig.FIELD_REQUEST_STATUS] as? String ?: "PENDING"
                        val status = try {
                            RequestStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing status: $statusStr", e)
                            RequestStatus.PENDING
                        }

                        val requestDate = (data[FirebaseConfig.FIELD_REQUEST_DATE] as? Number)?.toLong() ?: 0L
                        val approvalDate = (data[FirebaseConfig.FIELD_REQUEST_APPROVAL_DATE] as? Number)?.toLong() ?: 0L
                        val dispensedDate = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED_DATE] as? Number)?.toLong() ?: 0L
                        val approvedById = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_ID] as? String ?: ""
                        val approvedByName = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_NAME] as? String ?: ""
                        val tripDetails = data[FirebaseConfig.FIELD_REQUEST_TRIP_DETAILS] as? String ?: ""
                        val notes = data[FirebaseConfig.FIELD_REQUEST_NOTES] as? String ?: ""
                        val qrCodeData = data[FirebaseConfig.FIELD_REQUEST_QR_CODE] as? String ?: ""

                        val request = FuelRequest(
                            id = id,
                            driverId = driverId,
                            driverName = driverName,
                            vehicleId = vehicleId,
                            requestedAmount = requestedAmount,
                            dispensedAmount = dispensedAmount,
                            status = status,
                            requestDate = requestDate,
                            approvalDate = approvalDate,
                            dispensedDate = dispensedDate,
                            approvedById = approvedById,
                            approvedByName = approvedByName,
                            tripDetails = tripDetails,
                            notes = notes,
                            qrCodeData = qrCodeData
                        )

                        Log.d(TAG, "Successfully parsed document ${doc.id} to FuelRequest: $request")
                        request
                    } else {
                        Log.e(TAG, "Document ${doc.id} has null data")
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${doc.id} to FuelRequest", e)
                    null
                }
            }

            // Sort the requests by requestDate in descending order (newest first)
            val sortedRequests = requests.sortedByDescending { it.requestDate }

            Log.d(TAG, "Found ${sortedRequests.size} valid requests for driver: $driverId")
            emit(sortedRequests)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching requests for driver: $driverId", e)
            Log.e(TAG, "Exception details: ${e.message}")
            e.printStackTrace()
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
            Log.d(TAG, "Fetching requests with status: ${status.name}")

            val snapshot = requestsCollection
                .whereEqualTo(FirebaseConfig.FIELD_REQUEST_STATUS, status.name)
                .orderBy(FirebaseConfig.FIELD_REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Query completed. Documents found: ${snapshot.documents.size}")

            // Log each document for debugging
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "Document $index - ID: ${doc.id}")
                Log.d(TAG, "Document $index - Data: ${doc.data}")
            }

            // Process the results with manual conversion
            val requests = snapshot.documents.mapNotNull { doc ->
                try {
                    // Manual conversion to ensure proper enum handling
                    val data = doc.data
                    if (data != null) {
                        val id = doc.id
                        val driverId = data[FirebaseConfig.FIELD_REQUEST_DRIVER_ID] as? String ?: ""
                        val driverName = data[FirebaseConfig.FIELD_REQUEST_DRIVER_NAME] as? String ?: ""
                        val vehicleId = data[FirebaseConfig.FIELD_REQUEST_VEHICLE_ID] as? String ?: ""
                        val requestedAmount = (data[FirebaseConfig.FIELD_REQUEST_AMOUNT] as? Number)?.toDouble() ?: 0.0
                        val dispensedAmount = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED] as? Number)?.toDouble() ?: 0.0

                        // Handle status enum conversion carefully
                        val statusStr = data[FirebaseConfig.FIELD_REQUEST_STATUS] as? String ?: "PENDING"
                        val docStatus = try {
                            RequestStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing status: $statusStr", e)
                            RequestStatus.PENDING
                        }

                        // Only include documents that match the requested status
                        if (docStatus != status) {
                            Log.d(TAG, "Skipping document ${doc.id} with status $docStatus (looking for $status)")
                            return@mapNotNull null
                        }

                        val requestDate = (data[FirebaseConfig.FIELD_REQUEST_DATE] as? Number)?.toLong() ?: 0L
                        val approvalDate = (data[FirebaseConfig.FIELD_REQUEST_APPROVAL_DATE] as? Number)?.toLong() ?: 0L
                        val dispensedDate = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED_DATE] as? Number)?.toLong() ?: 0L
                        val approvedById = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_ID] as? String ?: ""
                        val approvedByName = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_NAME] as? String ?: ""
                        val tripDetails = data[FirebaseConfig.FIELD_REQUEST_TRIP_DETAILS] as? String ?: ""
                        val notes = data[FirebaseConfig.FIELD_REQUEST_NOTES] as? String ?: ""
                        val qrCodeData = data[FirebaseConfig.FIELD_REQUEST_QR_CODE] as? String ?: ""

                        val request = FuelRequest(
                            id = id,
                            driverId = driverId,
                            driverName = driverName,
                            vehicleId = vehicleId,
                            requestedAmount = requestedAmount,
                            dispensedAmount = dispensedAmount,
                            status = docStatus,
                            requestDate = requestDate,
                            approvalDate = approvalDate,
                            dispensedDate = dispensedDate,
                            approvedById = approvedById,
                            approvedByName = approvedByName,
                            tripDetails = tripDetails,
                            notes = notes,
                            qrCodeData = qrCodeData
                        )

                        Log.d(TAG, "Successfully parsed document ${doc.id} to FuelRequest: $request")
                        request
                    } else {
                        Log.e(TAG, "Document ${doc.id} has null data")
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${doc.id} to FuelRequest", e)
                    null
                }
            }

            Log.d(TAG, "Found ${requests.size} valid requests with status: ${status.name}")
            emit(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching requests with status: ${status.name}", e)
            Log.e(TAG, "Exception details: ${e.message}")
            e.printStackTrace()
            emit(emptyList())
        }
    }

    /**
     * Cancel a fuel request
     *
     * @param requestId ID of the request to cancel
     * @return Result containing the updated FuelRequest or an exception
     */
    suspend fun cancelFuelRequest(requestId: String): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                return Result.failure(Exception("Request not found"))
            }

            val request = requestDoc.toObject(FuelRequest::class.java)
                ?: return Result.failure(Exception("Failed to parse request data"))

            if (request.status != RequestStatus.PENDING) {
                return Result.failure(Exception("Only pending requests can be cancelled"))
            }

            // Update the request status to DECLINED
            val updatedRequest = request.copy(
                status = RequestStatus.DECLINED,
                approvalDate = Date().time,
                notes = request.notes + "\n[Cancelled by driver]"
            )

            requestsCollection.document(requestId).set(updatedRequest).await()
            Result.success(updatedRequest)
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling fuel request", e)
            Result.failure(e)
        }
    }

    /**
     * Get all fuel requests
     *
     * @return Flow emitting a list of all fuel requests
     */
    fun getAllRequests(): Flow<List<FuelRequest>> = flow {
        try {
            Log.d(TAG, "Fetching all requests")

            val snapshot = requestsCollection
                .orderBy(FirebaseConfig.FIELD_REQUEST_DATE, Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(TAG, "Query completed. Documents found: ${snapshot.documents.size}")

            // Log each document for debugging
            snapshot.documents.forEachIndexed { index, doc ->
                Log.d(TAG, "Document $index - ID: ${doc.id}")
                Log.d(TAG, "Document $index - Data: ${doc.data}")
            }

            // Process the results with manual conversion
            val requests = snapshot.documents.mapNotNull { doc ->
                try {
                    // Manual conversion to ensure proper enum handling
                    val data = doc.data
                    if (data != null) {
                        val id = doc.id
                        val driverId = data[FirebaseConfig.FIELD_REQUEST_DRIVER_ID] as? String ?: ""
                        val driverName = data[FirebaseConfig.FIELD_REQUEST_DRIVER_NAME] as? String ?: ""
                        val vehicleId = data[FirebaseConfig.FIELD_REQUEST_VEHICLE_ID] as? String ?: ""
                        val requestedAmount = (data[FirebaseConfig.FIELD_REQUEST_AMOUNT] as? Number)?.toDouble() ?: 0.0
                        val dispensedAmount = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED] as? Number)?.toDouble() ?: 0.0

                        // Handle status enum conversion carefully
                        val statusStr = data[FirebaseConfig.FIELD_REQUEST_STATUS] as? String ?: "PENDING"
                        val status = try {
                            RequestStatus.valueOf(statusStr)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing status: $statusStr", e)
                            RequestStatus.PENDING
                        }

                        val requestDate = (data[FirebaseConfig.FIELD_REQUEST_DATE] as? Number)?.toLong() ?: 0L
                        val approvalDate = (data[FirebaseConfig.FIELD_REQUEST_APPROVAL_DATE] as? Number)?.toLong() ?: 0L
                        val dispensedDate = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED_DATE] as? Number)?.toLong() ?: 0L
                        val approvedById = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_ID] as? String ?: ""
                        val approvedByName = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_NAME] as? String ?: ""
                        val tripDetails = data[FirebaseConfig.FIELD_REQUEST_TRIP_DETAILS] as? String ?: ""
                        val notes = data[FirebaseConfig.FIELD_REQUEST_NOTES] as? String ?: ""
                        val qrCodeData = data[FirebaseConfig.FIELD_REQUEST_QR_CODE] as? String ?: ""

                        val request = FuelRequest(
                            id = id,
                            driverId = driverId,
                            driverName = driverName,
                            vehicleId = vehicleId,
                            requestedAmount = requestedAmount,
                            dispensedAmount = dispensedAmount,
                            status = status,
                            requestDate = requestDate,
                            approvalDate = approvalDate,
                            dispensedDate = dispensedDate,
                            approvedById = approvedById,
                            approvedByName = approvedByName,
                            tripDetails = tripDetails,
                            notes = notes,
                            qrCodeData = qrCodeData
                        )

                        Log.d(TAG, "Successfully parsed document ${doc.id} to FuelRequest: $request")
                        request
                    } else {
                        Log.e(TAG, "Document ${doc.id} has null data")
                        null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing document ${doc.id} to FuelRequest", e)
                    null
                }
            }

            Log.d(TAG, "Found ${requests.size} valid requests")
            emit(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all requests", e)
            Log.e(TAG, "Exception details: ${e.message}")
            e.printStackTrace()
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

            // Get the request using our manual parsing method
            val request = getRequestById(requestId).getOrElse { e ->
                return Result.failure(e)
            }

            if (request.status != RequestStatus.PENDING) {
                return Result.failure(Exception("Request is not in PENDING status"))
            }

            // Create a simple QR code string instead of complex JSON
            val qrCodeContent = "fuel_request:$requestId"

            // Log the QR code content for debugging
            Log.d(TAG, "Generated simple QR code for request: $requestId, content: $qrCodeContent")

            // Update the request
            val updatedRequest = request.copy(
                status = RequestStatus.APPROVED,
                approvalDate = Date().time,
                approvedById = admin.id,
                approvedByName = admin.name,
                qrCodeData = qrCodeContent
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

            // Get the request using our manual parsing method
            val request = getRequestById(requestId).getOrElse { e ->
                return Result.failure(e)
            }

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

            // Get the request using our manual parsing method
            val request = getRequestById(requestId).getOrElse { e ->
                return Result.failure(e)
            }

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
            Log.d(TAG, "Fetching request by ID: $requestId")

            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                Log.e(TAG, "Request not found: $requestId")
                return Result.failure(Exception("Request not found"))
            }

            // Manual conversion to ensure proper enum handling
            val data = requestDoc.data
            if (data != null) {
                val id = requestDoc.id
                val driverId = data[FirebaseConfig.FIELD_REQUEST_DRIVER_ID] as? String ?: ""
                val driverName = data[FirebaseConfig.FIELD_REQUEST_DRIVER_NAME] as? String ?: ""
                val vehicleId = data[FirebaseConfig.FIELD_REQUEST_VEHICLE_ID] as? String ?: ""
                val requestedAmount = (data[FirebaseConfig.FIELD_REQUEST_AMOUNT] as? Number)?.toDouble() ?: 0.0
                val dispensedAmount = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED] as? Number)?.toDouble() ?: 0.0

                // Handle status enum conversion carefully
                val statusStr = data[FirebaseConfig.FIELD_REQUEST_STATUS] as? String ?: "PENDING"
                val status = try {
                    RequestStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing status: $statusStr", e)
                    RequestStatus.PENDING
                }

                val requestDate = (data[FirebaseConfig.FIELD_REQUEST_DATE] as? Number)?.toLong() ?: 0L
                val approvalDate = (data[FirebaseConfig.FIELD_REQUEST_APPROVAL_DATE] as? Number)?.toLong() ?: 0L
                val dispensedDate = (data[FirebaseConfig.FIELD_REQUEST_DISPENSED_DATE] as? Number)?.toLong() ?: 0L
                val approvedById = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_ID] as? String ?: ""
                val approvedByName = data[FirebaseConfig.FIELD_REQUEST_APPROVED_BY_NAME] as? String ?: ""
                val tripDetails = data[FirebaseConfig.FIELD_REQUEST_TRIP_DETAILS] as? String ?: ""
                val notes = data[FirebaseConfig.FIELD_REQUEST_NOTES] as? String ?: ""
                val qrCodeData = data[FirebaseConfig.FIELD_REQUEST_QR_CODE] as? String ?: ""

                val request = FuelRequest(
                    id = id,
                    driverId = driverId,
                    driverName = driverName,
                    vehicleId = vehicleId,
                    requestedAmount = requestedAmount,
                    dispensedAmount = dispensedAmount,
                    status = status,
                    requestDate = requestDate,
                    approvalDate = approvalDate,
                    dispensedDate = dispensedDate,
                    approvedById = approvedById,
                    approvedByName = approvedByName,
                    tripDetails = tripDetails,
                    notes = notes,
                    qrCodeData = qrCodeData
                )

                Log.d(TAG, "Successfully parsed request: $request")
                Result.success(request)
            } else {
                Log.e(TAG, "Document $requestId has null data")
                Result.failure(Exception("Failed to parse request data"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching request by ID: $requestId", e)
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
            Log.d(TAG, "Validating QR code: $qrCodeContent")

            // Only support the simple format "fuel_request:{requestId}"
            if (!qrCodeContent.startsWith("fuel_request:")) {
                Log.e(TAG, "Invalid QR code format: $qrCodeContent")
                return Result.failure(Exception("Invalid QR code format. Expected 'fuel_request:{requestId}'."))
            }

            val requestId = qrCodeContent.substringAfter("fuel_request:")
            Log.d(TAG, "Extracted request ID from QR code: $requestId")

            if (requestId.isBlank()) {
                Log.e(TAG, "Empty request ID in QR code")
                return Result.failure(Exception("Invalid QR code: missing request ID"))
            }

            // Get the request
            val request = getRequestById(requestId).getOrElse {
                Log.e(TAG, "Request not found: $requestId")
                return Result.failure(Exception("Request not found. Please check with an administrator."))
            }

            Log.d(TAG, "Found request: $request")

            // Check if already dispensed
            if (request.status == RequestStatus.DISPENSED) {
                Log.w(TAG, "Fuel already dispensed for request: $requestId")
                return Result.failure(Exception("Fuel has already been dispensed for this request."))
            }

            // Check if request is approved
            if (request.status != RequestStatus.APPROVED) {
                Log.w(TAG, "Request is not approved: $requestId, status: ${request.status}")
                return Result.failure(Exception("This request is not approved for fuel dispensing."))
            }

            Log.d(TAG, "QR code validation successful for request: $requestId")
            Result.success(request)
        } catch (e: Exception) {
            Log.e(TAG, "Error validating QR code", e)
            Result.failure(Exception("Error processing QR code: ${e.message ?: "Unknown error"}"))
        }
    }

    /**
     * Update trip details for a fuel request
     *
     * @param requestId ID of the request to update
     * @param tripDetails New trip details
     * @return Result containing the updated FuelRequest or an exception
     */
    suspend fun updateTripDetails(requestId: String, tripDetails: String): Result<FuelRequest> {
        return try {
            val requestDoc = requestsCollection.document(requestId).get().await()
            if (!requestDoc.exists()) {
                return Result.failure(Exception("Request not found"))
            }

            val request = requestDoc.toObject(FuelRequest::class.java)
                ?: return Result.failure(Exception("Failed to parse request data"))

            if (request.status != RequestStatus.PENDING) {
                return Result.failure(Exception("Only pending requests can be updated"))
            }

            // Update the request
            val updatedRequest = request.copy(
                tripDetails = tripDetails
            )

            requestsCollection.document(requestId).set(updatedRequest).await()
            Result.success(updatedRequest)
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
        // Use a consistent value for the signature to ensure it's the same each time
        // In a real app, this would use a more secure method like HMAC
        return "$requestId:$driverId".hashCode().toString()
    }
}
