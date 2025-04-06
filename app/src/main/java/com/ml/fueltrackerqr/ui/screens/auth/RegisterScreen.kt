package com.ml.fueltrackerqr.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientBrushes
import com.ml.fueltrackerqr.ui.components.PrimaryButton
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.viewmodel.AuthState
import com.ml.fueltrackerqr.viewmodel.AuthViewModel

/**
 * Registration screen for new users
 *
 * @param onRegisterSuccess Callback when registration is successful
 * @param onBackClick Callback when back button is clicked
 * @param authViewModel ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: (User) -> Unit,
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.DRIVER) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                onRegisterSuccess((authState as AuthState.Authenticated).user)
            }
            is AuthState.Error -> {
                snackbarHostState.showSnackbar((authState as AuthState.Error).message)
                authViewModel.clearError()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        },
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
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Join Fuel Tracker QR",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    // Form card with semi-transparent background
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundDark.copy(alpha = 0.7f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Personal information fields
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                modifier = Modifier.fillMaxWidth(),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                // Using basic colors instead of the experimental API
                                // colors = TextFieldDefaults.outlinedTextFieldColors(
                                    // textColor = TextPrimary,
                                    // cursorColor = Primary,
                                    // focusedBorderColor = Primary,
                                    // unfocusedBorderColor = TextPrimary.copy(alpha = 0.5f),
                                    // focusedLabelColor = Primary,
                                    // unfocusedLabelColor = TextPrimary.copy(alpha = 0.7f)
                                // )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Role selection
                            Text(
                                text = "Select Your Role",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary,
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            UserRoleSelector(
                                selectedRole = selectedRole,
                                onRoleSelected = { selectedRole = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Register button with gradient
                    PrimaryButton(
                        text = "Create Account",
                        onClick = {
                            if (validateInputs(name, email, password, confirmPassword)) {
                                authViewModel.register(
                                    email = email,
                                    password = password,
                                    name = name,
                                    role = selectedRole,
                                    phoneNumber = phoneNumber
                                )
                            } else {
                                // Show validation error
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = authState !is AuthState.Loading
                    )
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

/**
 * Component for selecting a user role
 *
 * @param selectedRole Currently selected role
 * @param onRoleSelected Callback when a role is selected
 */
@Composable
fun UserRoleSelector(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        UserRole.values().forEach { role ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selectedRole == role) BackgroundDark.copy(alpha = 0.5f) else Color.Transparent
                    )
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedRole == role,
                    onClick = { onRoleSelected(role) },
                    colors = androidx.compose.material3.RadioButtonDefaults.colors(
                        selectedColor = Primary,
                        unselectedColor = TextPrimary.copy(alpha = 0.7f)
                    )
                )
                Text(
                    text = role.name.replace('_', ' ').capitalize(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = TextPrimary
                )
            }
        }
    }
}

/**
 * Extension function to capitalize the first letter of each word
 */
private fun String.capitalize(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Validate registration inputs
 *
 * @param name User's name
 * @param email User's email
 * @param password User's password
 * @param confirmPassword Password confirmation
 * @return True if inputs are valid, false otherwise
 */
private fun validateInputs(
    name: String,
    email: String,
    password: String,
    confirmPassword: String
): Boolean {
    return name.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            password == confirmPassword
}
