package com.ml.fueltrackerqr.viewmodel

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
    private val userRepository = UserRepository()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        checkCurrentUser()
    }
    
    /**
     * Check if a user is currently logged in
     */
    private fun checkCurrentUser() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            userRepository.getCurrentUser()
                .onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure {
                    _currentUser.value = null
                    _authState.value = AuthState.Unauthenticated
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
            _authState.value = AuthState.Loading
            userRepository.loginUser(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(exception.message ?: "Login failed")
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
