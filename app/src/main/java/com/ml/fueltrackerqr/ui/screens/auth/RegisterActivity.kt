package com.ml.fueltrackerqr.ui.screens.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ml.fueltrackerqr.MainActivity
import com.ml.fueltrackerqr.model.User
import com.ml.fueltrackerqr.model.UserRole
import com.ml.fueltrackerqr.ui.theme.TealCoralTheme
import com.ml.fueltrackerqr.viewmodel.AuthViewModel

/**
 * Activity for handling user registration
 */
class RegisterActivity : ComponentActivity() {
    
    private lateinit var authViewModel: AuthViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        
        setContent {
            TealCoralTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(
                        onRegisterSuccess = { user -> navigateToDestination(user) },
                        onBackClick = { onBackPressed() },
                        authViewModel = authViewModel
                    )
                }
            }
        }
    }
    
    /**
     * Navigate to the appropriate destination based on user role
     */
    private fun navigateToDestination(user: User) {
        val intent = when (user.role) {
            UserRole.DRIVER -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "DRIVER_DASHBOARD")
            }
            UserRole.ADMIN -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "ADMIN_DASHBOARD")
            }
            UserRole.GAS_STATION -> Intent(this, MainActivity::class.java).apply {
                putExtra("DESTINATION", "GAS_STATION_DASHBOARD")
            }
        }
        
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}
