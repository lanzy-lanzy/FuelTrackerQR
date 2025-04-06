package com.ml.fueltrackerqr.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.R
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
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
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onLoginSuccess((authState as AuthState.Authenticated).user)
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                authViewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        GradientBackground(
            brush = GradientBrushes.backgroundGradient,
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
                            painter = painterResource(id = R.drawable.ic_app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(140.dp)
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // App title
                    Text(
                        text = "Fuel Tracker QR",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Sign in to continue",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Login form in a semi-transparent card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(BackgroundDark.copy(alpha = 0.7f))
                            .padding(24.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Email field
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Password field
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Login button with gradient
                            PrimaryButton(
                                text = "Sign In",
                                onClick = {
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        authViewModel.login(email, password)
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
