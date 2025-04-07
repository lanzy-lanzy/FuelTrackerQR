package com.ml.fueltrackerqr.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
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
                title = {
                    Text(
                        "Create Account",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF004D40),
                    titleContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SplashGradientBackground(
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
                    // Spacer at the top for better layout
                    Spacer(modifier = Modifier.height(16.dp))

                    // Enhanced form card with shadow and better styling
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                spotColor = Color(0xFF004D40)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF004D40).copy(alpha = 0.85f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Form title
                            Text(
                                text = "Create Your Account",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Personal information fields with icons
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Name",
                                        tint = Color(0xFFF57C73)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
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

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text("Confirm Password") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Confirm Password",
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

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Phone Number (Optional)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Phone",
                                        tint = Color(0xFFF57C73)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                            // Enhanced role selection with card background
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF00695C).copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "Select Your Role",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    UserRoleSelector(
                                        selectedRole = selectedRole,
                                        onRoleSelected = { selectedRole = it }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enhanced register button with gradient and animation
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val buttonElevation by animateDpAsState(
                        targetValue = if (isPressed) 2.dp else 6.dp,
                        label = "Button Elevation"
                    )

                    Button(
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
                                // Show validation error via snackbar
                                // This would be handled in LaunchedEffect
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
                                text = "Create Account",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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

/**
 * Enhanced component for selecting a user role with better visual styling
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedRole == role)
                        Color(0xFF4DB6AC).copy(alpha = 0.3f)
                    else
                        Color(0xFF004D40).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedRole == role,
                        onClick = { onRoleSelected(role) },
                        colors = androidx.compose.material3.RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFF57C73),
                            unselectedColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                    Text(
                        text = role.name.replace('_', ' ').capitalize(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = Color.White
                    )
                }
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
