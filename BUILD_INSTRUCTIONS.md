# Building and Installing FuelTrackerQR

This document provides instructions on how to build and install the FuelTrackerQR app.

## Option 1: Using the Provided Scripts

We've created several scripts to help you build and install the app:

### Windows Batch Script (Recommended)

1. Connect your Android device to your computer via USB
2. Enable USB debugging on your device
3. Run the `build_and_install.bat` script by double-clicking it or running it from the command prompt:
   ```
   .\build_and_install.bat
   ```

### PowerShell Script (Alternative)

1. Connect your Android device to your computer via USB
2. Enable USB debugging on your device
3. Right-click on `build_and_install.ps1` and select "Run with PowerShell" or run it from PowerShell:
   ```
   .\build_and_install.ps1
   ```

### Installation Only Script

If you've already built the app and just want to install it:

1. Connect your Android device to your computer via USB
2. Enable USB debugging on your device
3. Run the `install_only.bat` script:
   ```
   .\install_only.bat
   ```

## Option 2: Using Android Studio (If Scripts Fail)

If the scripts don't work for you, you can use Android Studio:

1. Open the project in Android Studio
2. Click on "Build" in the top menu
3. Select "Build Bundle(s) / APK(s)" then "Build APK(s)"
4. After the build completes, click on "Run" in the top menu
5. Select "Run 'app'" to install and run the app

## Option 3: Using Gradle Commands Manually

If you prefer to use Gradle commands directly:

1. Open a command prompt or terminal in the project directory
2. Make sure JAVA_HOME is set correctly
3. Run the following commands:

```
# To build the app
.\gradlew assembleDebug

# To install the app
.\gradlew installDebug
```

## Troubleshooting

If you encounter issues:

1. **Java Not Found**: Make sure you have JDK installed and JAVA_HOME is set correctly
2. **Device Not Found**: Make sure your device is connected and USB debugging is enabled
3. **Build Errors**: Check the error messages and make sure all dependencies are installed
4. **Installation Errors**: Make sure the app is built successfully before trying to install it

## Testing the Profile Picture Upload

After successfully installing the app:

1. Launch the app and log in
2. Navigate to the Driver Profile screen
3. Tap on your profile picture
4. Grant storage permissions if prompted
5. Select an image from your gallery
6. The image should be uploaded to Cloudinary and your profile picture should be updated
