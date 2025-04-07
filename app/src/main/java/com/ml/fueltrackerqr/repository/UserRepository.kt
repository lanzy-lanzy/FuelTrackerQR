package com.ml.fueltrackerqr.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ml.fueltrackerqr.firebase.FirebaseConfig
import com.ml.fueltrackerqr.firebase.FirebaseNotInitializedException
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository class for user-related operations
 */
class UserRepository {
    private val TAG = "UserRepository"

    // Lazy initialization of Firebase services to handle potential initialization errors
    private val auth: FirebaseAuth
        get() = try {
            FirebaseConfig.auth
        } catch (e: FirebaseNotInitializedException) {
            Log.e(TAG, "Firebase Auth not initialized", e)
            throw e
        }

    private val firestore: FirebaseFirestore
        get() = try {
            FirebaseConfig.firestore
        } catch (e: FirebaseNotInitializedException) {
            Log.e(TAG, "Firebase Firestore not initialized", e)
            throw e
        }

    private val usersCollection
        get() = firestore.collection(FirebaseConfig.USERS_COLLECTION)

    /**
     * Register a new user
     *
     * @param email User's email
     * @param password User's password
     * @param name User's full name
     * @param role User's role
     * @param phoneNumber User's phone number
     * @return Result containing the created User object or an exception
     */
    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.DRIVER,
        phoneNumber: String = "",
        vehicleId: String = ""
    ): Result<User> {
        return try {
            // Create authentication account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Failed to get user ID")

            // Create user document in Firestore
            val user = User(
                id = userId,
                name = name,
                email = email,
                role = role,
                phoneNumber = phoneNumber,
                vehicleId = vehicleId
            )

            usersCollection.document(userId).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error in registerUser", e)
            Result.failure(e)
        }
    }

    /**
     * Login a user
     *
     * @param email User's email
     * @param password User's password
     * @return Result containing the User object or an exception
     */
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Failed to get user ID")

            val userDoc = usersCollection.document(userId).get().await()
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                    ?: throw Exception("Failed to parse user data")
                Result.success(user)
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error in loginUser", e)
            Result.failure(e)
        }
    }

    /**
     * Get the current logged-in user
     *
     * @return Result containing the User object or an exception
     */
    suspend fun getCurrentUser(): Result<User> {
        val currentUser = auth.currentUser

        return if (currentUser != null) {
            try {
                val userDoc = usersCollection.document(currentUser.uid).get().await()
                if (userDoc.exists()) {
                    val user = userDoc.toObject(User::class.java)
                        ?: throw Exception("Failed to parse user data")
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error in getCurrentUser", e)
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("No user is currently logged in"))
        }
    }

    /**
     * Logout the current user
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Get all users with a specific role
     *
     * @param role The role to filter by
     * @return Flow emitting a list of users with the specified role
     */
    fun getUsersByRole(role: UserRole): Flow<List<User>> = flow {
        try {
            val snapshot = usersCollection
                .whereEqualTo(FirebaseConfig.FIELD_USER_ROLE, role.name)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            emit(users)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error in getUsersByRole", e)
            emit(emptyList())
        }
    }

    /**
     * Update user information
     *
     * @param user The updated user object
     * @return Result indicating success or failure
     */
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error in updateUser", e)
            Result.failure(e)
        }
    }

    /**
     * Update user's profile picture URL
     *
     * @param userId ID of the user to update
     * @param profilePictureUrl New profile picture URL
     * @return Result containing the updated User object or an exception
     */
    suspend fun updateProfilePicture(userId: String, profilePictureUrl: String): Result<User> {
        return try {
            // Get current user data
            val userDoc = usersCollection.document(userId).get().await()
            if (!userDoc.exists()) {
                return Result.failure(Exception("User not found"))
            }

            val user = userDoc.toObject(User::class.java) ?: throw Exception("Failed to parse user data")

            // Update with new profile picture URL
            val updatedUser = user.copy(profilePictureUrl = profilePictureUrl)

            // Save to Firestore
            usersCollection.document(userId).set(updatedUser).await()

            Result.success(updatedUser)
        } catch (e: Exception) {
            Log.e(TAG, "Error in updateProfilePicture", e)
            Result.failure(e)
        }
    }
}
