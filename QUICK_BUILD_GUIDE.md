# Quick Build Guide for FuelTrackerQR

Since we're having issues with the command line build, here are the simplest ways to build and install the app:

## Option 1: Using Android Studio (Recommended)

1. Run the `open_android_studio.bat` script to open Android Studio
2. Wait for the project to load
3. Click on "Build" in the top menu
4. Select "Build Bundle(s) / APK(s)" then "Build APK(s)"
5. After the build completes, click on "Run" in the top menu
6. Select "Run 'app'" to install and run the app

## Option 2: Using ADB (If APK is already built)

If you've already built the app using Android Studio:

1. Run the `install_with_adb.bat` script
2. Make sure your device is connected and USB debugging is enabled
3. The script will install the app on your device

## Testing the Profile Picture Upload

After successfully installing the app:

1. Launch the app and log in
2. Navigate to the Driver Profile screen
3. Tap on your profile picture
4. Grant storage permissions if prompted
5. Select an image from your gallery
6. The image should be uploaded to Cloudinary and your profile picture should be updated

## Troubleshooting

If you encounter any issues:

1. Make sure your device is connected and USB debugging is enabled
2. Try restarting Android Studio
3. Try restarting your device
4. Check the logcat output in Android Studio for any errors
