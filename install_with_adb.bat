@echo off
echo Attempting to install the app using ADB...

REM Try to find ADB
set "ADB_PATH="

if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
) else if exist "%ANDROID_HOME%\platform-tools\adb.exe" (
    set "ADB_PATH=%ANDROID_HOME%\platform-tools\adb.exe"
)

if "%ADB_PATH%"=="" (
    echo ADB not found in common locations.
    echo Please make sure Android SDK is installed.
    pause
    exit /b 1
)

echo Found ADB at: %ADB_PATH%

REM Check for connected devices
echo Checking for connected devices...
"%ADB_PATH%" devices

echo.
echo Please make sure your device is connected and USB debugging is enabled.
echo.
echo Press any key to continue...
pause > nul

REM Check if APK exists
set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
if not exist "%APK_PATH%" (
    echo Debug APK not found at: %APK_PATH%
    echo Please build the app first using Android Studio.
    pause
    exit /b 1
)

REM Install the app
echo Installing the app on connected device...
"%ADB_PATH%" install -r "%APK_PATH%"

if %ERRORLEVEL% neq 0 (
    echo Installation failed.
    echo Please make sure your device is connected and USB debugging is enabled.
) else (
    echo App installed successfully!
    echo You can now test the profile picture upload functionality.
)

pause
