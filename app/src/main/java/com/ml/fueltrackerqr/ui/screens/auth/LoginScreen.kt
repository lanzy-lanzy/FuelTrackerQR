package com.ml.fueltrackerqr.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
    val scope = rememberCoroutineScope()

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
                    // Logo with enhanced animation and shadow
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -50 }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    spotColor = Color(0xFF00695C)
                                )
                                .background(Color.White, CircleShape)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_fuel_pump),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Enhanced login form with card and elevation
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color(0xFF004D40)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF004D40).copy(alpha = 0.85f)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Form title
                            Text(
                                text = "Welcome Back",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Email field with icon
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = Color(0xFFF57C73)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4DB6AC),
                                    unfocusedBorderColor = Color(0xFF80CBC4),
                                    focusedLabelColor = Color(0xFF4DB6AC),
                                    unfocusedLabelColor = Color(0xFF80CBC4),
                                    cursorColor = Color(0xFF4DB6AC),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Password field with icon
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Password",
                                        tint = Color(0xFFF57C73)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF4DB6AC),
                                    unfocusedBorderColor = Color(0xFF80CBC4),
                                    focusedLabelColor = Color(0xFF4DB6AC),
                                    unfocusedLabelColor = Color(0xFF80CBC4),
                                    cursorColor = Color(0xFF4DB6AC),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Login button with enhanced gradient and animation
                            val interactionSource = remember { MutableInteractionSource() }
                            val isPressed by interactionSource.collectIsPressedAsState()
                            val buttonElevation by animateDpAsState(
                                targetValue = if (isPressed) 2.dp else 6.dp,
                                label = "Button Elevation"
                            )

                            Button(
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
                                        // Show a toast instead of snackbar
                                        Toast.makeText(
                                            context,
                                            "Please enter email and password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d(TAG, "Email or password is blank")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(buttonElevation, RoundedCornerShape(28.dp)),
                                enabled = authState !is AuthState.Loading,
                                interactionSource = interactionSource,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(Color(0xFFF57C73), Color(0xFF00897B))
                                            ),
                                            shape = RoundedCornerShape(28.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Sign In",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enhanced register link with animation
                    val registerScale = remember { Animatable(1f) }
                    LaunchedEffect(Unit) {
                        // Subtle pulsing animation to draw attention
                        while(true) {
                            registerScale.animateTo(1.05f, animationSpec = tween(800))
                            registerScale.animateTo(1f, animationSpec = tween(800))
                            delay(2000) // Pause between pulses
                        }
                    }

                    Card(
                        modifier = Modifier
                            .scale(registerScale.value)
                            .shadow(4.dp, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF57C73).copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        TextButton(
                            onClick = onRegisterClick,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Don't have an account? Register",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
