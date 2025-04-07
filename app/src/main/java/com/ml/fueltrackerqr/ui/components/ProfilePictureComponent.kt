package com.ml.fueltrackerqr.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ml.fueltrackerqr.service.CloudinaryService
import com.ml.fueltrackerqr.ui.theme.BackgroundLight
import com.ml.fueltrackerqr.ui.theme.TextPrimary
import com.ml.fueltrackerqr.util.GradientBrushes
import com.ml.fueltrackerqr.util.ImageUploadHelper
import kotlinx.coroutines.launch

/**
 * A component for displaying and updating a user's profile picture
 *
 * @param profilePictureUrl URL of the current profile picture
 * @param onProfilePictureUpdated Callback when profile picture is updated with new URL
 * @param size Size of the profile picture component
 * @param editable Whether the profile picture can be edited
 * @param modifier Modifier for the component
 */
@Composable
fun ProfilePicture(
    profilePictureUrl: String,
    onProfilePictureUpdated: (String) -> Unit,
    size: Int = 100,
    editable: Boolean = true,
    modifier: Modifier = Modifier,
    borderBrush: Brush = GradientBrushes.primaryGradient
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State for tracking upload progress
    var isUploading by remember { mutableStateOf(false) }

    // Create Cloudinary service
    val cloudinaryService = remember { CloudinaryService(context) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isUploading = true
            coroutineScope.launch {
                try {
                    try {
                        // Try to upload image to Cloudinary
                        Log.d("ProfilePicture", "Attempting to upload image to Cloudinary")
                        val imageUrl = cloudinaryService.uploadImage(uri)

                        // Call the callback with the new URL
                        Log.d("ProfilePicture", "Cloudinary upload successful: $imageUrl")
                        onProfilePictureUpdated(imageUrl)
                    } catch (cloudinaryError: Exception) {
                        // If Cloudinary upload fails, save the image locally as a fallback
                        Log.e("ProfilePicture", "Cloudinary upload failed: ${cloudinaryError.message}. Using local fallback.", cloudinaryError)
                        Toast.makeText(
                            context,
                            "Cloud upload failed. Saving image locally.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Save image locally
                        val localUri = ImageUploadHelper.saveImageLocally(context, uri)
                        val localUriString = ImageUploadHelper.getFileUriAsString(localUri)

                        // Call the callback with the local URI
                        Log.d("ProfilePicture", "Local save successful: $localUriString")
                        onProfilePictureUpdated(localUriString)
                    }
                } catch (e: Exception) {
                    Log.e("ProfilePicture", "Error handling image: ${e.message}", e)
                    Toast.makeText(
                        context,
                        "Failed to process image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                } finally {
                    isUploading = false
                }
            }
        }
    }

    // Permission launcher for storage access
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Launch image picker if permission granted
            imagePickerLauncher.launch("image/*")
        } else {
            // Show a toast message if permission is denied
            Toast.makeText(
                context,
                "Storage permission is required to select an image",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(BackgroundLight)
            .border(
                width = 2.dp,
                brush = borderBrush,
                shape = CircleShape
            )
            .then(
                if (editable) {
                    Modifier.clickable {
                        // Request appropriate permission based on Android version
                        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_IMAGES
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }

                        // Check if permission is already granted
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            // Permission already granted, launch picker directly
                            imagePickerLauncher.launch("image/*")
                        } else {
                            // Request permission
                            permissionLauncher.launch(permission)
                        }
                    }
                } else {
                    Modifier
                }
            )
    ) {
        if (isUploading) {
            // Show loading indicator while uploading
            CircularProgressIndicator(
                modifier = Modifier.size((size / 2).dp),
                color = Color(0xFF00BCD4) // Teal
            )
        } else if (profilePictureUrl.isNotEmpty()) {
            // Display profile picture if URL is available
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profilePictureUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size.dp),
                loading = {
                    CircularProgressIndicator(
                        modifier = Modifier.size((size / 3).dp),
                        color = Color(0xFF00BCD4) // Teal
                    )
                },
                error = {
                    // Show default icon if image fails to load
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile Avatar",
                        tint = TextPrimary,
                        modifier = Modifier.size((size * 0.6f).dp)
                    )
                }
            )
        } else {
            // Show default icon if no profile picture
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Avatar",
                tint = TextPrimary,
                modifier = Modifier.size((size * 0.6f).dp)
            )
        }

        // Show add icon if editable and no current picture
        if (editable && profilePictureUrl.isEmpty() && !isUploading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((size * 0.3f).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00BCD4)), // Teal
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Profile Picture",
                    tint = Color.White,
                    modifier = Modifier.size((size * 0.2f).dp)
                )
            }
        }
    }
}
