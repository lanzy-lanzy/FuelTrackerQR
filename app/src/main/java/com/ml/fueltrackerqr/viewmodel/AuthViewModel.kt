package com.ml.fueltrackerqr.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authentication-related operations
 */
class AuthViewModel : ViewModel() {
    private val TAG = "AuthViewModel"
    private val userRepository = UserRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Safely check for current user, handling potential Firebase initialization errors
        safeCheckCurrentUser()
    }

    /**
     * Safely check for current user, handling potential Firebase initialization errors
     */
    private fun safeCheckCurrentUser() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Safely checking for current user")
                checkCurrentUser()
                Log.d(TAG, "Current user check completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking current user", e)
                // Set to unauthenticated state to allow login flow to proceed
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    /**
     * Check if a user is currently logged in
     */
    private fun checkCurrentUser() {
        viewModelScope.launch {
            Log.d(TAG, "Starting checkCurrentUser")
            _authState.value = AuthState.Loading
            Log.d(TAG, "Auth state set to Loading")

            try {
                Log.d(TAG, "Calling userRepository.getCurrentUser()")
                userRepository.getCurrentUser()
                    .onSuccess { user ->
                        Log.d(TAG, "Current user found: ${user.name}, role: ${user.role}")
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated(user)
                        Log.d(TAG, "Auth state set to Authenticated")
                    }
                    .onFailure { error ->
                        Log.d(TAG, "No current user found: ${error.message}")
                        _currentUser.value = null
                        _authState.value = AuthState.Unauthenticated
                        Log.d(TAG, "Auth state set to Unauthenticated")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in checkCurrentUser", e)
                _currentUser.value = null
                _authState.value = AuthState.Unauthenticated
                Log.d(TAG, "Auth state set to Unauthenticated due to exception")
            }
        }
    }

    /**
     * Register a new user
     *
     * @param email User's email
     * @param password User's password
     * @param name User's full name
     * @param role User's role
     * @param phoneNumber User's phone number
     */
    fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.DRIVER,
        phoneNumber: String = ""
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            userRepository.registerUser(email, password, name, role, phoneNumber)
                .onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Registration failed")
                }
        }
    }

    /**
     * Login a user
     *
     * @param email User's email
     * @param password User's password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d(TAG, "Login started for email: $email")
            _authState.value = AuthState.Loading

            // For testing purposes, allow a test user to bypass Firebase
            if (email == "test@example.com" && password == "password") {
                Log.d(TAG, "Using test user account")
                val testUser = User(
                    id = "test-user-id",
                    name = "Test User",
                    email = email,
                    role = UserRole.DRIVER
                )
                _currentUser.value = testUser
                _authState.value = AuthState.Authenticated(testUser)
                Log.d(TAG, "Test user authenticated successfully")
                return@launch
            }

            try {
                Log.d(TAG, "Attempting Firebase login")
                userRepository.loginUser(email, password)
                    .onSuccess { user ->
                        Log.d(TAG, "Login successful for user: ${user.name}")
                        _currentUser.value = user
                        _authState.value = AuthState.Authenticated(user)
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "Login failed", exception)
                        _authState.value = AuthState.Error(exception.message ?: "Login failed")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during login", e)
                _authState.value = AuthState.Error(e.message ?: "Login failed due to an unexpected error")
            }
        }
    }

    /**
     * Logout the current user
     */
    fun logout() {
        userRepository.logout()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Clear any error state
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}

/**
 * Sealed class representing the authentication state
 */
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}
