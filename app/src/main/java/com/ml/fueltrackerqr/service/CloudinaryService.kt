package com.ml.fueltrackerqr.service

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service class for handling Cloudinary image uploads
 */
class CloudinaryService(private val context: Context) {

    companion object {
        private const val TAG = "CloudinaryService"
        private const val MAX_IMAGE_SIZE = 1024 // Max dimension for images
        private const val JPEG_QUALITY = 85 // JPEG compression quality (0-100)

        // Initialize Cloudinary with your credentials
        fun init(context: Context, cloudName: String, apiKey: String, apiSecret: String) {
            try {
                val config = mapOf(
                    "cloud_name" to cloudName,
                    "api_key" to apiKey,
                    "api_secret" to apiSecret,
                    "secure" to true
                )
                MediaManager.init(context, config)
                Log.d(TAG, "Cloudinary initialized successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing Cloudinary", e)
            }
        }
    }

    /**
     * Upload an image to Cloudinary from a URI
     *
     * @param imageUri URI of the image to upload
     * @param folder Optional folder to upload to in Cloudinary
     * @return URL of the uploaded image
     */
    suspend fun uploadImage(imageUri: Uri, folder: String = "fuel_tracker_profiles"): String = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting image upload process for URI: $imageUri")

            // Verify MediaManager is initialized
            if (MediaManager.get() == null) {
                Log.e(TAG, "MediaManager is not initialized. Initializing now...")
                throw IllegalStateException("Cloudinary MediaManager is not initialized")
            }

            // Compress the image before uploading
            Log.d(TAG, "Compressing image...")
            val compressedImageFile = compressImage(imageUri)
            Log.d(TAG, "Image compressed successfully. File size: ${compressedImageFile.length()} bytes")

            // Upload the compressed image
            Log.d(TAG, "Uploading image to Cloudinary...")
            val result = uploadToCloudinary(compressedImageFile.absolutePath, folder)
            Log.d(TAG, "Image uploaded successfully. URL: $result")

            return@withContext result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}", e)
            throw e
        }
    }

    /**
     * Compress an image from a URI
     *
     * @param imageUri URI of the image to compress
     * @return File containing the compressed image
     */
    private suspend fun compressImage(imageUri: Uri): File = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Could not open input stream for URI: $imageUri")

        // Decode image dimensions first
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        // Calculate sample size for downsampling
        val sampleSize = calculateSampleSize(options.outWidth, options.outHeight, MAX_IMAGE_SIZE)

        // Decode with the calculated sample size
        val inputStream2 = context.contentResolver.openInputStream(imageUri)
            ?: throw IllegalArgumentException("Could not open input stream for URI: $imageUri")

        val decodingOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
        }
        val bitmap = BitmapFactory.decodeStream(inputStream2, null, decodingOptions)
            ?: throw IllegalArgumentException("Could not decode bitmap from URI: $imageUri")
        inputStream2.close()

        // Create a temporary file for the compressed image
        val tempFile = File.createTempFile("compressed_image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)

        // Compress to JPEG
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, byteArrayOutputStream)
        outputStream.write(byteArrayOutputStream.toByteArray())

        // Clean up
        outputStream.close()
        bitmap.recycle()

        return@withContext tempFile
    }

    /**
     * Calculate sample size for downsampling an image
     */
    private fun calculateSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var sampleSize = 1
        if (width > maxDimension || height > maxDimension) {
            val widthRatio = Math.ceil(width.toDouble() / maxDimension.toDouble()).toInt()
            val heightRatio = Math.ceil(height.toDouble() / maxDimension.toDouble()).toInt()
            sampleSize = Math.max(widthRatio, heightRatio)
        }
        return sampleSize
    }

    /**
     * Upload a file to Cloudinary
     *
     * @param filePath Path to the file to upload
     * @param folder Folder to upload to in Cloudinary
     * @return URL of the uploaded image
     */
    private suspend fun uploadToCloudinary(filePath: String, folder: String): String = suspendCancellableCoroutine { continuation ->
        try {
            Log.d(TAG, "Starting Cloudinary upload for file: $filePath to folder: $folder")

            // Verify the file exists
            val file = File(filePath)
            if (!file.exists()) {
                val error = "File does not exist: $filePath"
                Log.e(TAG, error)
                continuation.resumeWithException(IllegalArgumentException(error))
                return@suspendCancellableCoroutine
            }

            // Create a unique upload ID using timestamp
            val uniqueId = "upload_${System.currentTimeMillis()}"

            // Create a signed upload request
            val requestId = MediaManager.get().upload(filePath)
                .option("folder", folder)
                .option("resource_type", "image")
                .option("public_id", uniqueId) // Use a unique ID to prevent conflicts
                .option("api_key", "714535422994564")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        Log.d(TAG, "Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        val progress = (bytes * 100) / totalBytes
                        Log.d(TAG, "Upload progress: $progress%")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        Log.d(TAG, "Upload successful: $requestId")
                        Log.d(TAG, "Result data: $resultData")

                        val secureUrl = resultData["secure_url"] as? String
                        if (secureUrl != null) {
                            Log.d(TAG, "Secure URL: $secureUrl")
                            continuation.resume(secureUrl)
                        } else {
                            val error = "Failed to get secure URL from Cloudinary response"
                            Log.e(TAG, error)
                            continuation.resumeWithException(Exception(error))
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        val errorMsg = "Upload error: ${error.description}, code: ${error.code}"
                        Log.e(TAG, errorMsg)
                        continuation.resumeWithException(Exception(errorMsg))
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        Log.d(TAG, "Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()

            continuation.invokeOnCancellation {
                Log.d(TAG, "Upload cancelled: $requestId")
                MediaManager.get().cancelRequest(requestId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in uploadToCloudinary: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }
}
