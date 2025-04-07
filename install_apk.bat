@echo off
echo ===================================================
echo FuelTrackerQR APK Installation Script
echo ===================================================

REM Check if ADB is available
set "ADB_PATH="

if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    set "ADB_PATH=%ANDROID_HOME%\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else (
    echo ADB not found in common locations.
    echo Please make sure Android SDK is installed.
    goto :error
)

REM Check for connected devices
echo Checking for connected devices...
"%ADB_PATH%" devices
echo.
echo Please make sure your device is listed above and shows as "device".
echo If it shows as "unauthorized", please accept the debugging prompt on your device.
echo If it's not listed, please check your USB connection and make sure USB debugging is enabled.
echo.
pause

REM Install the app
echo Installing the app on connected device...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    "%ADB_PATH%" install -r "app\build\outputs\apk\debug\app-debug.apk"
    if %ERRORLEVEL% neq 0 (
        echo Installation failed.
        goto :error
    ) else (
        echo ===================================================
        echo App installed successfully!
        echo ===================================================
        echo You can now test the profile picture upload functionality.
        goto :end
    )
) else (
    echo Debug APK not found at: app\build\outputs\apk\debug\app-debug.apk
    echo Please build the app first.
    goto :error
)

:error
echo ===================================================
echo Error occurred during installation.
echo ===================================================
echo Please try again after connecting your device properly.

:end
echo ===================================================
echo Script completed.
echo ===================================================
pause
