package com.ml.fueltrackerqr.ui.screens.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import com.ml.fueltrackerqr.ui.icons.Filled
import com.ml.fueltrackerqr.ui.theme.backgroundGradient
import com.ml.fueltrackerqr.ui.theme.primaryGradient
import com.ml.fueltrackerqr.ui.theme.secondaryGradient
import com.ml.fueltrackerqr.ui.icons.DirectionsCar
import com.ml.fueltrackerqr.ui.icons.LocalGasStation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.Vehicle
import com.ml.fueltrackerqr.ui.components.GradientBackground
import com.ml.fueltrackerqr.ui.components.GradientDivider
import com.ml.fueltrackerqr.util.GradientBrushes
import com.ml.fueltrackerqr.ui.theme.BackgroundDark
import com.ml.fueltrackerqr.ui.theme.BackgroundLight
import com.ml.fueltrackerqr.ui.theme.Primary
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.DriverViewModel

/**
 * Screen for displaying and editing driver profile information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    driverViewModel: DriverViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val driverVehicles by driverViewModel.driverVehicles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEditProfileClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDark,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        GradientBackground(
            brush = GradientBrushes.backgroundGradient,
            modifier = Modifier.padding(padding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (currentUser == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile header with avatar
                        ProfileHeader(user = currentUser!!)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Personal information section
                        ProfileInfoSection(user = currentUser!!)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Vehicle information section
                        VehicleInfoSection(vehicles = driverVehicles)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Fuel usage statistics
                        FuelUsageSection()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(user: User) {
    val avatarScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "avatar_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar with gradient border
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = avatarScale
                    scaleY = avatarScale
                }
        ) {
            // Outer glow
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(8.dp, CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Primary.copy(alpha = 0.7f),
                                Primary.copy(alpha = 0.0f)
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Avatar circle
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(BackgroundLight)
                    .border(
                        width = 2.dp,
                        brush = GradientBrushes.primaryGradient,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile Avatar",
                    tint = TextPrimary,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User name
        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // User role
        Text(
            text = "Driver",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun ProfileInfoSection(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            GradientDivider(
                brush = GradientBrushes.primaryGradient,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email
            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = user.email
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Phone",
                value = user.phoneNumber.ifEmpty { "Not provided" }
            )
        }
    }
}

@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f)
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun VehicleInfoSection(vehicles: List<Vehicle>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Assigned Vehicles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            GradientDivider(
                brush = GradientBrushes.primaryGradient,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (vehicles.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {
                    Text(
                        text = "No vehicles assigned yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextPrimary.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                vehicles.forEachIndexed { index, vehicle ->
                    VehicleCard(vehicle = vehicle)

                    if (index < vehicles.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleCard(vehicle: Vehicle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundLight.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vehicle icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = "Vehicle",
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Vehicle details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${vehicle.make} ${vehicle.model} (${vehicle.year})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Reg: ${vehicle.registrationNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalGasStation,
                        contentDescription = "Fuel Type",
                        tint = Primary,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.size(4.dp))

                    Text(
                        text = "${vehicle.fuelType} (${vehicle.tankCapacity}L)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun FuelUsageSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundDark.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Fuel Usage Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            GradientDivider(
                brush = GradientBrushes.primaryGradient,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sample statistics - in a real app, these would come from the ViewModel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "This Month",
                    value = "45.2L",
                    brush = GradientBrushes.primaryGradient
                )

                StatisticItem(
                    label = "Last Month",
                    value = "62.8L",
                    brush = GradientBrushes.pendingGradient
                )

                StatisticItem(
                    label = "Total",
                    value = "358.5L",
                    brush = GradientBrushes.purpleBlueGradient
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String,
    brush: Brush
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth(0.2f)
                .background(brush)
        )
    }
}
