package com.ml.fueltrackerqr

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.ml.fueltrackerqr.firebase.FirebaseConfig

/**
 * Application class for FuelTracker app
 * Handles initialization of Firebase and other app-wide configurations
 */
class FuelTrackerApp : Application() {

    companion object {
        private const val TAG = "FuelTrackerApp"
        var isFirebaseInitialized = false
        private var initializationAttempted = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Application onCreate started")

        // Initialize Firebase with retry mechanism
        initializeFirebase()
    }

    /**
     * Initialize Firebase with retry mechanism
     */
    private fun initializeFirebase(retryCount: Int = 0) {
        if (retryCount > 3) {
            Log.e(TAG, "Failed to initialize Firebase after multiple attempts")
            showToast("Failed to initialize app. Please restart.")
            return
        }

        try {
            Log.d(TAG, "Attempting to initialize Firebase (attempt ${retryCount + 1})")
            initializationAttempted = true

            // Check if Firebase is already initialized
            if (!FirebaseApp.getApps(this).isEmpty()) {
                Log.d(TAG, "Firebase already initialized")
                isFirebaseInitialized = true
            } else {
                // Initialize Firebase
                FirebaseApp.initializeApp(this)
                Log.d(TAG, "Firebase initialized successfully")
                isFirebaseInitialized = true
            }

            // Initialize FirebaseConfig after Firebase is initialized
            FirebaseConfig.initialize()
            Log.d(TAG, "FirebaseConfig initialized successfully")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase", e)
            isFirebaseInitialized = false

            // Retry after a delay
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d(TAG, "Retrying Firebase initialization...")
                initializeFirebase(retryCount + 1)
            }, 1000) // 1 second delay before retry
        }
    }

    /**
     * Show a toast message on the main thread
     */
    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }
}
