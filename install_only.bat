@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo FuelTrackerQR Direct Installation Script
echo ===================================================

REM Check if ADB is available
set "ADB_PATH="

if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    echo Found ADB at: !ADB_PATH!
) else (
    echo ADB not found in default location.
    echo Checking alternative locations...
    
    if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
        set "ADB_PATH=%ANDROID_HOME%\platform-tools\adb.exe"
        echo Found ADB at: !ADB_PATH!
    ) else (
        echo ADB not found.
        echo Please make sure Android SDK is installed.
        goto :error
    )
)

REM Check for connected devices
echo Checking for connected devices...
"!ADB_PATH!" devices | findstr /R /C:"device$" > nul
if !ERRORLEVEL! neq 0 (
    echo No Android devices connected.
    echo Please connect a device and try again.
    goto :error
)

REM Check if APK exists
set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
if not exist "!APK_PATH!" (
    echo Debug APK not found at: !APK_PATH!
    echo Please build the app first.
    goto :error
)

REM Install the app
echo Installing the app on connected device...
"!ADB_PATH!" install -r "!APK_PATH!"
if !ERRORLEVEL! neq 0 (
    echo Installation failed.
    goto :error
) else (
    echo App installed successfully!
    echo You can now test the profile picture upload functionality.
    goto :end
)

:error
echo ===================================================
echo Error occurred during installation.
echo ===================================================
echo Alternative method:
echo 1. Open the project in Android Studio
echo 2. Click on "Run" in the top menu
echo 3. Select "Run 'app'" to install and run the app

:end
echo ===================================================
echo Script completed.
echo ===================================================
pause
