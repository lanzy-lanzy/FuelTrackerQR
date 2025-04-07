package com.ml.fueltrackerqr.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.tween
import com.ml.fueltrackerqr.R
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.DarkTeal
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.viewmodel.AuthState
import com.ml.fueltrackerqr.viewmodel.AuthViewModel

/**
 * Login screen for user authentication
 *
 * @param onLoginSuccess Callback when login is successful
 * @param onRegisterClick Callback when register button is clicked
 * @param authViewModel ViewModel for authentication
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val TAG = "LoginScreen"
    Log.d(TAG, "Composing LoginScreen")

    val authState by authViewModel.authState.collectAsState()
    Log.d(TAG, "Current authState: ${authState::class.simpleName}")

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        Log.d(TAG, "LaunchedEffect triggered with authState: ${authState::class.simpleName}")
        when (authState) {
            is AuthState.Authenticated -> {
                Log.d(TAG, "User authenticated, calling onLoginSuccess")
                val user = (authState as AuthState.Authenticated).user
                Log.d(TAG, "Authenticated user: ${user.name}, role: ${user.role}")
                onLoginSuccess(user)
            }
            is AuthState.Error -> {
                Log.d(TAG, "Auth error: ${(authState as AuthState.Error).message}")
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                authViewModel.clearError()
            }
            is AuthState.Initial -> {
                Log.d(TAG, "Auth state is Initial")
            }
            is AuthState.Loading -> {
                Log.d(TAG, "Auth state is Loading")
            }
            is AuthState.Unauthenticated -> {
                Log.d(TAG, "Auth state is Unauthenticated")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo with animation
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -50 }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_fuel_pump),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(140.dp)
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login form in a semi-transparent card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkTeal.copy(alpha = 0.7f))
                            .padding(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Email field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Password field
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Login button with gradient
                            PrimaryButton(
                                text = "Sign In",
                                onClick = {
                                    Log.d(TAG, "Login button clicked")
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        Log.d(TAG, "Attempting login with email: $email")
                                        try {
                                            authViewModel.login(email, password)
                                            Log.d(TAG, "Login request sent successfully")
                                        } catch (e: Exception) {
                                            Log.e(TAG, "Error during login", e)
                                            // Show error via Toast
                                            Toast.makeText(
                                                context,
                                                "Login error: ${e.message ?: "Unknown error"}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        Log.d(TAG, "Email or password is blank")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = authState !is AuthState.Loading
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register link
                    TextButton(onClick = onRegisterClick) {
                        Text(
                            text = "Don't have an account? Register",
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                // Loading indicator
                AnimatedVisibility(
                    visible = authState is AuthState.Loading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
