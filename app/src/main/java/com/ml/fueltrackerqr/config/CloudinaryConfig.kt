package com.ml.fueltrackerqr.config

import android.content.Context
import android.util.Log
import com.cloudinary.android.MediaManager
import com.ml.fueltrackerqr.service.CloudinaryService

/**
 * Configuration class for Cloudinary
 */
object CloudinaryConfig {
    private const val TAG = "CloudinaryConfig"

    // Cloudinary credentials
    private const val CLOUD_NAME = "dhvy50oqb"
    private const val API_KEY = "714535422994564"
    private const val API_SECRET = "z4G22CrjD5mu2TrHKreYf8ZiQwo"

    private var isInitialized = false

    /**
     * Initialize Cloudinary with the provided credentials
     *
     * @param context Application context
     */
    fun init(context: Context) {
        if (isInitialized && MediaManager.get() != null) {
            Log.d(TAG, "Cloudinary already initialized")
            return
        }

        try {
            // Check if MediaManager is already initialized
            if (MediaManager.get() != null) {
                Log.d(TAG, "MediaManager already initialized")
                isInitialized = true
                return
            }

            // Create configuration map
            val config = mapOf(
                "cloud_name" to CLOUD_NAME,
                "api_key" to API_KEY,
                "api_secret" to API_SECRET,
                "secure" to true
            )

            // Initialize MediaManager directly
            MediaManager.init(context, config)
            isInitialized = true
            Log.d(TAG, "Cloudinary initialized successfully with cloud name: $CLOUD_NAME")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Cloudinary: ${e.message}", e)
            // Try to initialize through CloudinaryService as fallback
            try {
                CloudinaryService.init(context, CLOUD_NAME, API_KEY, API_SECRET)
                isInitialized = true
                Log.d(TAG, "Cloudinary initialized successfully through CloudinaryService")
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to initialize Cloudinary through CloudinaryService: ${e2.message}", e2)
            }
        }
    }

    /**
     * Check if Cloudinary is initialized
     *
     * @return true if Cloudinary is initialized, false otherwise
     */
    fun isInitialized(): Boolean {
        return try {
            val initialized = MediaManager.get() != null
            Log.d(TAG, "Cloudinary initialization status: $initialized")
            initialized
        } catch (e: Exception) {
            Log.e(TAG, "Error checking Cloudinary initialization status: ${e.message}", e)
            false
        }
    }

    /**
     * Update Cloudinary configuration with custom credentials
     *
     * @param context Application context
     * @param cloudName Cloudinary cloud name
     * @param apiKey Cloudinary API key
     * @param apiSecret Cloudinary API secret
     */
    fun updateConfig(context: Context, cloudName: String, apiKey: String, apiSecret: String) {
        try {
            // If already initialized, we need to deinitialize first
            if (MediaManager.get() != null) {
                MediaManager.get().cancelAllRequests()
            }

            // Create configuration map
            val config = mapOf(
                "cloud_name" to cloudName,
                "api_key" to apiKey,
                "api_secret" to apiSecret,
                "secure" to true
            )

            // Initialize MediaManager with new config
            MediaManager.init(context, config)
            isInitialized = true
            Log.d(TAG, "Cloudinary configuration updated successfully with cloud name: $cloudName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update Cloudinary configuration: ${e.message}", e)
            // Try to initialize through CloudinaryService as fallback
            try {
                CloudinaryService.init(context, cloudName, apiKey, apiSecret)
                isInitialized = true
                Log.d(TAG, "Cloudinary configuration updated successfully through CloudinaryService")
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to update Cloudinary configuration through CloudinaryService: ${e2.message}", e2)
            }
        }
    }
}
