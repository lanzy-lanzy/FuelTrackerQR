# Testing Profile Picture Upload Functionality

This document provides instructions on how to test the profile picture upload functionality in the FuelTrackerQR app.

## Building and Installing the App

### Using Android Studio (Recommended)

1. Open the project in Android Studio
2. Click on "Build" in the top menu
3. Select "Make Project" (or press Ctrl+F9)
4. After the build completes, click on "Run" in the top menu
5. Select "Run 'app'" (or press Shift+F10)

### Using Command Line (Alternative)

If you prefer using the command line, you can use the provided batch script:

1. Open a command prompt in the project directory
2. Run the `build_and_install.bat` script:
   ```
   .\build_and_install.bat
   ```

## Testing Steps

1. **Launch the app** and log in with your credentials
2. **Navigate to the Driver Profile screen** - you should see your profile with the default profile picture icon
3. **Tap on the profile picture** - this should trigger the permission request for storage access (if not already granted)
4. **Grant the permission** if prompted
5. **Select an image** from your gallery
6. **Wait for the upload** - you should see a loading indicator while the image is being uploaded to Cloudinary
7. **Verify the profile picture** is updated in the UI after the upload completes

## Expected Behavior

- You should see toast messages providing feedback during the process
- The profile picture should be updated successfully after upload
- If the Cloudinary upload fails, the app will fall back to saving the image locally

## Troubleshooting

If you encounter any issues during testing, check the following:

1. **Check Logcat in Android Studio** - Look for logs with the following tags:
   - "CloudinaryService" - for image upload related logs
   - "CloudinaryConfig" - for initialization related logs
   - "ProfilePicture" - for profile picture component logs
   - "AuthViewModel" - for profile picture update related logs
   - "UserRepository" - for Firestore update related logs

2. **Verify Cloudinary Dashboard** - After uploading a profile picture, check your Cloudinary dashboard at https://cloudinary.com/console to see if the image was uploaded to the "fuel_tracker_profiles" folder.

3. **Check Firestore Database** - Verify that the user document in Firestore has been updated with the profile picture URL.

4. **Common Issues and Solutions**:
   - If the image picker doesn't open, check if the storage permissions are granted
   - If the upload fails, check your internet connection and Cloudinary credentials
   - If the profile picture doesn't update after upload, check the Firestore update logs

## Cloudinary Credentials

The app is configured with the following Cloudinary credentials:

- Cloud name: dhvy50oqb
- API Key: 714535422994564
- API Secret: z4G22CrjD5mu2TrHKreYf8ZiQwo
