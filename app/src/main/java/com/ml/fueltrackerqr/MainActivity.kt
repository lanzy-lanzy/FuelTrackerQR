package com.ml.fueltrackerqr

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.ml.fueltrackerqr.firebase.FirebaseConfig
import com.ml.fueltrackerqr.navigation.AppNavigation
import com.ml.fueltrackerqr.navigation.AppScreen
import com.ml.fueltrackerqr.ui.theme.TealCoralTheme

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")

        try {
            // Set up basic UI first to avoid blank screen
            enableEdgeToEdge()

            // Make sure the navigation bar is transparent
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            // Always start with Login screen
            val startDestination = AppScreen.Login.name
            Log.d(TAG, "Start destination set to: $startDestination")

            // Check if Firebase is properly initialized
            Log.d(TAG, "Checking Firebase initialization")
            val firebaseAppInitialized = FuelTrackerApp.isFirebaseInitialized
            val firebaseConfigInitialized = FirebaseConfig.isInitialized()
            Log.d(TAG, "Firebase initialization status: App=$firebaseAppInitialized, Config=$firebaseConfigInitialized")

            // For demo purposes, we'll continue even if Firebase isn't initialized
            // This allows the app to work in demo mode with the test user
            if (!firebaseAppInitialized || !firebaseConfigInitialized) {
                Log.w(TAG, "Firebase not properly initialized, continuing in demo mode")
                Toast.makeText(this, "Running in demo mode. Use test@example.com / password to login.", Toast.LENGTH_LONG).show()
                // We don't return here, allowing the app to continue
            }

            Log.d(TAG, "Setting up main UI content")
            try {
                setContent {
                    Log.d(TAG, "Inside setContent block")
                    TealCoralTheme {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Log.d(TAG, "Creating NavController")
                            val navController = rememberNavController()
                            Log.d(TAG, "Setting up AppNavigation")
                            AppNavigation(
                                navController = navController,
                                startDestination = startDestination
                            )
                            Log.d(TAG, "AppNavigation setup complete")
                        }
                    }
                }
                Log.d(TAG, "Main UI content setup complete")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up UI", e)
                showErrorUI(e.message ?: "Unknown error occurred")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in onCreate", e)
            // Show a simple error toast as a last resort
            Toast.makeText(this, "Failed to start app: ${e.message}", Toast.LENGTH_LONG).show()
            finish() // Finish the activity if we can't even set up basic UI
        }
    }

    /**
     * Show error UI when Firebase is not initialized
     */
    private fun showFirebaseErrorUI() {
        Toast.makeText(this, "Error initializing app. Please try again.", Toast.LENGTH_LONG).show()

        setContent {
            TealCoralTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirebaseErrorScreen(onRetryClick = {
                        recreate() // Recreate the activity to try again
                    })
                }
            }
        }
    }

    /**
     * Show error UI for general errors
     */
    private fun showErrorUI(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

        setContent {
            TealCoralTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ErrorScreen(errorMessage = errorMessage, onRetryClick = {
                        recreate() // Recreate the activity to try again
                    })
                }
            }
        }
    }
}

/**
 * Error screen shown when Firebase fails to initialize
 */
@Composable
fun FirebaseErrorScreen(onRetryClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Failed to initialize app. Please check your internet connection and try again.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

/**
 * Generic error screen
 */
@Composable
fun ErrorScreen(errorMessage: String, onRetryClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = onRetryClick,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}