package com.ml.fueltrackerqr.ui.screens.gasstation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import com.ml.fueltrackerqr.ui.icons.Filled
import com.ml.fueltrackerqr.ui.icons.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ml.fueltrackerqr.ui.components.SplashGradientBackground
import com.ml.fueltrackerqr.viewmodel.AuthViewModel
import com.ml.fueltrackerqr.viewmodel.GasStationViewModel

/**
 * Dashboard screen for gas station operators
 *
 * @param onScanClick Callback when scan button is clicked
 * @param onLogoutClick Callback when logout button is clicked
 * @param gasStationViewModel ViewModel for gas station operations
 * @param authViewModel ViewModel for authentication
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationDashboardScreen(
    onScanClick: () -> Unit,
    onLogoutClick: () -> Unit,
    gasStationViewModel: GasStationViewModel,
    authViewModel: AuthViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gas Station Dashboard") },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { padding ->
        SplashGradientBackground(
            modifier = Modifier.padding(padding)
        ) {
            if (currentUser == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome, ${currentUser?.name}",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Icon(
                        imageVector = Icons.Filled.QrCodeScanner,
                        contentDescription = "Scan QR Code",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Scan a QR code to dispense fuel",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Use the button below to scan a QR code from a driver's approved fuel request",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onScanClick,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = "Scan"
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Scan QR Code")
                    }
                }
            }
        }
    }
}
