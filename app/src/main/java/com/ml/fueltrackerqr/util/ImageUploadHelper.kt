package com.ml.fueltrackerqr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Helper class for image upload operations
 */
object ImageUploadHelper {
    private const val TAG = "ImageUploadHelper"
    
    /**
     * Save an image to local storage as a fallback when cloud upload fails
     * 
     * @param context Application context
     * @param imageUri URI of the image to save
     * @return URI of the saved image
     */
    suspend fun saveImageLocally(context: Context, imageUri: Uri): Uri = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Saving image locally as fallback")
            
            // Create a unique filename
            val filename = "profile_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, filename)
            
            // Compress and save the image
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
            outputStream.flush()
            outputStream.close()
            
            Log.d(TAG, "Image saved locally: ${file.absolutePath}")
            
            // Return the URI of the saved file
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image locally: ${e.message}", e)
            throw e
        }
    }
    
    /**
     * Get a file URI as a string that can be used as a fallback profile picture URL
     * 
     * @param uri URI of the file
     * @return String representation of the URI
     */
    fun getFileUriAsString(uri: Uri): String {
        return uri.toString()
    }
}
